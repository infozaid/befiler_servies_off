package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationResponse;

public interface IpgRegistrationResponseServices {

    IpgRegistrationResponse saveOrUpdate(IpgRegistrationResponse ipgRegistrationResponse) throws Exception;
}
