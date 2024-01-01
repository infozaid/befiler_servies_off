package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenRequest;
import com.arittek.befiler_services.repository.payment.apps.AppsGetAccessTokenRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppsGetAccessTokenRequestServiceImpl implements AppsGetAccessTokenRequestService {

    private AppsGetAccessTokenRequestRepository appsGetAccessTokenRequestRepository;

    @Autowired
    public AppsGetAccessTokenRequestServiceImpl(AppsGetAccessTokenRequestRepository appsGetAccessTokenRequestRepository) {
	this.appsGetAccessTokenRequestRepository = appsGetAccessTokenRequestRepository;
    }

    @Override
    public AppsGetAccessTokenRequest saveOrUpdate(AppsGetAccessTokenRequest appsGetAccessTokenRequest) throws Exception {
        if (appsGetAccessTokenRequest != null)
            return appsGetAccessTokenRequestRepository.save(appsGetAccessTokenRequest);
	return null;
    }
}
