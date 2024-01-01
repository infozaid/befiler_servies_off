package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantResponse;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationResponse;

public interface FinjaPaymentToMerchantResponseServices {

    FinjaPaymentToMerchantResponse create(FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse) throws Exception;
    FinjaPaymentToMerchantResponse update(FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse) throws Exception;
}
