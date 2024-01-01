package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;

import java.util.List;

public interface CorporateServices {

    Corporate findOneByIdAndStatus(Integer corporateId, AppStatus status) throws Exception;

    Corporate saveOrUpdate(Corporate corporate) throws Exception;

    List<Corporate> findAllByStatus(AppStatus status) throws Exception;
}
