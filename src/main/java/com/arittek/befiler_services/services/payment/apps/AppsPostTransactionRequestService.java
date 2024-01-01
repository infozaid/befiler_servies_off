package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionRequest;

public interface AppsPostTransactionRequestService {

    AppsPostTransactionRequest saveOrUpdate(AppsPostTransactionRequest appsPostTransactionRequest) throws Exception;
}
