package com.arittek.befiler_services.services.payment.keenu;

import com.arittek.befiler_services.model.payment.keenu.KeenuResponse;
import com.arittek.befiler_services.repository.payment.keenu.KeenuResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeenuResponseServicesImpl implements KeenuResponseServices{

    private KeenuResponseRepository keenuResponseRepository;

    @Autowired
    public KeenuResponseServicesImpl(KeenuResponseRepository keenuResponseRepository) {
        this.keenuResponseRepository = keenuResponseRepository;
    }

    @Override
    public KeenuResponse saveOrUpdate(KeenuResponse keenuResponse) throws Exception {
        if (keenuResponse != null) {
            return keenuResponseRepository.save(keenuResponse);
        }
        return null;
    }
}
