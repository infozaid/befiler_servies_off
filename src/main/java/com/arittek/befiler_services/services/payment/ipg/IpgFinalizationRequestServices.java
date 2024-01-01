package com.arittek.befiler_services.services.payment.ipg;


import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;

public interface IpgFinalizationRequestServices {

    IpgFinalizationRequest saveOrUpdate(IpgFinalizationRequest ipgFinalizationRequest) throws Exception;


}
