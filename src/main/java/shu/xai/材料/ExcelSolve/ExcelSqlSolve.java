package shu.xai.材料.ExcelSolve;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public void RuleinsertExcelDataToSQL(String excelFilePath, String insertSQL, String sheetName, List<Integer> selectedColumns, int startrow) throws Exception {
        try (FileInputStream file = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取指定的 Sheet
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("指定的工作表 " + sheetName + " 不存在！");
                return;
            }

            // 遍历 Excel 行
            for (int i = startrow; i < sheet.getPhysicalNumberOfRows(); i++) { // 跳过表头，从第二行开始
                Row row = sheet.getRow(i);
                if (row != null) {
                    jdbcTemplateRule.update(connection -> {
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                        int columnIndex = 1; // PreparedStatement 索引从 1 开始

                        // **只处理 selectedColumns 指定的列**
                        for (int col : selectedColumns) {
                            Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
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
        } catch (Exception e) {
            System.err.println("导入数据时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void copyRow(Row sourceRow, Row targetRow, int startCol) {
        if (sourceRow == null || targetRow == null) {
            return;
        }

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell != null) {
                Cell targetCell = targetRow.createCell(startCol + i);
                // 复制单元格内容
                switch (sourceCell.getCellType()) {
                    case STRING:
                        targetCell.setCellValue(sourceCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        targetCell.setCellValue(sourceCell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        targetCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        targetCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    default:
                        targetCell.setCellValue("");
                }
            }
        }
    }

    public void ServerExcel(String excelFilePath, String outFilePath, String sheetName, String outname, List<Integer> list, int startrow) throws Exception {
        try (FileInputStream file = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(file)) {

            // 获取指定的 Sheet
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.out.println("指定的工作表 " + sheetName + " 不存在！");
                return;
            }
            Workbook destWorkbook = new XSSFWorkbook();
            Sheet outSheet = destWorkbook.createSheet(outname);

            // 先复制表头行，并在第一列添加"ID"标题
            Row headerRow = outSheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");  // 添加ID列标题
            copyRow(sheet.getRow(0), headerRow, 1);  // 从第二列开始复制原表头

            // 切割 Excel 行
            Integer newRowIndex = 1;
            for (int i = 0; i < list.size(); i++) {
                int actualRow = list.get(i) + startrow;
                Row row = sheet.getRow(actualRow);
                if (row != null) {
                    Row newRow = outSheet.createRow(newRowIndex++);
                    // 在第一列添加ID值
                    newRow.createCell(0).setCellValue(list.get(i));
                    // 复制原始行内容
                    copyRow(row, newRow, 1);  // 从第二行开始复制
                }
            }

            // 写入新文件
            try (FileOutputStream fos = new FileOutputStream(outFilePath)) {
                destWorkbook.write(fos);
            }
            System.out.println("成功提取 " + (newRowIndex ) + " 行到: " + outFilePath);
        } catch (Exception e) {
            System.err.println("切割数据时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
    // 读取最后一行数据为List<Double>
    public List<Double> readLastRow(String filePath, String sheetName) throws Exception {
        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row lastRow = sheet.getRow(sheet.getLastRowNum());

            List<Double> result = new ArrayList<>();
            for (int i = 0; i < lastRow.getLastCellNum(); i++) {
                result.add(parseCell(lastRow.getCell(i)));
            }
            return result;
        }
    }
    public List<Double> readFirstColumn(String filePath, String sheetName) throws Exception {
        try (FileInputStream file = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheet(sheetName);
            List<Double> result = new ArrayList<>(sheet.getLastRowNum()); // 预分配容量

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                result.add(parseCell(sheet.getRow(i).getCell(0)));
            }
            return result;
        }
    }

    private static double parseCell(Cell cell) {
        if (cell == null) return Double.NaN;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return round(cell.getNumericCellValue());
                case STRING:
                    return round(Double.parseDouble(cell.getStringCellValue().trim()));
                case BOOLEAN:
                    return cell.getBooleanCellValue() ? 1.0 : 0.0;
                default:
                    return Double.NaN;
            }
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    // 保留4位小数
    private static double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }

    public void StarticToWorkSquare(String choice)
    {
        List<String>Models=new ArrayList<>();
        Models.add("KNN");
        Models.add("MLR");
        Models.add("SVR");
        Models.add("GPR");
        // 假设目标文件路径和choice.xlsx的路径需要定义
        String targetFileRootPath = "./src/main/resources/python/MultifacetedModeling/Results/OnTrain/DKNCOR/"; // 替换为实际目标文件路径
        String choiceFileRootPath = "src/main/resources/python/MultifacetedModeling/DataPreProcessing/" ; // 替换为choice.xlsx所在路径
        for (String item:Models)
        {
            String targetFilePath=targetFileRootPath+item+".xlsx";
            String chooseFilePath=choiceFileRootPath+choice+item+".xlsx";
            try {
                // 获取目标文件对象
                File targetFile = new File(targetFilePath);
                String originalFileName = Paths.get(targetFilePath).getFileName().toString();
                // 如果目标文件存在，则删除
                if (targetFile.exists()) {
                    if (!targetFile.delete()) {
                        System.err.println("无法删除目标文件: " + targetFilePath);
                        return;
                    }
                }
                // 构建choice.xlsx的源路径
                Path sourcePath = Paths.get(chooseFilePath);

                // 检查源文件是否存在
                if (!Files.exists(sourcePath)) {
                    System.err.println("源文件不存在: " + chooseFilePath);
                    return;
                }
                // 复制choice.xlsx到目标路径并重命名为原文件名
                Path targetPath = Paths.get(targetFilePath);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("文件替换成功: " + originalFileName);

            } catch (IOException e) {
                System.err.println("处理文件时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


