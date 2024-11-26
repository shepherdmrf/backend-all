package shu.xai.材料.ExcelSolve;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.sql.*;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
public class ExcelSqlSolve {
    @Resource(name = "jdbcTemplatefeaturetable")
    private JdbcTemplate jdbcTemplatefeature;

    // 清空表并重置自增序号
    public void clearTable(String tableName) throws Exception {
        // 使用 JdbcTemplate 执行 SQL 语句
        String clearTableSQL = "TRUNCATE TABLE " + tableName;
        jdbcTemplatefeature.update(clearTableSQL);

        // 重置自增序号
        String resetAutoIncrementSQL = "ALTER TABLE " + tableName + " AUTO_INCREMENT = 1";
        jdbcTemplatefeature.update(resetAutoIncrementSQL);

        System.out.println("表 " + tableName + " 已清空，序号已重置！");
    }

    // 从 Excel 文件插入数据到数据库
    public void insertExcelDataToSQL(String excelFilePath, String insertSQL, String sheetName) throws Exception {
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
            for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) { // 跳过表头，从第二行开始
                Row row = sheet.getRow(i);
                if (row != null) {
                    // 使用 JdbcTemplate 的 update 方法来执行插入
                    jdbcTemplatefeature.update(connection -> {
                        PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                        // 假设我们知道 Excel 中每列的顺序，可以动态获取列的值并插入
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            switch (cell.getCellType()) {
                                case STRING:
                                    pstmt.setString(j + 1, cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    pstmt.setDouble(j + 1, cell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    pstmt.setBoolean(j + 1, cell.getBooleanCellValue());
                                    break;
                                default:
                                    pstmt.setNull(j + 1, Types.NULL);
                                    break;
                            }
                        }
                        return pstmt;
                    });
                }
            }

            System.out.println("数据已成功插入到数据库！");
        }
    }
}
