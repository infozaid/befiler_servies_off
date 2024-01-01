package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationResponse;
import com.arittek.befiler_services.repository.payment.ipg.IpgRegistrationResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IpgRegistrationResponseServicesImpl implements IpgRegistrationResponseServices {

    private IpgRegistrationResponseRepository ipgRegistrationResponseRepository;

    @Autowired
    IpgRegistrationResponseServicesImpl(IpgRegistrationResponseRepository ipgRegistrationResponseRepository) {
        this.ipgRegistrationResponseRepository = ipgRegistrationResponseRepository;
    }

    @Override
    public IpgRegistrationResponse saveOrUpdate(IpgRegistrationResponse ipgRegistrationResponse) throws Exception {
        if (ipgRegistrationResponse != null && ipgRegistrationResponse.getIpgRegistrationRequest() != null) {
            return ipgRegistrationResponseRepository.save(ipgRegistrationResponse);
        }
        return null;
    }
}
