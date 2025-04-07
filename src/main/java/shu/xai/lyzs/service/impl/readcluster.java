package shu.xai.lyzs.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class readcluster {
    public List<Map<String, String>> readFirstAndLastColumns(String filePath) {
        List<Map<String, String>> jsonData = new ArrayList<>();
//        List<List<String>> columnData = new ArrayList<>();
//        try {
//            InputStream excelFile = new FileInputStream(filePath);
//            Workbook workbook = WorkbookFactory.create(excelFile);
//
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                List<String> rowData = new ArrayList<>();
//                Cell firstCell2 = row.getCell(0);
//                if (firstCell2 != null) {
//                    rowData.add(firstCell2.toString());
//                }
//                Cell firstCell = row.getCell(1);
//                if (firstCell != null) {
//                    rowData.add(firstCell.toString());
//                }
//                Cell firstCell1 = row.getCell(2);
//                if (firstCell1 != null) {
//                    rowData.add(firstCell1.toString());
//                }
//                Cell lastCell = row.getCell(row.getLastCellNum() - 1);
//                if (lastCell != null) {
//                    rowData.add(lastCell.toString());
//                }
//
//                columnData.add(rowData);
//            }
        try (InputStream inp = new FileInputStream(filePath)) {
            Workbook workbook = new XSSFWorkbook(inp);
            Sheet sheet = workbook.getSheetAt(0); // 假设只有一个工作表

            Row headerRow = sheet.getRow(0); // 第一行作为表头

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    String cellValue = "";
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        if (cell.getCellType() == CellType.STRING) {
                            cellValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            cellValue = String.valueOf(cell.getNumericCellValue());
                        }
                    }
                    String headerValue = "";
                    Cell headercell=headerRow.getCell(j);
                    if(headercell!=null){
                        if (headercell.getCellType() == CellType.STRING) {
                            headerValue = headercell.getStringCellValue();
                        } else if (headercell.getCellType() == CellType.NUMERIC) {
                            headerValue = String.valueOf(cell.getNumericCellValue());
                        }
                    }
//                    String headerValue = headerRow.getCell(j).getStringCellValue();
                    rowData.put(headerValue, cellValue);
//                    rowData.put(headerRow.getCell(j).getStringCellValue(), cellValue);
                }
                jsonData.add(rowData);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonData;
    }

}
