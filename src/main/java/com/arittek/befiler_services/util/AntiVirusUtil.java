package com.arittek.befiler_services.util;/*
package com.arittek.util;

import com.av.AVClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntiVirusUtil {

    static String avMode = "SCANREPAIR";

    static boolean scan = true;

    static String avServer = "192.168.100.37";


    static int avPort = 1344;


    public static void main(String[] args) {

        try {


//                File file = new File("F:\\Acitivities\\eicar_com.zip");
            File file = new File("D:\\UPLOADED_FOLDER\\eicar_com.zip");
            byte[] bs = Files.readAllBytes(file.toPath());
            System.out.println( scanFile(bs, file.getName()));

        } catch (IOException ex) {
            Logger.getLogger(com.arittek.util.AntiVirusUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(com.arittek.util.AntiVirusUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean scanFile(byte[] fileBytes, String fileName)  throws IOException, Exception {

        if (scan) {
            AVClient avc = new AVClient(avServer, avPort, avMode);
//                System.out.println("Scan: "+avc.scanfile(fileName, fileBytes));
            System.out.println("Scan: "+avc.scanfile("D:\\UPLOADED_FOLDER\\eicar_com.zip","D:\\Cheeta\\bhardo_tax_services_2\\output.txt"));

            if (avc.scanfile(fileName, fileBytes) == -1) {
                return true;
            }
        }
        return false;
    }



}
*/
