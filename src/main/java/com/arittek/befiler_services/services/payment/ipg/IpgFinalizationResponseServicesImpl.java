package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationResponse;
import com.arittek.befiler_services.repository.payment.ipg.IpgFinalizationResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IpgFinalizationResponseServicesImpl implements IpgFinalizationResponseServices {

    private IpgFinalizationResponseRepository ipgFinalizationResponseRepository;

    @Autowired
    IpgFinalizationResponseServicesImpl(IpgFinalizationResponseRepository ipgFinalizationResponseRepository) {
        this.ipgFinalizationResponseRepository = ipgFinalizationResponseRepository;
    }

    @Override
    public IpgFinalizationResponse update(IpgFinalizationResponse ipgFinalizationResponse) {
        if(ipgFinalizationResponse.getIpgRegistrationRequestId() != null){
            IpgFinalizationResponse ipgFinalizationResponse1 = ipgFinalizationResponseRepository.findById(ipgFinalizationResponse.getIpgRegistrationRequestId()).orElse(null);
            if(ipgFinalizationResponse1 == null){
                return  null;
            }
            return ipgFinalizationResponseRepository.save(ipgFinalizationResponse);
        }
        return null;
    }

    @Override
    public IpgFinalizationResponse create(IpgFinalizationResponse ipgFinalizationResponse) {
        if (ipgFinalizationResponse != null) {
            return ipgFinalizationResponseRepository.save(ipgFinalizationResponse);
        }
        return null;
    }
}
