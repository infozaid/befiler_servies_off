package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.email.EmailLog;

import java.util.List;

public interface EmailLogServices {

    EmailLog save(EmailLog emailLog) throws Exception;
    Boolean save(String emailSubject, String emailBody, Integer emailStatus, String[] emailAddresses) throws Exception;

    EmailLog findOneById(Integer id) throws Exception;

    List<EmailLog> findAll() throws Exception;
}
