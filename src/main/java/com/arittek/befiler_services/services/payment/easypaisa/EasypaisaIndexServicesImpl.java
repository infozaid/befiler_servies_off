package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIndex;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EasypaisaIndexServicesImpl implements EasypaisaIndexServices {

    private final EasypaisaIndexRepository easypaisaIndexRepository;

    @Autowired
    public EasypaisaIndexServicesImpl(EasypaisaIndexRepository easypaisaIndexRepository) {
        this.easypaisaIndexRepository = easypaisaIndexRepository;
    }

    @Override
    public EasypaisaIndex saveOrUpdate(EasypaisaIndex easypaisaIndex) throws Exception {
        if (easypaisaIndex != null) {
            return easypaisaIndexRepository.save(easypaisaIndex);
        }
        return null;
    }
}
