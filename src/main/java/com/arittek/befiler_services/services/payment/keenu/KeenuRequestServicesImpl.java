package com.arittek.befiler_services.services.payment.keenu;

import com.arittek.befiler_services.model.payment.keenu.KeenuRequest;
import com.arittek.befiler_services.repository.payment.keenu.KeenuRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeenuRequestServicesImpl implements KeenuRequestServices{

    private KeenuRequestRepository keenuRequestRepository;

    @Autowired
    public KeenuRequestServicesImpl(KeenuRequestRepository keenuRequestRepository) {
        this.keenuRequestRepository = keenuRequestRepository;
    }

    @Override
    public KeenuRequest saveOrUpdate(KeenuRequest keenuRequest) throws Exception {
        return keenuRequestRepository.save(keenuRequest);
    }
}
