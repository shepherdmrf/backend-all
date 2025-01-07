package shu.xai.dataAnalysis.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void processFile(MultipartFile file) throws Exception {
        // 存储 Excel 表头到字段的映射
        Map<Integer, String> columnToFieldMap = new HashMap<>();
        String tableName = "data.data2"; // 动态生成的表名，可以根据需要修改

        // 使用 Apache POI 解析 Excel 文件
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 解析表头（第一行）
            Row headerRow = rowIterator.next();

            // 动态创建数据库表
            StringBuilder dropTableSql = new StringBuilder("DROP TABLE IF EXISTS " + tableName);
            System.out.println("执行 SQL 删除表: " + dropTableSql.toString());
            jdbcTemplate.execute(dropTableSql.toString()); // 执行删除表的 SQL

            StringBuilder createTableSql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String columnName = cell.getStringCellValue().trim();
                    System.out.println("原始列名: " + columnName); // 打印原始列名
                    String fieldName = convertToFieldName(columnName);
                    System.out.println("转换后的字段名（驼峰格式）: " + fieldName); // 打印转换后的字段名

                    // 处理特殊字段名
                    fieldName = handleSpecialFieldNames(fieldName);
                    System.out.println("最终字段名（处理特殊情况后）: " + fieldName); // 打印最终字段名

                    columnToFieldMap.put(i, fieldName);

                    // 根据字段名生成 SQL，假设所有字段为 VARCHAR 类型（可以根据需求进一步改进）
                    createTableSql.append(fieldName).append(" VARCHAR(255), ");
                }
            }

            createTableSql.setLength(createTableSql.length() - 2);
            createTableSql.append(")");

            System.out.println("生成的 CREATE TABLE SQL: " + createTableSql.toString());

            jdbcTemplate.execute(createTableSql.toString());

            // 解析数据行并插入数据
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<Object> values = new ArrayList<>();

                for (Map.Entry<Integer, String> entry : columnToFieldMap.entrySet()) {
                    int columnIndex = entry.getKey();
                    String fieldName = entry.getValue();

                    // 字段名特殊处理，避免字段名和实际类属性不一致
                    fieldName = handleSpecialFieldNames(fieldName);

                    // 获取单元格值
                    Double value = getCellValueAsDouble(row, columnIndex);
                    if (value != null) {
                        values.add(value); // 将数据添加到 values 列表中
                    } else {
                        values.add(null); // 对应列为空时，插入 NULL
                    }
                }

                // 插入数据到数据库
                insertDataIntoTable(tableName, columnToFieldMap, values);
            }
        }
    }

    // 插入数据到动态创建的表
    private void insertDataIntoTable(String tableName, Map<Integer, String> columnToFieldMap, List<Object> values) {
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder valueSql = new StringBuilder("VALUES (");

        // 动态构建字段名和插入的值
        for (String field : columnToFieldMap.values()) {
            insertSql.append(field).append(", ");
            valueSql.append("?, ");
        }

        // 去掉最后的逗号
        insertSql.setLength(insertSql.length() - 2);
        valueSql.setLength(valueSql.length() - 2);

        insertSql.append(") ").append(valueSql).append(")");

        // 使用 jdbcTemplate 执行插入操作
        jdbcTemplate.update(insertSql.toString(), values.toArray());
    }

    // 处理特殊字段名转换
    private String handleSpecialFieldNames(String fieldName) {
        switch (fieldName) {
            case "V_Na(1)O6":
                return "V_Na1_O6";
            case "V_Na(2)O8":
                return "V_Na2_O8";
            case "V_Na(3)O5":
                return "V_Na3_O5";
            default:
                return fieldName;
        }
    }

    // 获取单元格值并转换为 Double
    private Double getCellValueAsDouble(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    System.out.println("无法将字符串转换为数字: " + cell.getStringCellValue());
                    return null;
                }
            default:
                return null;
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