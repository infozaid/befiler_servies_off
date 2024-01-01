package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.payment.ipg.IpgRegistrationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IpgRegistrationRequestServicesImpl implements IpgRegistrationRequestServices {

    private IpgRegistrationRequestRepository ipgRegistrationRequestRepository;

    @Autowired
    IpgRegistrationRequestServicesImpl(IpgRegistrationRequestRepository ipgRegistrationRequestRepository) {
        this.ipgRegistrationRequestRepository = ipgRegistrationRequestRepository;
    }

    @Override
    public IpgRegistrationRequest findOne(Integer id) {
        if(id != null) {
            IpgRegistrationRequest ipgRegistrationRequest = ipgRegistrationRequestRepository.findById(id).orElse(null);
            return ipgRegistrationRequest;
        }
        return null;
    }

    @Override
    public List<IpgRegistrationRequest> findAll() throws Exception {
        return (List<IpgRegistrationRequest>) ipgRegistrationRequestRepository.findAll();
    }

    @Override
    public IpgRegistrationRequest saveOrUpdate(IpgRegistrationRequest ipgRegistrationRequest) throws Exception {
        if (ipgRegistrationRequest != null && ipgRegistrationRequest.getPaymentCustomerInfo() != null) {
            return ipgRegistrationRequestRepository.save(ipgRegistrationRequest);
        }
        return null;
    }

    @Override
    public List<IpgRegistrationRequest> findAllByUser(User user)throws Exception {
        if(user != null){
            return ipgRegistrationRequestRepository.findAllByCreatedBy(user.getId());
        }
        return new ArrayList<>();
    }
}
