package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMARequest;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMAResponse;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaMARequestRepository;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaMAResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EasypaisaMAServicesImpl implements EasypaisaMAServices {

    private final EasypaisaMARequestRepository easypaisaMARequestRepository;
    private final EasypaisaMAResponseRepository easypaisaMAResponseRepository;

    @Autowired
    public EasypaisaMAServicesImpl(EasypaisaMARequestRepository easypaisaMARequestRepository, EasypaisaMAResponseRepository easypaisaMAResponseRepository) {
        this.easypaisaMARequestRepository = easypaisaMARequestRepository;
        this.easypaisaMAResponseRepository = easypaisaMAResponseRepository;
    }

    @Override
    public EasypaisaMARequest saveOrUpdateRequest(EasypaisaMARequest easypaisaMARequest) throws Exception {
        if (easypaisaMARequest != null) {
            return easypaisaMARequestRepository.save(easypaisaMARequest);
        }
        return null;
    }

    @Override
    public EasypaisaMAResponse saveOrUpdateResponse(EasypaisaMAResponse easypaisaMAResponse) throws Exception {
        if (easypaisaMAResponse != null) {
            return easypaisaMAResponseRepository.save(easypaisaMAResponse);
        }
        return null;
    }
}
