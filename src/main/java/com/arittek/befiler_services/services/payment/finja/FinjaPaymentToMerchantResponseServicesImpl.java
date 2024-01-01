package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantResponse;
import com.arittek.befiler_services.repository.payment.finja.FinjaPaymentToMerchantResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinjaPaymentToMerchantResponseServicesImpl implements FinjaPaymentToMerchantResponseServices {

    private FinjaPaymentToMerchantResponseRepository finjaPaymentToMerchantResponseRepository;

    @Autowired
    public FinjaPaymentToMerchantResponseServicesImpl(FinjaPaymentToMerchantResponseRepository finjaPaymentToMerchantResponseRepository) {
        this.finjaPaymentToMerchantResponseRepository = finjaPaymentToMerchantResponseRepository;
    }

    @Override
    public FinjaPaymentToMerchantResponse create(FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse) throws Exception {
        if (finjaPaymentToMerchantResponse != null) {
            return finjaPaymentToMerchantResponseRepository.save(finjaPaymentToMerchantResponse);
        }
        return null;
    }

    @Override
    public FinjaPaymentToMerchantResponse update(FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse) throws Exception {
        if(finjaPaymentToMerchantResponse.getFinjaGetCustomerInfoRequestId() != null){
            FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse1 = finjaPaymentToMerchantResponseRepository.findById(finjaPaymentToMerchantResponse.getFinjaGetCustomerInfoRequestId()).orElse(null);
            if(finjaPaymentToMerchantResponse1 != null){
                return finjaPaymentToMerchantResponseRepository.save(finjaPaymentToMerchantResponse);
            }
        }
        return null;
    }
}
