package com.arittek.befiler_services.services.payment.keenu;

import com.arittek.befiler_services.model.payment.keenu.KeenuResponse;

public interface KeenuResponseServices {

    KeenuResponse saveOrUpdate(KeenuResponse keenuResponse) throws Exception;
}
