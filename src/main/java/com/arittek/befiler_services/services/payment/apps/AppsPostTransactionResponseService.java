package com.arittek.befiler_services.services.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionResponse;

public interface AppsPostTransactionResponseService {

    AppsPostTransactionResponse saveOrUpdate(AppsPostTransactionResponse appsPostTransactionResponse) throws Exception;
}
