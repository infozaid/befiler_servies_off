package com.arittek.befiler_services.services.payment.easypaisa;


import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaConfirm;

public interface EasypaisaConfirmServices {

    EasypaisaConfirm saveOrUpdate(EasypaisaConfirm easypaisaConfirm) throws Exception;
}
