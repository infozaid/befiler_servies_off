package com.arittek.befiler_services.services.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIPN;

public interface EasypaisaIPNServices {

    EasypaisaIPN saveOrUpdate(EasypaisaIPN easypaisaIPN) throws Exception;
}
