package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.TermsAndConditions;
import com.arittek.befiler_services.repository.TermsAndConditionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TermsAndConditionsServicesImpl implements TermsAndConditionsServices {

    @Autowired
    private TermsAndConditionsRepository termsAndConditionsRepository;

    @Override
    public TermsAndConditions findOneById(Integer termsAndConditionsId) {
        if (termsAndConditionsId != null) {
            return termsAndConditionsRepository.findById(termsAndConditionsId).orElse(null);
        }
        return null;
    }

    @Override
    public TermsAndConditions findByTypeAndStatus(Integer type, Integer status) {
        if (type != null && status != null) {
            return termsAndConditionsRepository.findByTypeAndStatus(type, status);
        }
        return null;
    }
}
