package com.arittek.befiler_services.services.payment.promo_code;

import com.arittek.befiler_services.repository.payment.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoCodeAssignServicesServicesImpl implements PromoCodeAssignServices {

    private PromoCodeRepository promoCodeRepository;

    @Autowired
    public PromoCodeAssignServicesServicesImpl(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }
}
