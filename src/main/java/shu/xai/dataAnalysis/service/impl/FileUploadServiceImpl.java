package shu.xai.dataAnalysis.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shu.xai.dataAnalysis.entity.MaterialData;
import shu.xai.dataAnalysis.mapper.MaterialDataMapper;
import shu.xai.dataAnalysis.service.FileUploadService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private MaterialDataMapper materialDataMapper;

    @Transactional
    @Override
    public void processFile(MultipartFile file) throws Exception {
        List<MaterialData> dataList = new ArrayList<>();

        // 使用 Apache POI 解析 Excel 文件
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 解析表头（第一行）
            Row headerRow = rowIterator.next();
            Map<Integer, String> columnToFieldMap = new HashMap<>(); // 列与字段映射

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String columnName = cell.getStringCellValue().trim();
                    columnToFieldMap.put(i, convertToFieldName(columnName));
                }
            }

            // 解析数据行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                MaterialData data = new MaterialData();

                for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
                    int columnIndex = entry.getKey();
                    String fieldName = entry.getValue();

                    if ("V_Na(1)O6".equals(fieldName)) {
                        fieldName = "V_Na1_O6";
                    } else if ("V_Na(2)O8".equals(fieldName)) {
                        fieldName = "V_Na2_O8";
                    } else if ("V_Na(3)O5".equals(fieldName)) {
                        fieldName = "V_Na3_O5";
                    }

                    Double value = getCellValueAsDouble(row, columnIndex);
                    if (value != null) {
                        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                        try {
                            Method setter = MaterialData.class.getDeclaredMethod(setterName, Double.class);
                            setter.invoke(data, value);
                        } catch (NoSuchMethodException e) {
                            System.out.println("字段 " + fieldName + " 没有对应的 setter 方法");
                        }
                    }
                }

                dataList.add(data); // 添加到数据列表
            }
        }

        // 调用 Mapper 批量插入数据
        materialDataMapper.insertMaterialData(dataList);
    }

    /**
     * 工具方法：获取单元格的 Double 值
     */
    private Double getCellValueAsDouble(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null; // 返回 null 表示单元格为空
        }
        try {
            return cell.getNumericCellValue(); // 返回数值类型
        } catch (IllegalStateException e) {
            return null; // 非数值类型返回 null
        }
    }

    /**
     * 工具方法：将表头转换为实体类的字段名
     * 例如：表头 "Occu_6b" 转为 "Occu_6b"
     */
    private String convertToFieldName(String columnName) {
        // 如果表头与字段名一致，不需要转换。否则可以添加更多逻辑。
        return columnName.replace(" ", "_"); // 替换空格为下划线（视需求调整）
    }
}
