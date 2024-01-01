package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaConfirm;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaConfirmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EasypaisaConfirmServicesImpl implements EasypaisaConfirmServices {

    private EasypaisaConfirmRepository easypaisaConfirmRepository;

    @Autowired
    public EasypaisaConfirmServicesImpl(EasypaisaConfirmRepository easypaisaConfirmRepository) {
        this.easypaisaConfirmRepository = easypaisaConfirmRepository;
    }

    @Override
    public EasypaisaConfirm saveOrUpdate(EasypaisaConfirm easypaisaConfirm) throws Exception {
        if (easypaisaConfirm != null) {
            return easypaisaConfirmRepository.save(easypaisaConfirm);
        }
        return null;
    }
}
