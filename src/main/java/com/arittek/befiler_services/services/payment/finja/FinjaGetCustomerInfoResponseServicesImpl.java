package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoResponse;
import com.arittek.befiler_services.repository.payment.finja.FinjaGetCustomerInfoResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinjaGetCustomerInfoResponseServicesImpl implements FinjaGetCustomerInfoResponseServices {

    private FinjaGetCustomerInfoResponseRepository finjaGetCustomerInfoResponseRepository;

    @Autowired
    public FinjaGetCustomerInfoResponseServicesImpl(FinjaGetCustomerInfoResponseRepository finjaGetCustomerInfoResponseRepository) {
        this.finjaGetCustomerInfoResponseRepository = finjaGetCustomerInfoResponseRepository;
    }

    @Override
    public FinjaGetCustomerInfoResponse create(FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse) throws Exception {
        if (finjaGetCustomerInfoResponse != null) {
            return finjaGetCustomerInfoResponseRepository.save(finjaGetCustomerInfoResponse);
        }
        return null;
    }

    @Override
    public FinjaGetCustomerInfoResponse update(FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse) throws Exception {
        if(finjaGetCustomerInfoResponse.getFinjaGetCustomerInfoRequestId() != null){
            FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse1 =  finjaGetCustomerInfoResponseRepository.findById(finjaGetCustomerInfoResponse.getFinjaGetCustomerInfoRequestId()).orElse(null);
            if(finjaGetCustomerInfoResponse1 != null){
                return finjaGetCustomerInfoResponseRepository.save(finjaGetCustomerInfoResponse);
            }
        }
        return null;
    }
}
