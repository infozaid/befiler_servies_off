package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantRequest;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;

public interface FinjaPaymentToMerchantRequestServices {

    FinjaPaymentToMerchantRequest saveOrUpdate(FinjaPaymentToMerchantRequest finjaPaymentToMerchantRequest) throws Exception;
}
