package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIndex;

public interface EasypaisaIndexServices {

    EasypaisaIndex saveOrUpdate(EasypaisaIndex easypaisaIndex) throws Exception;
}
