package com.arittek.befiler_services.util.email;

import com.arittek.befiler_services.model.email.EmailLog;
import com.arittek.befiler_services.services.EmailLogServices;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MyPrint;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EmailSender {

    private static Properties fMailServerConfig = new Properties();

    private static EmailLogServices emailLogServices;

    public static void setEmailLogServices(EmailLogServices emailLogServices) {
        EmailSender.emailLogServices = emailLogServices;
    }

    public static Boolean sendEmail(String body, String subject, String[] recipients) throws Exception {
        if (recipients != null && recipients.length != 0) {
            Boolean emailSend = EmailSender.sendFromGMail(subject, body, recipients);
            if (emailSend) emailLogServices.save(subject, body, 1, recipients);
            else emailLogServices.save(subject, body, 0, recipients);
            return emailSend;
        }
        return true;
    }

    public static Boolean sendEmail(String body, String subject, String recipients) throws Exception {
        Boolean emailSend = EmailSender.sendFromGMail(subject, body, recipients);
        EmailLog emailLog = new EmailLog(recipients, subject, body);
        if (emailSend) emailLog.setEmailStatus(1);
        else emailLog.setEmailStatus(0);
        emailLogServices.save(emailLog);
        return emailSend;
    }

    public static Boolean sendEmail(String body, String subject, String recipients, List<String> paths) throws Exception {
        Boolean emailSend = EmailSender.sendFromGMail(subject, body, recipients, paths);
        EmailLog emailLog = new EmailLog(recipients, subject, body, String.join("##", paths));
        if (emailSend) emailLog.setEmailStatus(1);
        else emailLog.setEmailStatus(0);
        emailLogServices.save(emailLog);
        return emailSend;
    }


    public static Boolean sendFromGMail(String subject, String body, String to[]) throws Exception {

        try {
            Session session = Session.getInstance(fMailServerConfig, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fMailServerConfig.getProperty("mail.smtp.userFrom"), fMailServerConfig.getProperty("mail.smtp.userFromPassword"));
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fMailServerConfig.getProperty("aliasUserFromName"), "BeFiler - Your Tax Consultant"));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            MyPrint.println("To Length :" + to.length);
            for (int i = 0; i < to.length; i++) {
                toAddress[i] = new InternetAddress(to[i].trim());
                MyPrint.println("for email : " + toAddress[i]);
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setText(body);
            message.setContent(body, "text/html");
            Transport transport = session.getTransport("smtp");
            transport.connect(fMailServerConfig.getProperty("mail.smtp.host"), fMailServerConfig.getProperty("mail.smtp.userFrom"), fMailServerConfig.getProperty("mail.smtp.userFromPassword"));
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
            Logger4j.getLogger().error("Exception:", ae);
            return false;
        } catch (MessagingException me) {
            me.printStackTrace();
            Logger4j.getLogger().error("Exception:", me);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
            EmailSender.sendExceptionByEmail(e);
            return false;
        }

        return true;
    }

    public static Boolean sendFromGMail(String subject, String body, String to) throws Exception {


        Session session = Session.getInstance(fMailServerConfig, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fMailServerConfig.getProperty("mail.smtp.userFrom"), fMailServerConfig.getProperty("mail.smtp.userFromPassword"));
            }
        });
        session.getProperties().put("mail.smtp.ssl.trust", fMailServerConfig.getProperty("mail.smtp.host"));    // by maqsood


        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fMailServerConfig.getProperty("aliasUserFromName"), "BeFiler - Your Tax Consultant"));
            InternetAddress toAddress = new InternetAddress(to);

            message.addRecipient(Message.RecipientType.TO, toAddress);

            message.setSubject(subject);
            message.setText(body);
            message.setContent(body, "text/html");
            Transport transport = session.getTransport("smtp");

            transport.connect
                    (
                            fMailServerConfig.getProperty("mail.smtp.host"),
                            fMailServerConfig.getProperty("mail.smtp.userFrom"),
                            fMailServerConfig.getProperty("mail.smtp.userFromPassword")

                    );
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (AddressException ae) {
            ae.printStackTrace();
            Logger4j.getLogger().error("Exception:", ae);
            return false;
        } catch (MessagingException me) {
            me.printStackTrace();
            Logger4j.getLogger().error("Exception:", me);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
            EmailSender.sendExceptionByEmail(e);
            return false;
        }

        return true;
    }

    public static Boolean sendFromGMail(String subject, String body, String to, List<String> paths) throws Exception {

        // creates a new session with an authenticator
        Session session = Session.getInstance(fMailServerConfig, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fMailServerConfig.getProperty("mail.smtp.userFrom"), fMailServerConfig.getProperty("mail.smtp.userFromPassword"));
            }
        });

        // creates a new e-mail message
        Message message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(fMailServerConfig.getProperty("aliasUserFromName"), "BeFiler - Your Tax Consultant"));
            InternetAddress toAddress = new InternetAddress(to);

            message.addRecipient(Message.RecipientType.TO, toAddress);

            message.setSubject(subject);
            message.setText(body);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // adds attachments
            if (paths != null && paths.size() > 0) {
                for (String filePath : paths) {
                    MimeBodyPart attachPart = new MimeBodyPart();
                    try {
                        attachPart.attachFile(filePath);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    multipart.addBodyPart(attachPart);
                }
            }
            // sets the multi-part as e-mail's content
            message.setContent(multipart);


            Transport transport = session.getTransport("smtp");

            transport.connect
                    (
                            fMailServerConfig.getProperty("mail.smtp.host"),
                            fMailServerConfig.getProperty("mail.smtp.userFrom"),
                            fMailServerConfig.getProperty("mail.smtp.userFromPassword")

                    );
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (AddressException ae) {
            ae.printStackTrace();
            Logger4j.getLogger().error("Exception:", ae);
            return false;
        } catch (MessagingException me) {
            me.printStackTrace();
            Logger4j.getLogger().error("Exception:", me);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
            EmailSender.sendExceptionByEmail(e);
            return false;
        }

        return true;
    }


    public static boolean sendExceptionByEmail(Exception exception) {
        try {
            String recipeints[] = {fMailServerConfig.getProperty("RECIPIENT")};
            EmailSender.sendFromGMail("PMS - " + exception.toString(), org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exception), recipeints);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Email Exception", e);
        }
        return false;
    }

    static {
        fetchConfig();
    }

    private static void fetchConfig() {
        //This file contains the javax.mail config properties mentioned above.
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            fMailServerConfig.load(loader.getResourceAsStream("mail.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger4j.getLogger().error("Exception:", ex);
            MyPrint.errln("Cannot open and load mail server properties file.");
        }
    }
}
