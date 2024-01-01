package com.arittek.befiler_services.util.payment.easypaisa;

import com.arittek.befiler_services.util.CommonUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

public class EasypaisaUtil {

    public static void main(String args[]) throws Exception {
        /*Map<String, String> fields = new TreeMap<String, String>();

        fields.put("storeId", storeId);
        fields.put("amount", amount);
        fields.put("postBackURL", postBackURL);
        fields.put("orderRefNum", orderRefNum);
        fields.put("expiryDate", expiryDate);
        fields.put("autoRedirect", autoRedirect);
        fields.put("paymentMethod", paymentMethod);
        fields.put("emailAddr", emailAddr);
        fields.put("mobileNum", mobileNum);

        System.out.println(fields);

        String value = "";
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            value = value + entry.getKey() + "=" + entry.getValue() + "&";
        }
        value = value.substring(0, value.length() - 1);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec("3QOZMY14XL3IODYM".getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        com.arittek.befiler_services.config.base64.Base64.encode(cipher.doFinal(value.getBytes())));

        System.out.println(com.arittek.befiler_services.config.base64.Base64.encode("info@befiler.com:Arittek@arw786"));*/

        String credentials = "info@befiler.com:Arittek@arw786";
        System.out.println(new String(Base64.encodeBase64(credentials.getBytes())));
    }

    public static String getExpireDate() {
        Date date = CommonUtil.getCurrentTimestamp(30L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        String date1 =  sdf.format(date);
        return date1;
    }

    public static String getExpireDateForOTPTransaction() {
        Date date = CommonUtil.getCurrentTimestamp(1440L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        String date1 =  sdf.format(date);
        return date1;
    }

    public static String getCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return new String(Base64.encodeBase64(credentials.getBytes()));
    }

    public static String getMerchantHashedRequest(String hashKey, String storeId, String amount, String postBackURL, String orderRefNum, String expiryDate, String autoRedirect, String paymentMethod, String emailAddr, String mobileNum) throws Exception{
        Map<String, String> fields = new HashMap<String, String>();

        fields.put("amount", amount);
        fields.put("storeId", storeId);
        fields.put("postBackURL", postBackURL);
        fields.put("orderRefNum", orderRefNum);
        fields.put("paymentMethod", paymentMethod);
        fields.put("expiryDate", expiryDate);
        fields.put("autoRedirect", autoRedirect);
        fields.put("emailAddr", emailAddr);
        fields.put("mobileNum", mobileNum);

        /*List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);*/

        Map<String, String> fieldNames = new TreeMap<String, String>(fields);
        /*Map<String, String> fieldNames = new TreeMap<String, String>(fields);*/
        System.out.println(fieldNames);

        String value = "";
        for (Map.Entry<String, String> entry : fieldNames.entrySet()) {
            value = value + entry.getKey() + "=" + entry.getValue() + "&";
        }
        System.out.println(value);
        value = value.substring(0, value.length() - 1);
        System.out.println(value);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        SecretKeySpec secretKey = new SecretKeySpec("3QOZMY14XL3IODYM".getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new String(Base64.encodeBase64(cipher.doFinal(value.getBytes())));
        /*return "";*/
    }

    public static String mobileNo(String mobileNo) {
        if (mobileNo.matches("0\\d{10}")) {
            return mobileNo;
        } else if (mobileNo.matches("0\\d{3}-\\d{7}")) {
            return mobileNo.replace("-", "");
        } else if (mobileNo.matches("92\\d{10}")) {
            mobileNo = mobileNo.replaceFirst("92", "0");
            return mobileNo;
        } else if (mobileNo.matches("92\\d{3}-\\d{7}")) {
            mobileNo = mobileNo.replaceFirst("92", "0");
            return mobileNo.replace("-", "");
        }
        return null;
    }
}
