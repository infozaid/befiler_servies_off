package com.arittek.befiler_services.config.static_context_initializr;

import com.arittek.befiler_services.services.EmailLogServices;
import com.arittek.befiler_services.util.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {

    @Autowired
    private EmailLogServices emailLogServices;

    @PostConstruct
    public void init() {
        EmailSender.setEmailLogServices(emailLogServices);
    }

}
