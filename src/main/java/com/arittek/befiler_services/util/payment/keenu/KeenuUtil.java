package com.arittek.befiler_services.util.payment.keenu;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KeenuUtil {

    public static void main(String[] args) {
        System.out.println(generateRequestCheckSum("A494F562DEC04989E937207D2E36A4BC", "180900015400363", "1191826800097", "10.0", "25/09/2018", "14:59:03"));

        System.out.println(generateResponseCheckSum("C738E8AEAFC85D650E92494C341E8611", "181000415604400", "3791831900289", "3000.0", "Askari Bank Limited", "Success", "15/11/2018", "14:12:26"));

        System.out.println("double : " + String.format("%.2f", Double.parseDouble("10.0")));

        System.out.print(verifyCheckSum("C738E8AEAFC85D650E92494C341E8611", "181000415604400", "3791831900289", "3000.00", "Askari Bank Limited", "Success", "15/11/2018", "14:12:26", "caa83f501d50cf30ea50b3290a86a485"));
    }

    public static String generateRequestCheckSum (String secret, String merchantId, String orderNo, String orderAmount, String date, String time) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((secret).getBytes("UTF-8"), "HmacMD5");
            String msg = secret+"|"+merchantId+"|"+orderNo+"|"+orderAmount+"|"+date+"|"+time;
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

    public static String generateResponseCheckSum (String secret, String merchantId, String orderNo, String orderAmount, String bankName, String transactionStatus, String date, String time) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((secret).getBytes("UTF-8"), "HmacMD5");
            String msg = secret+"|"+merchantId+"|"+orderNo+"|"+orderAmount+"|"+bankName+"|"+transactionStatus+"|"+date+"|"+time;
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);

            byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));

            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
        } catch (InvalidKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return digest;
    }

    public static Boolean verifyCheckSum (String secret, String merchantId, String orderNo, String orderAmount, String bankName, String transactionStatus, String date, String time, String checkSum) {
        String newCheckSum = generateResponseCheckSum(secret, merchantId, orderNo, orderAmount, bankName, transactionStatus, date, time);
        System.out.println(checkSum);
        System.out.println(newCheckSum);
        if (newCheckSum.equalsIgnoreCase(checkSum))
            return true;
        else
            return false;
    }

    public static String getKeenuDate() {
        String DATE_FORMAT = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String formatedDate = sdf.format(new Date());
        return formatedDate;
    }

    public static String getKeenuTime(){
        String DATE_FORMAT = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String formatedDate = sdf.format(new Date());
        return formatedDate;
    }
}
