package com.arittek.befiler_services.controller;/*
package com.arittek.controller;

import com.arittek.beans.*;
import com.arittek.enums.UserStatus;
import com.arittek.model.SalarySlab;
import com.arittek.model.TaxformYears;
import com.arittek.model.user.User;
import com.arittek.services.AppStatusServices;
import com.arittek.services.SalarySlabServices;
import com.arittek.services.TaxformYearsServices;
import com.arittek.services.UsersServices;
import com.arittek.util.CommonUtil;
import com.arittek.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "salary/slab")
public class SalarySlabController {

    private UsersServices usersServices;
    private SalarySlabServices salarySlabServices;
    private AppStatusServices appStatusServices;
    private TaxformYearsServices taxformYearsServices;

    @Autowired
    SalarySlabController(UsersServices usersServices, SalarySlabServices salarySlabServices, AppStatusServices appStatusServices, TaxformYearsServices taxformYearsServices) {
        this.usersServices = usersServices;
        this.salarySlabServices = salarySlabServices;
        this.appStatusServices = appStatusServices;
        this.taxformYearsServices = taxformYearsServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<Status> saveOrUpdateAll(@RequestBody SalarySlabByTaxformYearBean salarySlabByTaxformYearBean) throws Exception {
                System.out.print("salary Slabe is avalible");
                System.out.print(salarySlabByTaxformYearBean.getAuthorizerId());
        try {
            if (salarySlabByTaxformYearBean != null && salarySlabByTaxformYearBean.getSalarySlabBeanList() != null && salarySlabByTaxformYearBean.getSalarySlabBeanList().size() > 0) {

                User authorizer;
                System.out.print("aurtrigzer id "+salarySlabByTaxformYearBean.getAuthorizerId());
                if (salarySlabByTaxformYearBean.getAuthorizerId() != null) {
                    */
/*authorizer = usersServices.findOneByIdAndStatus(salarySlabByTaxformYearBean.getAuthorizerId(), usersServices.findUserStatusById(1));*//*

                    authorizer = usersServices.findOneByIdAndStatus(salarySlabByTaxformYearBean.getAuthorizerId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                }

                TaxformYears taxformYears;
                if (salarySlabByTaxformYearBean.getYearId() != null) {
                    taxformYears = taxformYearsServices.findOneByIdAndStatusNotIn(salarySlabByTaxformYearBean.getYearId(), appStatusServices.findOneByDeletedStatus());
                    if (taxformYears == null) {
                        return new ResponseEntity<>(new Status(0, "Please select year"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Please select year"), HttpStatus.OK);
                }

                for (SalarySlabBean salarySlabBean : salarySlabByTaxformYearBean.getSalarySlabBeanList()) {
                    SalarySlab salarySlab;
                    if (salarySlabBean.getId() != null) {
                        salarySlab = salarySlabServices.findOneActiveSalarySlab(salarySlabBean.getId());
                        if (salarySlab == null) {
                            salarySlab = new SalarySlab();
                        }
                    } else {
                        salarySlab = new SalarySlab();
                    }
                    salarySlab.setSerialNumber(salarySlabBean.getSerialNo());
                    salarySlab.setUpperLimit(salarySlabBean.getUpperLimit());
                    salarySlab.setFixed(salarySlabBean.getFixed());
                    salarySlab.setRate(Double.parseDouble(salarySlabBean.getRate()));
                    salarySlab.setLowerLimit(salarySlabBean.getLowerLimit());
                    salarySlab.setTaxformYear(taxformYears);
                    salarySlab.setAuthorizer(authorizer);
                    salarySlab.setCurrentDate(CommonUtil.getCurrentTimestamp());
                    salarySlab.setStatus(appStatusServices.findOneByActiveStatus());
                    salarySlabServices.saveOrUpdate(salarySlab);

                    System.out.println("SaveOrUpdate successfully:  ");
                }
                return new ResponseEntity<>(new Status(1, "Record updated successfully"), HttpStatus.OK);
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
    }

    @RequestMapping(value = "/taxformYear", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<StatusBean> getList(@RequestBody SalarySlabByTaxformYearBean salarySlabByTaxformYearBean) throws Exception {
        try {
            if (salarySlabByTaxformYearBean != null) {

                TaxformYears taxformYears;
                if (salarySlabByTaxformYearBean.getYearId() != null) {
                    taxformYears = taxformYearsServices.findOneByIdAndStatus(salarySlabByTaxformYearBean.getYearId(), appStatusServices.findOneByActiveStatus());
                    if (taxformYears == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Please select year"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Please select year"), HttpStatus.OK);
                }

                List<SalarySlab> salarySlabList = salarySlabServices.findAllByTaxformYearAndStatus(taxformYears, appStatusServices.findOneByActiveStatus());

                if (salarySlabList != null) {
                    List<SalarySlabBean> salarySlabBeanList = new ArrayList<>();
                    for (SalarySlab salarySlab : salarySlabList) {
                        SalarySlabBean salarySlabBean = new SalarySlabBean();
                        salarySlabBean.setId(salarySlab.getId());
                        salarySlabBean.setSerialNo(salarySlab.getSerialNumber());
                        salarySlabBean.setUpperLimit(salarySlab.getUpperLimit());
                        salarySlabBean.setFixed(salarySlab.getFixed());
                        salarySlabBean.setRate(salarySlab.getRate() + "");
                        salarySlabBean.setLowerLimit(salarySlab.getLowerLimit());
                        salarySlabBean.setAuthorizerId(salarySlab.getAuthorizer().getId());
                        salarySlabBeanList.add(salarySlabBean);
                    }

                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(salarySlabBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data"), HttpStatus.OK);
    }


    // ====================== delete ====================================================
    @RequestMapping(value = "/delete", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> delete(@RequestBody SalarySlabBean salarySlabBean) throws Exception {
        try {
            if (salarySlabBean != null) {

                User authorizer;
                if (salarySlabBean.getAuthorizerId() != null) {
                    */
/*authorizer = usersServices.findOneByIdAndStatus(salarySlabBean.getAuthorizerId(), usersServices.findUserStatusById(1));*//*

                    authorizer = usersServices.findOneByIdAndStatus(salarySlabBean.getAuthorizerId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);
                }

                SalarySlab salarySlab;
                if (salarySlabBean.getId() != null) {
                    salarySlab = salarySlabServices.findOneActiveSalarySlab(salarySlabBean.getId());
                    if (salarySlab == null) {
                        return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
                }
                salarySlab = salarySlabServices.delete(salarySlab, authorizer);
                if (salarySlab != null) {
                    return new ResponseEntity<>(new Status(0, "Record deleted successfully"), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "failed Incomplete data"), HttpStatus.OK);
    }

    // ====================== taxform years list  ======================== nadeem

    @RequestMapping(value = "/taxformYears", produces = "application/json",method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<StatusBean> getTaxformYears() throws Exception {
        List<TaxformYears> taxformYearss = taxformYearsServices.findAllByStatusNotIn(appStatusServices.findOneByDeletedStatus());
        List<TaxformYearsBeans> yearsBeansList = new ArrayList<>();
        if (taxformYearss.size() > 0) {
            for (TaxformYears taxformYears : taxformYearss) {
                TaxformYearsBeans taxformYearsBeans = new TaxformYearsBeans();
                taxformYearsBeans.setYearId(taxformYears.getId());
                taxformYearsBeans.setYear(taxformYears.getYear());
                taxformYearsBeans.setCurrentDate(taxformYears.getCurrentDate());
                yearsBeansList.add(taxformYearsBeans);
            }
            StatusBean statusBean = new StatusBean(1, "Successfully");
            statusBean.setResponse(yearsBeansList);
            return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
        }
        else return new ResponseEntity<StatusBean>(new StatusBean(0,"Data not found! "), HttpStatus.OK);
    }


    // ====================== save year  ======================== nadeem

    @RequestMapping(value = "/saveYear", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> saveYear(@RequestBody TaxformYearsBeans taxformYearsBeans) throws Exception {
        if (taxformYearsBeans != null && taxformYearsBeans.getYear() != null) {

            TaxformYears taxformYears1 = taxformYearsServices.findOneByYearAndStatusNotIn(taxformYearsBeans.getYear(), appStatusServices.findOneByDeletedStatus());
            if (taxformYears1 == null) {
                TaxformYears taxformYears = new TaxformYears();
                taxformYears.setYear(taxformYearsBeans.getYear());
                taxformYears.setCurrentDate(CommonUtil.getCurrentTimestamp());
                taxformYearsServices.save(taxformYears);
                return new ResponseEntity<Status>(new Status(1, "save successfully: " + taxformYearsBeans.getYear()), HttpStatus.OK);
            }
            return new ResponseEntity<Status>(new Status(0, "already saved: " + taxformYearsBeans.getYear()), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(0, "Incomplete Data ! "), HttpStatus.OK);
    }
    // also created taxformYear EVENT in befiler db

    // ====================== delete year  ======================== nadeem

    @RequestMapping(value = "/deleteYear", produces = "application/json", method = RequestMethod.DELETE)
    public ResponseEntity<Status> deleteYear(@RequestBody TaxformYearsBeans taxformYearsBeans) throws Exception {
        if (taxformYearsBeans != null && taxformYearsBeans.getYearId() != null) {
            TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatus(taxformYearsBeans.getYearId(), appStatusServices.findOneByActiveStatus());
            if (taxformYears != null) {
                */
/*taxformYearsServices.delete(taxformYears);*//*

                return new ResponseEntity<Status>(new Status(1, "delete successfully: " + taxformYears.getYear()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<Status>(new Status(0, "failed "), HttpStatus.OK);
    }
}
*/
