package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIPN;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaIPNRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EasypaisaIPNServicesImpl implements EasypaisaIPNServices {

    private final EasypaisaIPNRepository easypaisaIPNRepository;

    @Autowired
    public EasypaisaIPNServicesImpl(EasypaisaIPNRepository easypaisaIPNRepository) {
        this.easypaisaIPNRepository = easypaisaIPNRepository;
    }

    @Override
    public EasypaisaIPN saveOrUpdate(EasypaisaIPN easypaisaIPN) throws Exception {
        if (easypaisaIPN != null) {
            return easypaisaIPNRepository.save(easypaisaIPN);
        }
        return null;
    }
}
