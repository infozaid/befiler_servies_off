package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionResponse;
import com.arittek.befiler_services.repository.payment.apps.AppsPostTransactionResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppsPostTransactionResponseServiceImpl implements AppsPostTransactionResponseService {

    private AppsPostTransactionResponseRepository appsPostTransactionResponseRepository;

    @Autowired
    public AppsPostTransactionResponseServiceImpl(AppsPostTransactionResponseRepository appsPostTransactionResponseRepository) {
	this.appsPostTransactionResponseRepository = appsPostTransactionResponseRepository;
    }

    @Override
    public AppsPostTransactionResponse saveOrUpdate(AppsPostTransactionResponse appsPostTransactionResponse) throws Exception {
        if (appsPostTransactionResponse != null)
            return appsPostTransactionResponseRepository.save(appsPostTransactionResponse);
	return null;
    }
}
