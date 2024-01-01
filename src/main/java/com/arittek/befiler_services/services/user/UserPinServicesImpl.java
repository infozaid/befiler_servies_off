package com.arittek.befiler_services.services.user;

import com.arittek.befiler_services.model.user.UserPin;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.user.UserPinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPinServicesImpl implements UserPinServices {

    @Autowired
    UserPinRepository userPinRepository;

    @Override
    public UserPin findOne(Integer id) {
        if (id != null) {
            UserPin userPin = userPinRepository.findById(id).orElse(null);
            return userPin;

        }
        return null;
    }

    @Override
    public UserPin findByCode(String code) {
        if (!code.equals(null) || !code.equals("")) {
            UserPin userPin = userPinRepository.findByCode(code);
            return userPin;
        }
        return null;
    }

    @Override
    public UserPin save(UserPin userPin) throws Exception {
        if(userPin.getId() != null){
            return  null;
        }
        UserPin savedUserPin = userPinRepository.save(userPin);
        return savedUserPin;
    }

    @Override
    public UserPin update(UserPin userPin) {
        if (userPin.getId() != null) {
            UserPin userPinPersisted = findOne(userPin.getId());
            if (userPinPersisted == null) {
                return null;
            }
            UserPin updatedUserPin = userPinRepository.save(userPin);
            return updatedUserPin;
        }
        return null;
    }

    @Override
    public UserPin findAllByUserAndCode(User user, String code) {
        if (user != null && !code.equals(null)) {
            UserPin user1 = userPinRepository.findAllByUserAndCode(user, code);
            return user1;
        }
        return null;
    }

}
