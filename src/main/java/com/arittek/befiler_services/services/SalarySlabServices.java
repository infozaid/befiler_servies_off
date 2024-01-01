package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.SalarySlab;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface SalarySlabServices {

    SalarySlab findOneActiveSalarySlab(Integer salarySlabId) throws Exception;

    SalarySlab delete(SalarySlab salarySlab, User authorizer) throws Exception;
    SalarySlab saveOrUpdate(SalarySlab salarySlab)throws Exception;


    List<SalarySlab> findAllByTaxformYearAndStatus(TaxformYears taxformYears, AppStatus status) throws Exception;

}
