package com.arittek.befiler_services.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class EncoderDecoder {
    public static String getEncryptedSHA1Password(String pwd){
        try{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String password=pwd;
        InputStream fis = new ByteArrayInputStream(password.getBytes());

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();


        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println(sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        return(hexString.toString());


    }
    catch(Exception e){
            e.printStackTrace();
            return null;
    }
        }
}
