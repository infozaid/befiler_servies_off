/*
package com.arittek.befiler_services.test.poi;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;

public class ReadDocument {

    public static void main(String args[]) {
        // READING PARAGRAPHS
        */
/*try {
            File file = new File("D:\\workspaces\\befiler\\poi\\2017831181435412IncomeTaxOrdinance2001updatedupto30.06.2017.doc");
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());

            HWPFDocument doc = new HWPFDocument(fis);

            WordExtractor we = new WordExtractor(doc);

            String[] paragraphs = we.getParagraphText();

            System.out.println("Total no of paragraph "+paragraphs.length);
            for (String para : paragraphs) {
                System.out.println(para.toString());
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*//*


        // Reading Headers and Footers
        try {
            FileInputStream fis = new FileInputStream("D:\\workspaces\\befiler\\poi\\2017831181435412IncomeTaxOrdinance2001updatedupto30.06.2017.docx");
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
            XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(xdoc);

            XWPFHeader header = policy.getDefaultHeader();
            if (header != null) {
                System.out.println(header.getText());
            }

            XWPFFooter footer = policy.getDefaultFooter();
            if (footer != null) {
                System.out.println(footer.getText());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
*/
