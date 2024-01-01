package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionRequest;
import com.arittek.befiler_services.repository.payment.apps.AppsPostTransactionRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppsPostTransactionRequestServiceImpl implements AppsPostTransactionRequestService {

    private AppsPostTransactionRequestRepository appsPostTransactionRequestRepository;

    @Autowired
    public AppsPostTransactionRequestServiceImpl(AppsPostTransactionRequestRepository appsPostTransactionRequestRepository) {
	this.appsPostTransactionRequestRepository = appsPostTransactionRequestRepository;
    }

    @Override
    public AppsPostTransactionRequest saveOrUpdate(AppsPostTransactionRequest appsPostTransactionRequest) throws Exception {
        if (appsPostTransactionRequest != null)
            return appsPostTransactionRequestRepository.save(appsPostTransactionRequest);
	return null;
    }
}
