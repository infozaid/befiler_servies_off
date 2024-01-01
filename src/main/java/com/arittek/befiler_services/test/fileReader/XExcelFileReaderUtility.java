package com.arittek.befiler_services.test.fileReader;/*
package com.arittek.fileReader;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XExcelFileReaderUtility {

    public static void main(String args[]) {
        */
/*try {
            String path = "D://2018917129712233ATL_IT.xlsx";

            XExcelFileReader xExcelFileReader = new XExcelFileReader(path, 1);

            List<String[]> rows =  xExcelFileReader.readRows(400000);
            for (String[] row : rows) {
                System.out.println(row[0] + " " + row[1] + " " + row[2]);
                System.out.println();
                System.out.println("--------------------");
                System.out.println();
                *//*
*/
/*for (String data : row) {
                    System.out.println(data);
                }*//*
*/
/*
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*//*


        try {
            */
/*InputStream is = new FileInputStream(new File("D://2018827981843494ATL_IT.xlsx"));
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);

            for (Sheet sheet : workbook){
                System.out.println(sheet.getSheetName());
                for (Row r : sheet) {
                    for (Cell c : r) {
                        System.out.println(c.getStringCellValue());
                    }
                    System.out.println("-----------------------------");

                }
            }*//*


            try (BufferedReader br = Files.newBufferedReader(Paths.get("D://2018924992727350ATL_IncomeTax.csv"), StandardCharsets.UTF_8)) {
                for (String line = null; (line = br.readLine()) != null;) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                   // InputStream or File for XLSX file (required)

    }
}
*/
