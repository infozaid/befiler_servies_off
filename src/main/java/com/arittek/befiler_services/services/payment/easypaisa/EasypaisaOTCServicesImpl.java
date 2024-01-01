package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCRequest;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCResponse;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaOTCRequestRepository;
import com.arittek.befiler_services.repository.payment.easypaisa.EasypaisaOTCResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EasypaisaOTCServicesImpl implements EasypaisaOTCServices{

    private final EasypaisaOTCRequestRepository easypaisaOTCRequestRepository;
    private final EasypaisaOTCResponseRepository easypaisaOTCResponseRepository;

    @Autowired
    public EasypaisaOTCServicesImpl(EasypaisaOTCRequestRepository easypaisaOTCRequestRepository, EasypaisaOTCResponseRepository easypaisaOTCResponseRepository) {
        this.easypaisaOTCRequestRepository = easypaisaOTCRequestRepository;
        this.easypaisaOTCResponseRepository = easypaisaOTCResponseRepository;
    }

    @Override
    public EasypaisaOTCRequest saveOrUpdateRequest(EasypaisaOTCRequest easypaisaOTCRequest) throws Exception {
        if (easypaisaOTCRequest != null) {
            return easypaisaOTCRequestRepository.save(easypaisaOTCRequest);
        }
        return null;
    }

    @Override
    public EasypaisaOTCResponse saveOrUpdateResponse(EasypaisaOTCResponse easypaisaOTCResponse) throws Exception {
        if (easypaisaOTCResponse != null) {
            return easypaisaOTCResponseRepository.save(easypaisaOTCResponse);
        }
        return null;
    }
}
