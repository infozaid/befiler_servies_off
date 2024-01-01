package com.arittek.befiler_services.controller.setting;

import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.beans.taxform.TaxformYearsBeans;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/taxform/years")
public class TaxformYearsController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private TaxformYearsServices taxformYearsServices;
    private CorporateEmployeeServices corporateEmployeeServices;

    @Autowired
    public TaxformYearsController(UsersServices usersServices, TaxformServices taxformServices, TaxformYearsServices taxformYearsServices, CorporateEmployeeServices corporateEmployeeServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.taxformYearsServices = taxformYearsServices;
        this.corporateEmployeeServices = corporateEmployeeServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Status> saveTaxformYear(@RequestBody TaxformYearsBeans taxformYearsBeans){
        try {
            if (taxformYearsBeans != null && taxformYearsBeans.getYear() != null && taxformYearsBeans.getAuthorizerId() != null) {

                /*User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), UserStatus.ACTIVE);
                if (authorizer == null) {
                    return new ResponseEntity<>(new Status(0, "Session expired."), HttpStatus.OK);
                }

                /*TaxformYears taxformYears = taxformYearsServices.findOneByYearAndStatusNotIn(taxformYearsBeans.getYear(), appStatusServices.findOneByDeletedStatus());*/
                TaxformYears taxformYears = taxformYearsServices.findOneByYearAndStatusNotIn(taxformYearsBeans.getYear(), AppStatus.DELETED);
                if (taxformYears != null) {
                    return new ResponseEntity<>(new Status(0, "Year already exists."), HttpStatus.OK);
                }

                taxformYears = new TaxformYears();
                taxformYears.setYear(taxformYearsBeans.getYear());
                /*taxformYears.setStatus(appStatusServices.findOneByDeActiveStatus());*/
                taxformYears.setStatus(AppStatus.DE_ACTIVE);

                taxformYears = taxformYearsServices.save(taxformYears);
                if (taxformYears != null) {
                    return new ResponseEntity<>(new Status(1, "Record created successfully."), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data."), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Status> updateTaxformYear(@RequestBody TaxformYearsBeans taxformYearsBeans){
        try {
            if (taxformYearsBeans != null && taxformYearsBeans.getYearId() != null && taxformYearsBeans.getYear() != null && taxformYearsBeans.getAuthorizerId() != null) {

                /*User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), UserStatus.ACTIVE);
                if (authorizer == null) {
                    return new ResponseEntity<>(new Status(0, "Session expired."), HttpStatus.OK);
                }

                /*TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatusNotIn(taxformYearsBeans.getYearId(), appStatusServices.findOneByDeletedStatus());*/
                TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatusNotIn(taxformYearsBeans.getYearId(), AppStatus.DELETED);
                if (taxformYears == null) {
                    return new ResponseEntity<>(new Status(0, "Can't update year."), HttpStatus.OK);
                }

                /*TaxformYears taxformYears1 = taxformYearsServices.findOneByYearAndStatusNotIn(taxformYearsBeans.getYear(), appStatusServices.findOneByDeletedStatus());*/
                TaxformYears taxformYears1 = taxformYearsServices.findOneByYearAndStatusNotIn(taxformYearsBeans.getYear(), AppStatus.ACTIVE);
                if (taxformYears1 != null) {
                    return new ResponseEntity<>(new Status(0, "Year already exists."), HttpStatus.OK);
                }

                taxformYears.setYear(taxformYearsBeans.getYear());
                /*taxformYears.setStatus(appStatusServices.findOneByDeActiveStatus());*/
                taxformYears.setStatus(AppStatus.DE_ACTIVE);

                taxformYears = taxformYearsServices.update(taxformYears);
                if (taxformYears != null) {
                    return new ResponseEntity<>(new Status(1, "Record updated successfully."), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data."), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/changeStatus" ,produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Status> activeTaxformYear(@RequestBody TaxformYearsBeans taxformYearsBeans){
        try {
            if (taxformYearsBeans != null && taxformYearsBeans.getYearId() != null && taxformYearsBeans.getAuthorizerId() != null) {

                /*User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                User authorizer = usersServices.findOneByIdAndStatus(taxformYearsBeans.getAuthorizerId(), UserStatus.ACTIVE);
                if (authorizer == null) {
                    return new ResponseEntity<>(new Status(0, "Session expired."), HttpStatus.OK);
                }

                /*TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatusNotIn(taxformYearsBeans.getYearId(), appStatusServices.findOneByDeletedStatus());*/
                TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatusNotIn(taxformYearsBeans.getYearId(), AppStatus.DELETED);
                if (taxformYears == null) {
                    return new ResponseEntity<>(new Status(0, "Can't update year."), HttpStatus.OK);
                }

                if (taxformYears.getStatus() != null) {
                    if (taxformYears.getStatus().getId() == 1)
                        /*taxformYears.setStatus(appStatusServices.findOneByDeActiveStatus());*/
                        taxformYears.setStatus(AppStatus.DE_ACTIVE);
                    else
                        /*taxformYears.setStatus(appStatusServices.findOneByActiveStatus());*/
                        taxformYears.setStatus(AppStatus.ACTIVE);

                    taxformYears = taxformYearsServices.update(taxformYears);
                    if (taxformYears != null) {
                        return new ResponseEntity<>(new Status(1, "Record updated successfully."), HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data."), HttpStatus.OK);
    }

    @RequestMapping(value = "/portal" ,produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StatusBean> getTaxformYearsForPortal(){
        try {
            /*List<TaxformYears> taxformYearsList = taxformYearsServices.findAllByStatusNotIn(appStatusServices.findOneByDeletedStatus());*/
            List<TaxformYears> taxformYearsList = taxformYearsServices.findAllByStatusNotIn(AppStatus.DELETED);
            if (taxformYearsList != null && taxformYearsList.size() > 0) {
                List<TaxformYearsBeans> taxformYearsBeansList = new ArrayList<>();
                for (TaxformYears taxformYears : taxformYearsList) {
                    if (taxformYears != null && taxformYears.getStatus() != null) {
                        TaxformYearsBeans taxformYearsBeans = new TaxformYearsBeans();

                        taxformYearsBeans.setYearId(taxformYears.getId());
                        taxformYearsBeans.setYear(taxformYears.getYear());
                        if (taxformYears.getStatus().getId() == 1) {
                            taxformYearsBeans.setStatus("De-active");
                        } else {
                            taxformYearsBeans.setStatus("Active");
                        }
                        taxformYearsBeansList.add(taxformYearsBeans);
                    }
                }
                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(taxformYearsBeansList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data."), HttpStatus.OK);
    }
    /*@RequestMapping(produces = "application/json", method = RequestMethod.GET)
public @ResponseBody ResponseEntity<StatusBean> getTaxformYearsForCustomer(){
    try {
        List<TaxformYears> taxformYearsList = taxformYearsServices.findAllActiveYears();
        if (taxformYearsList != null && taxformYearsList.size() > 0) {
            List<TaxformYearsBeans> taxformYearsBeansList = new ArrayList<>();
            for (TaxformYears taxformYears : taxformYearsList) {
                if (taxformYears != null && taxformYears.getStatus() != null) {
                    TaxformYearsBeans taxformYearsBeans = new TaxformYearsBeans();

                    taxformYearsBeans.setYearId(taxformYears.getId());
                    taxformYearsBeans.setYear(taxformYears.getYear());
                    taxformYearsBeansList.add(taxformYearsBeans);
                }
            }
            StatusBean statusBean = new StatusBean(1, "Successfull");
            statusBean.setResponse(taxformYearsBeansList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        }
    } catch (Exception e) {
        Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
        Logger4j.getLogger().error("Exception : " , e);
    }
    return new ResponseEntity<>(new StatusBean(0, "Incomplete data."), HttpStatus.OK);
}*/

    @RequestMapping(value = "/customer",produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<StatusBean> getTaxformYearsForCustomer(@RequestBody TaxformBean taxformBean){
        try {
            User user;
            if (taxformBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Incomplete data : user"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete data : userId"), HttpStatus.OK);
            }

            List<TaxformYears> taxformYearsList = taxformYearsServices.findAllActiveYears();
            if (taxformYearsList != null && taxformYearsList.size() > 0) {
                List<TaxformYearsBeans> taxformYearsBeansList = new ArrayList<>();

                for (TaxformYears taxformYears : taxformYearsList) {
                    if (taxformYears != null && taxformYears.getStatus() != null) {
                        TaxformYearsBeans taxformYearsBeans = new TaxformYearsBeans();

                        taxformYearsBeans.setYearId(taxformYears.getId());
                        taxformYearsBeans.setYear(taxformYears.getYear());


                        Taxform taxform = taxformServices.findOneByTaxformYearAndUser(taxformYears, user);
                        if (taxform != null && taxform.getStatus().getId() != null) {
                            taxformYearsBeans.setTaxformId(taxform.getId());
                            taxformYearsBeans.setDescription(taxform.getStatus().getDescription());
                            if (taxform.getStatus().getId() == 0) {
                                taxformYearsBeans.setTaxformStatus("0");
                                taxformYearsBeans.setTaxformStatusMobile(0);
                                taxformYearsBeans.setTaxformStatusDescription("Update");
                            } else if (taxform.getStatus().getId() == 1) {
                                taxformYearsBeans.setTaxformStatus("0");
                                taxformYearsBeans.setTaxformStatusMobile(1);
                                taxformYearsBeans.setTaxformStatusDescription("Corporate");
                            } else if (taxform.getStatus().getId() == 2) {
                                taxformYearsBeans.setTaxformStatus("0");
                                taxformYearsBeans.setTaxformStatusMobile(1);
                                taxformYearsBeans.setTaxformStatusDescription("FBR");
                            } else if (taxform.getStatus().getId() == 3) {
                                taxformYearsBeans.setTaxformStatus("0");
                                taxformYearsBeans.setTaxformStatusMobile(1);
                                taxformYearsBeans.setTaxformStatusDescription("Payment");
                            } else {
                                taxformYearsBeans.setTaxformStatus("1");
                                taxformYearsBeans.setTaxformStatusMobile(1);
                                taxformYearsBeans.setTaxformStatusDescription("Review");
                            }

                        } else {
                            taxformYearsBeans.setTaxformStatus("-1");
                            taxformYearsBeans.setTaxformStatusMobile(-1);
                            taxformYearsBeans.setTaxformStatusDescription("Start");
                        }

                        CorporateEmployee corporateEmployee = corporateEmployeeServices.findCorporateEmployeeByCnicNo(user.getCnicNo());
                        if (corporateEmployee == null) {
                            taxformYearsBeans.setCorporateCheck(false);
                            taxformYearsBeans.setPaymentCheck(true);
                        } else {
                            taxformYearsBeans.setCorporateCheck(true);
                            if (corporateEmployee.getPaymentByCorporate() != null && corporateEmployee.getPaymentByCorporate()) {
                                taxformYearsBeans.setPaymentCheck(false);
                            } else {
                                taxformYearsBeans.setPaymentCheck(true);
                            }
                        }
                        taxformYearsBeansList.add(taxformYearsBeans);
                    }
                }
                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(taxformYearsBeansList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data."), HttpStatus.OK);
    }


}

