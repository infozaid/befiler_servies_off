package com.arittek.befiler_services.config.converters;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.sql.*;
import java.util.Base64;

public class NTNEncrypt {
    public static void main(String args[]){
        try {
            // create our mysql database connection
            String myDriver = "com.mysql.cj.jdbc.Driver";
            String myUrl = "jdbc:mysql://localhost:3306/befiler_prod";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "mysql");

            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String query = "SELECT * FROM `fbr_user_account_info` fbr;";

            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next())
            {
                int id = rs.getInt("id");
                String fbrPassword = rs.getString("fbr_password");
                String fbrPin = rs.getString("fbr_pin");

                // print the results
                System.out.format("%s, %s, %s\n", id, fbrPassword, fbrPin);

                query = "update fbr_user_account_info set fbr_password = ?, fbr_pin = ?  where id = ?";
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, fbrPassword != null ? encrypt(fbrPassword): null);
                preparedStmt.setString(2, fbrPin != null ? encrypt(fbrPin): null);
                preparedStmt.setInt   (3, id);

                // execute the java preparedstatement
                preparedStmt.executeUpdate();

            }
            st.close();
            /*System.out.println(encrypt( "12345"));*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    static String encrypt(String attribute) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key secretKey = new SecretKeySpec("X4WJ10OH8LEXSUBA".getBytes(), "AES");
        AlgorithmParameterSpec algorithmParameters = getAlgorithmParameterSpec(cipher);

        callCipherInit(cipher, Cipher.ENCRYPT_MODE, secretKey, algorithmParameters);

        byte[] bytesToEncrypt = attribute.getBytes();
        byte[] encryptedBytes = callCipherDoFinal(cipher, bytesToEncrypt);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    static byte[] callCipherDoFinal(Cipher cipher, byte[] bytes) throws IllegalBlockSizeException, BadPaddingException {
        return cipher.doFinal(bytes);
    }

    static AlgorithmParameterSpec getAlgorithmParameterSpec(Cipher cipher) {
        byte[] iv = new byte[getCipherBlockSize(cipher)];
        return new IvParameterSpec(iv);
    }
    static void callCipherInit(Cipher cipher, int encryptionMode, Key secretKey, AlgorithmParameterSpec algorithmParameters) throws InvalidKeyException, InvalidAlgorithmParameterException {
        cipher.init(encryptionMode, secretKey, algorithmParameters);
    }
    static int getCipherBlockSize(Cipher cipher) {
        return cipher.getBlockSize();
    }
}
