package com.arittek.befiler_services.test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class SendEmailTest {


    private static Properties fMailServerConfig = new Properties();

    /*public static void sendEmail(String to)
    {
        final String username = "notification@befiler.com";
        final String password = "z[]o7@S4ClIi";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.befiler.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress("notification@befiler.com","Befiler"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("Notification");
            String messages= EmailUtil.emailTemaplate("befiler.com", "Kirat Kumar");
            message.setContent(messages, "text/html");

            Transport.send(message);

            System.out.println("Done");

        }

        catch (MessagingException e)
        {
            // throw new RuntimeException(e);
            System.out.println("Username or Password are incorrect ... exiting !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void sendEmail(String to)
    {
        final String username = "befiler.com@gmail.com";
        final String password = "Arittek1";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress("notification@befiler.com","z[]o7@S4ClIi"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            message.setSubject("Notification");
            String messages= "This is test mail";
            message.setContent(messages, "text/html");

            Transport.send(message);

            System.out.println("Done");

        }

        catch (MessagingException e)
        {
            e.printStackTrace();
            // throw new RuntimeException(e);
            System.out.println("Username or Password are incorrect ... exiting !");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args)
    {

    }
}