package com.arittek.befiler_services.services.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;
import com.arittek.befiler_services.repository.payment.ipg.IpgFinalizationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IpgFinalizationRequestServicesImpl implements IpgFinalizationRequestServices {

    private IpgFinalizationRequestRepository ipgFinalizationRequestRepository;

    @Autowired
    IpgFinalizationRequestServicesImpl(IpgFinalizationRequestRepository ipgFinalizationRequestRepository) {
        this.ipgFinalizationRequestRepository = ipgFinalizationRequestRepository;

    }

    @Override
    public IpgFinalizationRequest saveOrUpdate(IpgFinalizationRequest ipgFinalizationRequest) throws Exception {
        if (ipgFinalizationRequest != null && ipgFinalizationRequest.getIpgRegistrationRequest() != null) {
            return ipgFinalizationRequestRepository.save(ipgFinalizationRequest);
        }
        return null;
    }
}
