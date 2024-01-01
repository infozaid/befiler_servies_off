package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.TermsAndConditions;

public interface TermsAndConditionsServices {
    TermsAndConditions findOneById(Integer termsAndConditionsId);
    TermsAndConditions findByTypeAndStatus(Integer type, Integer status);
}
