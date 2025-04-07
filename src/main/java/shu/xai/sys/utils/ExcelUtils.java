package shu.xai.sys.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Iterator;

/**
 * Created by yuziyi on 2023/2/27.
 */
public class ExcelUtils {


    /**
     * 读取Excel解析为json格式
     *
     * @param filePath 路径
     * @return JSONArray
     */
    public static JSONObject readExcel(String filePath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray1=new JSONArray();
        JSONArray jsonArray2=new JSONArray();
        Workbook workbook = null;
        CSVReader csvReader=null;
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(filePath);

        if (filePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (filePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else {
            //csv
            csvReader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(inputStream, "utf-8"))).build();
        }
        if(filePath.endsWith(".csv")) {
            int i = 0;
            String[] title=null;
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                if (i >= 1) {
//                    System.out.println(next[0]);
                    JSONObject tmp = new JSONObject();
                    for (int j=0;j<next.length;j++) {
                        tmp.put(title[j],next[j]);
                    }
                    jsonArray2.add(tmp);
                }else {
//                    System.out.println(next[0]);
                    title=next;
                    for (String aNext : next) {
                        JSONObject tmp = new JSONObject();
                        tmp.put("name",aNext);
                        tmp.put("label",aNext);
                        jsonArray1.add(tmp);
                    }
                }
                i++;
            }
        }else {
            Sheet sheet = workbook.getSheetAt(0);
            Row firstRow = sheet.getRow(sheet.getFirstRowNum());
            int CellNum = firstRow.getLastCellNum();
            for (int j = 0; j < CellNum; j++) {
                String fistRowCellValue = firstRow.getCell(j).toString();
                JSONObject tmp = new JSONObject();
                tmp.put("name",fistRowCellValue);
                tmp.put("label",fistRowCellValue);
                jsonArray1.add(tmp);
            }
            int lastRowNum = sheet.getLastRowNum();
//            System.out.println(lastRowNum);
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                int lastCellNum = row.getLastCellNum();
                JSONObject tmp = new JSONObject();
                for (int j = 0; j < lastCellNum; j++) {
                    String fistRowCellValue = firstRow.getCell(j).toString();
                    String value = row.getCell(j).toString();
                    tmp.put(fistRowCellValue,value);;
                }
                jsonArray2.add(tmp);
            }
            workbook.close();
            inputStream.close();
        }
        jsonObject.put("tableColumn",jsonArray1);
        jsonObject.put("tableData",jsonArray2);

        return jsonObject;
    }
}
