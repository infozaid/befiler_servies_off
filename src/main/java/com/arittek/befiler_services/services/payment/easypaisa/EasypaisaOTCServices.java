package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCRequest;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCResponse;

public interface EasypaisaOTCServices {

    EasypaisaOTCRequest saveOrUpdateRequest(EasypaisaOTCRequest easypaisaOTCRequest) throws Exception;
    EasypaisaOTCResponse saveOrUpdateResponse(EasypaisaOTCResponse easypaisaOTCResponse) throws Exception;
}
