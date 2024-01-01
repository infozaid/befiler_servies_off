package com.arittek.befiler_services.fbr;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.user.UsersServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FbrActiveTaxpayerServicesImpl implements FbrActiveTaxpayerServices {

    private UsersServices usersServices;
    private FbrActiveTaxpayerRepository registeredRepository;

    @Autowired
    public FbrActiveTaxpayerServicesImpl(UsersServices usersServices, FbrActiveTaxpayerRepository registeredRepository){
        this.usersServices = usersServices;
        this.registeredRepository = registeredRepository;
    }
    @Override
    public FbrActiveTaxpayer save(FbrActiveTaxpayer taxpayer) {
        if(taxpayer != null){
            return registeredRepository.save(taxpayer);
        }
        return null;
    }

    @Override
    public FbrActiveTaxpayer findOne(Integer userId) {
        if(userId != null){
        return registeredRepository.findById(userId).orElse(null);
        }
        return null;
    }

    @Override
    public FbrActiveTaxpayer findByRegistrationNo(String registrationNo)throws Exception {
        if(registrationNo != null){
            return registeredRepository.findOneByRegistrationNo(registrationNo);
        }
        return null;
    }

    @Override
    public List<FbrActiveTaxpayer> findAll(User user) throws Exception {
        if(usersServices.checkIfUserIsAdmin(user) || usersServices.checkIfUserIsAccountant(user)) {
            return (List<FbrActiveTaxpayer>) registeredRepository.findAll();
        }
        return null;
    }

}
