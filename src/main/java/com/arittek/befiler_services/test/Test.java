package com.arittek.befiler_services.test;/*
package com.arittek.test;

import com.arittek.fbr.FbrActiveTaxpayer;
import com.arittek.fileReader.XExcelFileReader;
import com.arittek.util.CommonUtil;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class Test {
    public static void main(String args[]){
        */
/*System.out.println(generateSignature());
        System.out.println(generateSignature().toLowerCase());*//*


        try {
            String path = "D://2018917129712233ATL_IT.xlsx";
            */
/*InputStream is = new FileInputStream(new File(path));

            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(50)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(1024)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);

            long countRow = 0;
            for (Sheet sheet : workbook) {
                System.out.println(sheet.getSheetName());
                for (Row r : sheet) {
                    System.out.println(r.getCell(1).getStringCellValue());
                    System.out.println(r.getCell(2).getStringCellValue());
                    System.out.println(r.getCell(3).getStringCellValue());
                    System.out.println(countRow++);
                }
            }*//*


            XExcelFileReader xExcelFileReader = new XExcelFileReader(path, 1);

            List<String[]> rows =  xExcelFileReader.readRows(400000);
            for (String[] row : rows) {
                System.out.println(row[0] + " " + row[1] + " " + row[2]);
                System.out.println();
                System.out.println("--------------------");
                System.out.println();
                */
/*for (String data : row) {
                    System.out.println(data);
                }*//*

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

*/
