package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.email.EmailConfig;

public interface EmailConfigServices {
     EmailConfig findByActiveStatus()throws Exception;

}
