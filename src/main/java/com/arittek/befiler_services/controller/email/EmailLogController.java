package com.arittek.befiler_services.controller.email;

import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailLogController {

  /*  @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.userFrom}")
    private String userFrom;

    @Value("${mail.smtp.userFromPassword}")
    private String userFromPassword;

    @Value("${mail.smtp.auth}")
    private String auth;
*/

  /*  public static void sendEmail(String userEmail) {

        String host = "smtp.gmail.com";//or IP address
        String to = userEmail;//change accordingly
        String from = "befilertech@gmail.com";//change accordingly
        String password = "UkGc871cP$5Z";//change accordingly

        //Get the session object
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
//        properties.put("mail.smtp.socketFactory.port", "587");
//        properties.put("mail.smtp.socketFactory.class",
//                "javax.net.ssl.SSLSocketFactory");

        properties.put("mail.smtp.port", "587");;
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", password);

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,password);
                    }
                });
        *//*session.getProperties().put("mail.smtp.ssl.trust", "smtp.gmail.com");
        session.getProperties().put("mail.smtp.starttls.enable", "true");
*//*

        //compose the message
        try{
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
//            message.setRecipients(Message.RecipientType.TO,
//                    InternetAddress.parse(to));
            message.setSubject("Ignore");
            message.setText("Hello, this is from befiler automatic email gernator of sending email");

//            Transport transport = session.getTransport("smtp");
//
//            transport.connect(from, password);
//            transport.sendMessage(message, message.getAllRecipients());
//            transport.close();

            // Send message
            Transport.send(message);
            System.out.println("message sent successfully....");

        }catch (MessagingException e) {throw new RuntimeException(e);}
    }*/

//    public static void main(String[] args){
//
//        EmailLogController.sendEmail("amaqsood68@gmail.com");
//    }

}
