package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.repository.corporateRepository.CorporateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorporateServicesImpl implements CorporateServices {

    private CorporateRepository corporateRepository;

    @Autowired
    CorporateServicesImpl(CorporateRepository corporateRepository) {
        this.corporateRepository = corporateRepository;
    }

    @Override
    public Corporate findOneByIdAndStatus(Integer corporateId, AppStatus status) throws Exception {
        if (corporateId != null && status != null) {
            return corporateRepository.findOneByIdAndStatus(corporateId, status);
        }
        return null;
    }

    @Override
    public Corporate saveOrUpdate(Corporate corporate) throws Exception {
        if(corporate != null){
            return corporateRepository.save(corporate);
        }
        return null;
    }

    @Override
    public List<Corporate> findAllByStatus(AppStatus status) throws Exception {
        if (status != null) {
            return corporateRepository.findAllByStatus(status);
        }
        return null;
    }
}
