package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenResponse;
import com.arittek.befiler_services.repository.payment.apps.AppsGetAccessTokenResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppsGetAccessTokenResponseServiceImpl implements AppsGetAccessTokenResponseService {

    private AppsGetAccessTokenResponseRepository appsGetAccessTokenResponseRepository;

    @Autowired
    public AppsGetAccessTokenResponseServiceImpl(AppsGetAccessTokenResponseRepository appsGetAccessTokenResponseRepository) {
	this.appsGetAccessTokenResponseRepository = appsGetAccessTokenResponseRepository;
    }

    @Override
    public AppsGetAccessTokenResponse saveOrUpdate(AppsGetAccessTokenResponse appsGetAccessTokenResponse) throws Exception {
        if (appsGetAccessTokenResponse != null)
            return appsGetAccessTokenResponseRepository.save(appsGetAccessTokenResponse);
	return null;
    }
}
