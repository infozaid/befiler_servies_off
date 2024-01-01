package com.arittek.befiler_services.fbr;

import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface FbrActiveTaxpayerServices {

    FbrActiveTaxpayer save(FbrActiveTaxpayer taxpayer);
    FbrActiveTaxpayer findOne(Integer userId);
    FbrActiveTaxpayer findByRegistrationNo(String registrationNo)throws Exception;
    List<FbrActiveTaxpayer> findAll(User user)throws Exception;
}
