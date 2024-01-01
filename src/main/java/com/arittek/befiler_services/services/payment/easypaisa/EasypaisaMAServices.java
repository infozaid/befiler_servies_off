package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMARequest;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMAResponse;

public interface EasypaisaMAServices {

    EasypaisaMARequest saveOrUpdateRequest(EasypaisaMARequest easypaisaMARequest) throws Exception;
    EasypaisaMAResponse saveOrUpdateResponse(EasypaisaMAResponse easypaisaMAResponse) throws Exception;
}
