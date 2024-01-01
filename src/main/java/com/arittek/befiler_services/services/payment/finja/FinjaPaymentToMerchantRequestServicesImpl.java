package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantRequest;
import com.arittek.befiler_services.repository.payment.finja.FinjaPaymentToMerchantRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinjaPaymentToMerchantRequestServicesImpl implements FinjaPaymentToMerchantRequestServices{

    private FinjaPaymentToMerchantRequestRepository finjaPaymentToMerchantRequestRepository;

    @Autowired
    public FinjaPaymentToMerchantRequestServicesImpl(FinjaPaymentToMerchantRequestRepository finjaPaymentToMerchantRequestRepository) {
        this.finjaPaymentToMerchantRequestRepository = finjaPaymentToMerchantRequestRepository;
    }

    @Override
    public FinjaPaymentToMerchantRequest saveOrUpdate(FinjaPaymentToMerchantRequest finjaPaymentToMerchantRequest) throws Exception {
        if (finjaPaymentToMerchantRequest != null && finjaPaymentToMerchantRequest.getFinjaGetCustomerInfoRequest() != null) {
            return finjaPaymentToMerchantRequestRepository.save(finjaPaymentToMerchantRequest);
        }
        return null;
    }
}
