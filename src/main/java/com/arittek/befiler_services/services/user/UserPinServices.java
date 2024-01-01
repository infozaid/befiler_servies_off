package com.arittek.befiler_services.services.user;

import com.arittek.befiler_services.model.user.UserPin;
import com.arittek.befiler_services.model.user.User;

public interface UserPinServices {

    UserPin findOne(Integer id);
    UserPin findByCode(String code);

    UserPin save(UserPin userPin) throws Exception;
    UserPin update(UserPin userPin);

    UserPin findAllByUserAndCode(User user, String code);

}
