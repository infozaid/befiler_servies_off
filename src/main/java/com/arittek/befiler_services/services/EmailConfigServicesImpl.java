package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.email.EmailConfig;
import com.arittek.befiler_services.repository.EmailConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailConfigServicesImpl implements EmailConfigServices{

    @Autowired
    EmailConfigRepository emailConfigRepository;


    @Override
    public EmailConfig findByActiveStatus() throws Exception {
        return emailConfigRepository.findByStatus(1);
    }
}
