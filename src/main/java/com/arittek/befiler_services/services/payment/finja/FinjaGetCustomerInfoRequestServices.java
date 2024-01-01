package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface FinjaGetCustomerInfoRequestServices {

    FinjaGetCustomerInfoRequest findOne(Integer id);

    FinjaGetCustomerInfoRequest saveOrUpdate(FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest) throws Exception;

    List<FinjaGetCustomerInfoRequest> findAll()throws Exception;
    List<FinjaGetCustomerInfoRequest> findAllByUser(User user)throws Exception;
}
