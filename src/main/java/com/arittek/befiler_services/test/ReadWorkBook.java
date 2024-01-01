/*
package com.arittek.befiler_services.test;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

*/
/**
 * Created by Arittek on 10/16/2017.
 *//*

public class ReadWorkBook {
    public static  void main(String arg[])throws  Exception {
        File file = new File("D:\\SMC_Members_Data_Training_SCDP_CMP.xlsx");
        FileInputStream in = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row;
        if (sheet != null) {
            Iterator<Row> rowIterator = sheet.iterator();

            MyPrint.println(": District :"+"\t"+": Talka :");
            while (rowIterator.hasNext()) {
                row = (XSSFRow) rowIterator.next();
                MyPrint.println(row.getCell(1)+"\t"+row.getCell(2));

            }
        }

    }
}
*/
