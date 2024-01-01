package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.email.EmailLog;
import com.arittek.befiler_services.repository.EmailLogRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailLogServicesImpl implements EmailLogServices {

    private final EmailLogRepository emailLogRepository;

    @Autowired
    public EmailLogServicesImpl(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    public EmailLog save(EmailLog emailLog) throws Exception {
        if (emailLog != null) {
            return emailLogRepository.save(emailLog);
        }
        return null;
    }


    @Override
    public Boolean save(String emailSubject, String emailBody, Integer emailStatus, String[] emailAddresses) throws Exception {
        if (emailAddresses != null) {
            for (int i=0; i<emailAddresses.length; i++) {
                EmailLog emailLog = new EmailLog(emailAddresses[i], emailStatus, emailSubject, emailBody);
                emailLogRepository.save(emailLog);
            }
            return true;
        }
        return false;
    }

    @Override
    public EmailLog findOneById(Integer id) throws Exception {
        if (id != null) {
            return emailLogRepository.findById(id).orElse(null);
        }
        return null;
    }

    @Override
    public List<EmailLog> findAll() throws Exception {
        return (List<EmailLog>) emailLogRepository.findAll();
    }
}
