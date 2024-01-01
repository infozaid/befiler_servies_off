package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.EmailNotification;

import java.util.List;

public interface EmailNotificationService {


    boolean save(EmailNotification emailNotification);
    boolean update(EmailNotification emailNotification);

    public List<EmailNotification> getAllActiveRecords();

    public EmailNotification findOne(Integer id);

    public String[] getAllActiveEmails();




}
