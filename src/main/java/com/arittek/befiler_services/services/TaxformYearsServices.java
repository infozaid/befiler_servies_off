package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;

import java.util.List;

public interface TaxformYearsServices {

    TaxformYears findOneByIdAndActiveStatus(Integer yearId) throws Exception;
    TaxformYears findOneByIdAndStatus(Integer yearId, AppStatus status) throws Exception;
    TaxformYears findOneByIdAndStatusNotIn(Integer yearId, AppStatus status) throws Exception;
    TaxformYears findOnePreviousYear(TaxformYears taxformYears) throws Exception;

    TaxformYears findOneByYearAndStatusNotIn(Integer year, AppStatus status) throws Exception;

    TaxformYears save(TaxformYears taxformYears)throws Exception;
    TaxformYears update(TaxformYears taxformYears)throws Exception;

    List<TaxformYears> findAllActiveYears() throws Exception;
    List<TaxformYears> findAllByStatus(AppStatus appStatus) throws Exception;
    List<TaxformYears> findAllByStatusNotIn(AppStatus appStatus) throws Exception;


}
