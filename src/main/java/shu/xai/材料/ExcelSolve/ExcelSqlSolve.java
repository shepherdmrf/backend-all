package shu.xai.材料.ExcelSolve;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.sql.*;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.File;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.io.IOException;

@Component
public class ExcelSqlSolve {
    @Resource(name = "jdbcTemplatefeaturetable")
    private JdbcTemplate jdbcTemplatefeature;

    @Resource(name = "jdbcTemplateRule")
    private JdbcTemplate jdbcTemplateRule;

    // 清空表并重置自增序号
    public void clearTable(String tableName) throws Exception {
        // 使用 JdbcTemplate 执行 SQL 语句
        try {
            String clearTableSQL = "TRUNCATE TABLE " + tableName;
            jdbcTemplatefeature.update(clearTableSQL);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        // 重置自增序号
        try {
            String resetAutoIncrementSQL = "ALTER TABLE " + tableName + " AUTO_INCREMENT = 1";
            jdbcTemplatefeature.update(resetAutoIncrementSQL);
            System.out.println("表 " + tableName + " 已清空，序号已重置！");
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    public void clear(String tableName) throws Exception {
        try {
            String clearTableSQL = "TRUNCATE TABLE " + tableName;
            jdbcTemplatefeature.update(clearTableSQL);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    public void RuleClear(String tableName) throws Exception {
        try {
            String clearTableSQL = "TRUNCATE TABLE " + tableName;
            jdbcTemplateRule.update(clearTableSQL);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    // 从 Excel 文件插入数据到数据库
    public void insertExcelDataToSQL(String excelFilePath, String insertSQL, String sheetName, int groupValue) throws Exception {
        // 读取 Excel 文件
        try (FileInputStream file = new FileInputStream(excelFilePath)) {
            Workbook workbook = new XSSFWorkbook(file);

            // 通过名称指定工作表
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("指定的工作表 " + sheetName + " 不存在！");
                return;
            }

            // 使用 PreparedStatement 执行插入操作
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // 跳过表头，从第二行开始
                Row row = sheet.getRow(i);
                if (row != null) {
                    jdbcTemplatefeature.update(connection -> {
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);

                        // 遍历 Excel 单元格，填充列值
                        int columnIndex = 1; // PreparedStatement 的列索引从 1 开始
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null) {
                                pstmt.setNull(columnIndex++, Types.NULL);
                            } else {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        pstmt.setString(columnIndex++, cell.getStringCellValue().trim());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            pstmt.setDate(columnIndex++, new java.sql.Date(cell.getDateCellValue().getTime()));
                                        } else {
                                            pstmt.setDouble(columnIndex++, cell.getNumericCellValue());
                                        }
                                        break;
                                    case BOOLEAN:
                                        pstmt.setBoolean(columnIndex++, cell.getBooleanCellValue());
                                        break;
                                    default:
                                        pstmt.setNull(columnIndex++, Types.NULL);
                                        break;
                                }
                            }
                        }

                        // 设置额外的 group 列值
                        pstmt.setInt(columnIndex, groupValue); // group 列为最后一列

                        return pstmt;
                    });
                }
            }

            System.out.println("数据已成功插入到数据库！");
        }
    }

    public void insertExcelDataToSQL(String excelFilePath, String insertSQL, String sheetName) throws Exception {
        try (FileInputStream file = new FileInputStream(excelFilePath)) {
            Workbook workbook = new XSSFWorkbook(file);

            // 通过名称指定工作表
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("指定的工作表 " + sheetName + " 不存在！");
                return;
            }

            // 使用 PreparedStatement 执行插入操作
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // 跳过表头，从第二行开始
                Row row = sheet.getRow(i);
                if (row != null) {
                    jdbcTemplatefeature.update(connection -> {
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);

                        // 遍历 Excel 单元格，填充列值
                        int columnIndex = 1; // PreparedStatement 的列索引从 1 开始
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null) {
                                pstmt.setNull(columnIndex++, Types.NULL);
                            } else {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        pstmt.setString(columnIndex++, cell.getStringCellValue().trim());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            pstmt.setDate(columnIndex++, new java.sql.Date(cell.getDateCellValue().getTime()));
                                        } else {
                                            pstmt.setDouble(columnIndex++, cell.getNumericCellValue());
                                        }
                                        break;
                                    case BOOLEAN:
                                        pstmt.setBoolean(columnIndex++, cell.getBooleanCellValue());
                                        break;
                                    default:
                                        pstmt.setNull(columnIndex++, Types.NULL);
                                        break;
                                }
                            }
                        }
                        return pstmt;
                    });
                }
            }
            System.out.println("数据已成功插入到数据库！");
        }
    }

    public void RuleinsertExcelDataToSQL(String excelFilePath, String insertSQL, String sheetName) throws Exception {
        try (FileInputStream file = new FileInputStream(excelFilePath)) {
            Workbook workbook = new XSSFWorkbook(file);

            // 通过名称指定工作表
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("指定的工作表 " + sheetName + " 不存在！");
                return;
            }

            // 使用 PreparedStatement 执行插入操作
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) { // 跳过表头，从第二行开始
                Row row = sheet.getRow(i);
                if (row != null) {
                    jdbcTemplateRule.update(connection -> {
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);

                        // 遍历 Excel 单元格，填充列值
                        int columnIndex = 1; // PreparedStatement 的列索引从 1 开始
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cell == null) {
                                pstmt.setNull(columnIndex++, Types.NULL);
                            } else {
                                switch (cell.getCellType()) {
                                    case STRING:
                                        pstmt.setString(columnIndex++, cell.getStringCellValue().trim());
                                        break;
                                    case NUMERIC:
                                        if (DateUtil.isCellDateFormatted(cell)) {
                                            pstmt.setDate(columnIndex++, new java.sql.Date(cell.getDateCellValue().getTime()));
                                        } else {
                                            pstmt.setDouble(columnIndex++, cell.getNumericCellValue());
                                        }
                                        break;
                                    case BOOLEAN:
                                        pstmt.setBoolean(columnIndex++, cell.getBooleanCellValue());
                                        break;
                                    default:
                                        pstmt.setNull(columnIndex++, Types.NULL);
                                        break;
                                }
                            }
                        }
                        return pstmt;
                    });
                }
            }
            System.out.println("数据已成功插入到数据库！");
        }
    }

    private static String[] toStringArray(double[][] array) {
        List<String> result = new ArrayList<>();
        for (double[] subArray : array) {
            for (double value : subArray) {
                result.add(String.valueOf(value));
            }
        }
        return result.toArray(new String[0]);
    }

    public void insertSignToDatabase() {
        FileInputStream fis = null;
        String EXCEL_FILE_PATH = "./src/main/resources/python/MultifacetedModeling/DataInput/uploadedFile.xlsx";
        try {
            clearTable("sign_table");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("清除表出错: " + e.getMessage());
        }
        try {
            // 打开 Excel 文件
            File excelFile = new File(EXCEL_FILE_PATH);
            fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Row firstRow = sheet.getRow(0); // 获取第一行数据

            // 遍历第一行数据并插入数据库
            String sql = "INSERT INTO sign_table (value, name) VALUES (?, ?)";
            for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
                Cell cell = firstRow.getCell(i);
                if (cell != null) {
                    // 获取 Excel 单元格的值，转换为字符串
                    String cellValue = cell.toString();

                    // 插入到数据库中
                    jdbcTemplatefeature.update(sql, i, cellValue); // i 作为 value，cellValue 作为 name
                }
            }
            System.out.println("数据插入成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("读取 Excel 文件时出错: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("插入数据时出错: " + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String creatSqledecision_table() {
        // Excel 文件路径
        String excelFilePath = "./src/main/resources/python/MultifacetedModeling/DataInput/uploadedFile.xlsx"; // 获取绝对路径
        List<String> models = Arrays.asList("gpr_", "knn_", "mlr_", "svr_");
        String insertSQL = " (";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // 获取 Excel 第一个 sheet
            Sheet sheet = workbook.getSheetAt(0);

            // 获取第一行作为列名
            Row headerRow = sheet.getRow(0);
            List<String> columnNames = new ArrayList<>();

            // 读取列名
            for (int i = 0; i < Math.min(45, headerRow.getPhysicalNumberOfCells()); i++) {
                columnNames.add(headerRow.getCell(i).getStringCellValue());
            }

            // 生成 INSERT INTO SQL 语句的列部分
            for (String columnName : columnNames) {
                insertSQL += "`" + columnName + "`, ";  // 使用反引号包围列名，防止特殊字符问题
            }
            insertSQL += "`d_class`) VALUES (";

            // 生成 VALUES 部分，假设你准备填充的参数是?，每列一个
            for (int i = 0; i < 46; i++) {
                insertSQL += "?, ";  // 每个列名对应一个参数
            }
            insertSQL = insertSQL.substring(0, insertSQL.length() - 2); // 去掉最后的 ", "
            insertSQL += ")";

            // 创建表的 SQL 语句
            for (String model : models) {
                StringBuilder drop = new StringBuilder("DROP TABLE `" + model + "decision_table`");
                jdbcTemplatefeature.execute(drop.toString());
                StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + model + "decision_table` (");
                for (String columnName : columnNames) {
                    String columnType = "TINYINT"; // 默认数据类型
                    createTableSQL.append("`").append(columnName).append("` ").append(columnType).append(", ");
                }
                createTableSQL.append("`d_class` TINYINT);"); // 添加 `d_class` 列

                // 执行创建表操作
                jdbcTemplatefeature.execute(createTableSQL.toString());
                System.out.println("表格创建成功！");
            }

        } catch (IOException e) {
            e.printStackTrace();  // 可以替换为更详细的日志记录
        }

        // 返回生成的 INSERT SQL 语句
        return insertSQL;
    }
}


