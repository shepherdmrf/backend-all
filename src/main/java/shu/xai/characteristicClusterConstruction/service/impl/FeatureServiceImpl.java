package shu.xai.characteristicClusterConstruction.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import shu.xai.characteristicClusterConstruction.entity.ExcelRowData;
import shu.xai.characteristicClusterConstruction.entity.Feature;
import shu.xai.characteristicClusterConstruction.entity.KeyKnowledgeFeature;
import shu.xai.characteristicClusterConstruction.mapper.FeatureMapper;
import shu.xai.characteristicClusterConstruction.service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shu.xai.材料.service.CallPython.CallPythonScript;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import java.sql.*;

@Service
public class FeatureServiceImpl implements FeatureService {

    @Autowired
    FeatureMapper featureMapper;

    @Override
    public List<Feature> getMLRData() {
        List<Feature> result = featureMapper.findDataMLR();
        System.out.println("查询结果数量：" + result.size());
        if (!result.isEmpty()) {
            System.out.println("第一个结果：" + result.get(0));
        }
        return result;
    }

    @Override
    public List<Feature> getKNNData() {
        return featureMapper.findDataKNN();
    }

    @Override
    public List<Feature> getSVRData() {
        return featureMapper.findDataSVR();
    }

    @Override
    public List<Feature> getGPRData() {
        return featureMapper.findDataGPR();
    }

    @Override
    public List<KeyKnowledgeFeature> getKeyKnowledgeByModel(String model) {
        return getKnowledgeFeaturesByModel(model);
    }

    public List<Feature> getFeaturesByModel(String model) {
        List<Feature> features = new ArrayList<>();

        switch (model) {
            case "mlr":
                features = featureMapper.findDataMLR();
                break;
            case "knn":
                features = featureMapper.findDataKNN();
                break;
            case "svr":
                features = featureMapper.findDataSVR();
                break;
            case "gpr":
                features = featureMapper.findDataGPR();
                break;
            default:
                throw new IllegalArgumentException("Unsupported model: " + model);
        }

        return features;
    }

    public List<KeyKnowledgeFeature> getKnowledgeFeaturesByModel(String model) {
        List<KeyKnowledgeFeature> features = new ArrayList<>();

        switch (model) {
            case "mlr":
                features = featureMapper.findKnowledgeMLR();
                break;
            case "knn":
                features = featureMapper.findKnowledgeKNN();
                break;
            case "svr":
                features = featureMapper.findKnowledgeSVR();
                break;
            case "gpr":
                features = featureMapper.findKnowledgeGPR();
                break;
            default:
                throw new IllegalArgumentException("Unsupported model: " + model);
        }

        return features;
    }


    @Override
    public List<Map<String, Object>> processAndHandleExcel(String model) {

        List<Feature> features1 = getFeaturesByModel(model);
        List<KeyKnowledgeFeature> features2 = getKnowledgeFeaturesByModel(model);

        try {
            // 创建 ObjectMapper 实例
            ObjectMapper objectMapper = new ObjectMapper();

            // 将 features1 和 features2 转换为 JSON 字符串
            String jsonFeatures1 = objectMapper.writeValueAsString(features1);
            String jsonFeatures2 = objectMapper.writeValueAsString(features2);

            System.out.println("jsonFeatures1: " + jsonFeatures1);
            System.out.println("jsonFeatures2: " + jsonFeatures2);

            // 调用 Python 脚本并传递参数
            CallPythonScript.CallKnowPosKernal(jsonFeatures1, jsonFeatures2, model);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String excelFilePath = "./src/main/resources/python/MultifacetedModeling/Results/OnAllTrainKernel/DKNCOR/Know+Pos-Kernel/" + model + ".xlsx";

        // 存储所有查询的结果
        List<Map<String, Object>> allData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            // 遍历每个 Sheet
            boolean firstNonEmptySheetFound = false;
            int j = 0;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++, j++) {
                Sheet sheet = workbook.getSheetAt(i);

                // 检查当前 Sheet 是否为空
                if (isSheetEmpty(sheet)) {
                    System.out.println("Skipping empty sheet: " );
                    j--;
                    continue;  // 如果当前 Sheet 为空，跳过该 Sheet
                }

                // 找到第一个非空的 Sheet 后开始处理
                if (!firstNonEmptySheetFound) {
                    System.out.println("Processing first non-empty sheet: " );
                    firstNonEmptySheetFound = true;  // 标记已找到第一个非空的 Sheet
                } else {
                    System.out.println("Processing sheet");
                }

                String tableName = model + j;

                // 处理当前 Sheet
                processSheet(sheet, tableName);

                // 查询该表的数据并将其与 Sheet 标识符一起添加到结果列表
                List<ExcelRowData> sheetData = queryStoredData(tableName);

                for (ExcelRowData row : sheetData) {
                    System.out.println(row);
                }

                // 为每个 Sheet 创建一个独立的 Map，区分不同的 Sheet
                if(j != 10) {
                    Map<String, Object> sheetResult = new HashMap<>();
                    sheetResult.put("sheetName", sheet.getSheetName()); // 或者使用 sheet.getSheetName()
                    sheetResult.put("sheetData", sheetData);  // 将数据放入 "sheetData" 键中

                    allData.add(sheetResult);  // 将包含 Sheet 数据的 Map 添加到 allData 列表中
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 返回所有的表数据，包含区分的每个 Sheet
        return allData;
    }

    private boolean isSheetEmpty(Sheet sheet) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell != null && cell.toString().trim().length() > 0) {
                    return false;
                }
            }
        }
        return true;
    }


    private List<ExcelRowData> queryStoredData(String s) {
        return featureMapper.queryDataByTable(s);
    }


    public void processSheet(Sheet sheet, String tableName) {
        Iterator<Row> rowIterator = sheet.iterator();

        // 获取列名（假设列名在第一行）
        Row headerRow = rowIterator.next();
        StringBuilder columns = new StringBuilder();
        for (Cell cell : headerRow) {
            columns.append(cell.getStringCellValue()).append(",");
        }
        columns.deleteCharAt(columns.length() - 1); // 去除最后的逗号

        // 存储从 Excel 中读取的数据
        List<ExcelRowData> excelRowDataList = new ArrayList<>();

        // 在插入数据之前清空表
        featureMapper.clearTable(tableName);

        // 创建一个 DecimalFormat 实例，指定格式保留两位小数
        DecimalFormat df = new DecimalFormat("#.####");

        // 为每行数据生成唯一 ID
        int idCounter = 1; // 从 1 开始计数

        // 遍历每一行数据并构建 ExcelRowData 对象
        // 创建一个 DataFormatter 实例
        DataFormatter dataFormatter = new DataFormatter();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            ExcelRowData rowData = new ExcelRowData();

            // 如果 tableName 包含 "10"，则为每行生成 ID
            if (tableName.contains("10")) {
                rowData.setId(idCounter++);
            }

            // 使用 DataFormatter 获取单元格的字符串值
            rowData.setFeatures(dataFormatter.formatCellValue(row.getCell(0)));

            // 处理其他列数据，直接获取格式化后的字符串
            rowData.setLength(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(1)), df));
            rowData.setScore(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(2)), df));
            rowData.setCvRmse(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(3)), df));
            rowData.setRmse(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(4)), df));
            rowData.setR2(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(5)), df));
            rowData.setTime(formatIfNumeric(dataFormatter.formatCellValue(row.getCell(6)), df));

            // 将每行数据添加到列表中
            excelRowDataList.add(rowData);
        }


        // 批量插入数据
        if (!excelRowDataList.isEmpty()) {
            if (tableName.contains("10")) {
                // 执行带有 ID 的插入
                featureMapper.insertBatchWithId(excelRowDataList, tableName);
            } else {
                // 执行不带 ID 的普通插入
                featureMapper.insertBatch(excelRowDataList, tableName);
            }
        }
    }



    // 辅助方法：检查字符串是否可以转换为数字，并格式化数字
    private String formatIfNumeric(String cellValue, DecimalFormat df) {
        try {
            // 尝试将字符串转换为数字
            double numericValue = Double.parseDouble(cellValue);
            // 如果转换成功，则格式化为保留两位小数
            return df.format(numericValue);
        } catch (NumberFormatException e) {
            // 如果不是有效的数字，则返回原始字符串
            return cellValue;
        }
    }

    public List<ExcelRowData> processBestAndStoreInExcel(String model, String features) {
        String tableName = model + 0;

        List<ExcelRowData> allMaxData = new ArrayList<>();

        // 现有 Excel 文件路径
        String excelFilePath = "./src/main/resources/python/MultifacetedModeling/Results/OnAllTrainKernel/DKNCOR/Know+Pos-Kernel/" + model + ".xlsx";

        // 读取现有的 Excel 文件
        FileInputStream fis = null;
        Workbook workbook = null;
        try {
            fis = new FileInputStream(excelFilePath);
            workbook = WorkbookFactory.create(fis);  // 打开现有文件
        } catch (IOException e) {
            e.printStackTrace();
        }

        String sheetName = "best";
        Sheet existingSheet = workbook.getSheet(sheetName);

        if (existingSheet != null) {
            // 删除已有的 Sheet
            int sheetIndex = workbook.getSheetIndex(existingSheet);
            workbook.removeSheetAt(sheetIndex);
        }

        // 创建新的 Sheet
        Sheet newSheet = workbook.createSheet(sheetName);

        // 创建表头
        Row headerRow = newSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Features");
        headerRow.createCell(1).setCellValue("Length");
        headerRow.createCell(2).setCellValue("Score");
        headerRow.createCell(3).setCellValue("CV RMSE");
        headerRow.createCell(4).setCellValue("RMSE");
        headerRow.createCell(5).setCellValue("R2");
        headerRow.createCell(6).setCellValue("Time");

        int rowIndex = 1;  // Excel 文件中的行号

        String tableName1 = "best" + model;
        featureMapper.clearTable(tableName1);

        int idCounter = 1;
        // 遍历每个表

        // 查询该表中 CV RMSE 最大的行
        ExcelRowData maxCvRmseRow = featureMapper.queryBestRowByFeatures(tableName, features);

        maxCvRmseRow.setId(idCounter++);


        if (maxCvRmseRow != null) {
            // 将该行插入到数据库中的一个新的表，用于存储最大 CV RMSE 数据

            featureMapper.insertIntoMaxCvRmseTable(maxCvRmseRow, tableName1);

            // 将该行数据存入 Excel 文件
            Row newRow = newSheet.createRow(rowIndex++);
            newRow.createCell(0).setCellValue(maxCvRmseRow.getFeatures());
            newRow.createCell(1).setCellValue(maxCvRmseRow.getLength());
            newRow.createCell(2).setCellValue(maxCvRmseRow.getScore());
            newRow.createCell(3).setCellValue(maxCvRmseRow.getCvRmse());
            newRow.createCell(4).setCellValue(maxCvRmseRow.getRmse());
            newRow.createCell(5).setCellValue(maxCvRmseRow.getR2());
            newRow.createCell(6).setCellValue(maxCvRmseRow.getTime());

//                // 将数据存储到结果列表中
//                Map<String, Object> maxDataMap = new HashMap<>();
//                maxDataMap.put("features", maxCvRmseRow.getFeatures());
//                maxDataMap.put("length", maxCvRmseRow.getLength());
//                maxDataMap.put("score", maxCvRmseRow.getScore());
//                maxDataMap.put("cvRmse", maxCvRmseRow.getCV_RMSE());
//                maxDataMap.put("rmse", maxCvRmseRow.getRmse());
//                maxDataMap.put("r2", maxCvRmseRow.getR2());
//                maxDataMap.put("time", maxCvRmseRow.getTime());
//
//                allMaxData.add(maxDataMap);  // 将结果数据加入列表
        }


        allMaxData = featureMapper.queryDataByTable(tableName1);

        // 保存修改后的 Excel 文件
        try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("数据处理完成，已保存到原始文件，添加了一个 'best' sheet");

        // 返回最大 CV RMSE 数据给前端
        return allMaxData;
    }

    public File generateFeatureVenn(String model, int id) {
        try {
            // 根据模型和 ID 获取特征数据
            List<Feature> features1 = getFeaturesByModel(model);
            List<KeyKnowledgeFeature> features2 = getKnowledgeFeaturesByModel(model);
            String tablename = "best" + model;
            String features3 = featureMapper.findBestById(tablename, id);

            // 转换 features2 为 JSON 格式
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonFeatures1 = objectMapper.writeValueAsString(features1);
            String jsonFeatures2 = objectMapper.writeValueAsString(features2);

            // 调用 Python 脚本
            CallPythonScript.CallFeatureVenn(jsonFeatures1, jsonFeatures2, features3);

            // 假设 Python 脚本生成的图片文件路径如下
            String imagePath = "./src/main/resources/python/MultifacetedModeling/venn_diagram.png";

            // 检查生成的文件是否存在
            File generatedImage = new File(imagePath);
            if (!generatedImage.exists()) {
                throw new RuntimeException("Python script did not generate the image.");
            }

            // 返回生成的图片文件
            return generatedImage;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while generating the Venn diagram", e);
        }
    }


}