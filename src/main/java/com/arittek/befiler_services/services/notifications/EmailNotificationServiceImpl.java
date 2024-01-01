package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.notifications.EmailNotification;
import com.arittek.befiler_services.repository.notifications.EmailNotificationRepository;
import org.apache.http.impl.bootstrap.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {

    EmailNotificationRepository emailNotificationRepository;

    @Autowired
    public EmailNotificationServiceImpl(EmailNotificationRepository emailNotificationRepository) {
        this.emailNotificationRepository = emailNotificationRepository;
    }

    @Override
    public boolean save(EmailNotification emailNotification) {
        if (emailNotification != null) {
            emailNotificationRepository.save(emailNotification);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(EmailNotification emailNotification) {
        if (emailNotification != null) {
            emailNotificationRepository.save(emailNotification);
            return true;
        }
        return false;
    }

    @Override
    public List<EmailNotification> getAllActiveRecords() {
        return emailNotificationRepository.findAllByStatus(AppStatus.ACTIVE);
    }

    @Override
    public EmailNotification findOne(Integer id) {
        return emailNotificationRepository.findById(id).get();
    }

    @Override
    public String[] getAllActiveEmails(){
        List<EmailNotification> emailNotificationList = emailNotificationRepository.findAllByStatus(AppStatus.ACTIVE);
        if (emailNotificationList != null && !emailNotificationList.isEmpty()) {
            String emails[] = new String[emailNotificationList.size()];
            int i = 0;
            for (EmailNotification emailNotification : emailNotificationList) {
                emails[i++] = emailNotification.getEmail();
            }
            return emails;
        }
        return null;
    }
}
