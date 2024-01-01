package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface CorporateEmployeeServices {

    CorporateEmployee findCorporateEmployeeByCnicNo(String cnic)throws Exception;
    CorporateEmployee findOne(Integer corporateEmployeeId)throws Exception;
    CorporateEmployee findByIdAndStatus(Integer id, AppStatus appStatus) throws Exception;

    void delete(CorporateEmployee corporateEmployee)throws Exception;
    CorporateEmployee create(CorporateEmployee corporateEmployee)throws Exception;
    CorporateEmployee saveOrUpdate(CorporateEmployee corporateEmployee) throws Exception;

    List<CorporateEmployee> findAllByUser(User user)throws Exception;
    List<CorporateEmployee> findAllByCorporate(Corporate corporate)throws Exception;

    List<CorporateEmployee> findAllByCorporateAndStatus(Corporate corporate, AppStatus status) throws Exception;

}
