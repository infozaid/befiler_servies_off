package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoResponse;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationResponse;

public interface FinjaGetCustomerInfoResponseServices {
    FinjaGetCustomerInfoResponse create(FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse) throws Exception;
    FinjaGetCustomerInfoResponse update(FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse) throws Exception;
}
