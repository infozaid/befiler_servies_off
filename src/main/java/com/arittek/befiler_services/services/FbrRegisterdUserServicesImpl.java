package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.FbrRegisteredUser;
import com.arittek.befiler_services.repository.FbrRegisteredUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FbrRegisterdUserServicesImpl implements FbrRegisterdUserServices{

    @Autowired
    FbrRegisteredUserRepository fbrRegisterdUserRepository;




    @Override
    public FbrRegisteredUser createFbrRegistredUser(FbrRegisteredUser fbrRegisteredUser) throws Exception {

        return fbrRegisterdUserRepository.save(fbrRegisteredUser);
    }

    @Override
    public FbrRegisteredUser update(FbrRegisteredUser fbrRegistredUser) {
        if(fbrRegistredUser != null && fbrRegistredUser.getId() != null){
            FbrRegisteredUser fbrRegistredUser1 = fbrRegisterdUserRepository.findById(fbrRegistredUser.getId()).orElse(null);
            if(fbrRegistredUser1 != null){
                fbrRegisterdUserRepository.save(fbrRegistredUser1);
            }
        }
        return null;
    }
}
