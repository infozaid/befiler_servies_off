package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface IpgRegistrationRequestServices {

    IpgRegistrationRequest findOne(Integer id);

    IpgRegistrationRequest saveOrUpdate(IpgRegistrationRequest ipgRegistrationRequest) throws Exception;

    List<IpgRegistrationRequest> findAll()throws Exception;
    List<IpgRegistrationRequest> findAllByUser(User user)throws Exception;
}
