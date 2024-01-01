package com.arittek.befiler_services.controller.corporate;

import com.arittek.befiler_services.beans.CorporateEmployeeBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(value = "/corporate/employee")
public class CorporateEmployeeController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private CorporateServices corporateServices;
    private PaymentServices paymentServices;
    private CorporateEmployeeServices corporateEmployeeServices;
    private RoleService roleService;

    @Autowired
    CorporateEmployeeController(UsersServices usersServices, TaxformServices taxformServices, CorporateServices corporateServices, PaymentServices paymentServices, CorporateEmployeeServices corporateEmployeeServices, RoleService roleService) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.corporateServices = corporateServices;
        this.paymentServices = paymentServices;
        this.roleService = roleService;
        this.corporateEmployeeServices = corporateEmployeeServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> createCorporateEmployee(@RequestBody CorporateEmployeeBean corporateEmployeeBean) throws Exception {

        if (corporateEmployeeBean != null && corporateEmployeeBean.getCorporateId() != null) {
            try {
                User authorizer;
                if (corporateEmployeeBean.getUserId() != null) {
                    /*authorizer = usersServices.findOneByIdAndStatus(corporateEmployeeBean.getUserId(), usersServices.findUserStatusById(1));*/
                    authorizer = usersServices.findOneByIdAndStatus(corporateEmployeeBean.getUserId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                }

                Corporate corporate;
                if (corporateEmployeeBean.getCorporateId() != null) {
                    corporate = corporateServices.findOneByIdAndStatus(corporateEmployeeBean.getCorporateId(), AppStatus.ACTIVE);
                    if (corporate == null) {
                        return new ResponseEntity<>(new Status(0, "Please select corporate"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Please select corporate"), HttpStatus.OK);
                }

                CorporateEmployee corporateEmployeeCnic = corporateEmployeeServices.findCorporateEmployeeByCnicNo(corporateEmployeeBean.getCnicNo());
                if (corporateEmployeeCnic != null) {
                    return new ResponseEntity<>(new Status(0, "User is already registered"), HttpStatus.OK);
                }

                User userCnic = usersServices.findOneByCnicNo(corporateEmployeeBean.getCnicNo());

                CorporateEmployee corporateEmployee = new CorporateEmployee();

                corporateEmployee.setId(corporateEmployeeBean.getId());
                corporateEmployee.setFirstName(corporateEmployeeBean.getFirstName());
                corporateEmployee.setLastName(corporateEmployeeBean.getLastName());
                corporateEmployee.setEmailAddress(corporateEmployeeBean.getEmailAddress());
                corporateEmployee.setCnicNo(corporateEmployeeBean.getCnicNo());
                corporateEmployee.setDesignation(corporateEmployeeBean.getDesignation());
                corporateEmployee.setDepartment(corporateEmployeeBean.getDepartment());
                corporateEmployee.setCorporate(corporate);
                corporateEmployee.setPaymentByCorporate(corporateEmployeeBean.getPaymentByCorporate());

                if (userCnic == null) {
                    corporateEmployee.setStatus(AppStatus.DE_ACTIVE);
                } else {
                    /*userCnic.setUserType(usersServices.findUserTypeById(2));*/
                    Set<Role> roles = new HashSet<>();
                    roles.add(roleService.findOneCustomerRole());
                    userCnic.setRoles(roles);
                    usersServices.update(userCnic);
                    corporateEmployee.setStatus(AppStatus.ACTIVE);
                }
                corporateEmployeeServices.create(corporateEmployee);

                return new ResponseEntity<>(new Status(1, "CorporateEmployee Created Successfully."), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<>(new Status(0, "CorporateEmployee Created failed."), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0, "CorporateEmployee Created failed."), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.PUT})
    public ResponseEntity<Status> updateCorporateEmployee(@RequestBody CorporateEmployeeBean corporateEmployeeBean) throws Exception {
        if (corporateEmployeeBean != null && corporateEmployeeBean.getCorporateId() != null) {
            Corporate corporate = corporateServices.findOneByIdAndStatus(corporateEmployeeBean.getCorporateId(), AppStatus.ACTIVE);
            User authorizer = usersServices.findOneByIdAndStatus(corporateEmployeeBean.getUserId(), UserStatus.ACTIVE);
            if (corporate != null && authorizer != null) {
                if (corporateEmployeeBean.getId() != null) {
                    CorporateEmployee corporateEmployee = corporateEmployeeServices.findOne(corporateEmployeeBean.getId());
                    if (corporateEmployee != null) {
                        corporateEmployee.setFirstName(corporateEmployeeBean.getFirstName());
                        corporateEmployee.setLastName(corporateEmployeeBean.getLastName());
                        corporateEmployee.setEmailAddress(corporateEmployeeBean.getEmailAddress());
                        corporateEmployee.setCnicNo(corporateEmployeeBean.getCnicNo());
                        corporateEmployee.setCorporate(corporate);
                        corporateEmployeeServices.create(corporateEmployee);
                        return new ResponseEntity<>(new Status(1, "CorporateEmployee successfully update."), HttpStatus.OK);
                    }
                    return new ResponseEntity<>(new Status(0, "Invalid CorporateEmployee!"), HttpStatus.OK);
                }
                return new ResponseEntity<>(new Status(0, "Record not Found!"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Invalid User."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data!"), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getList", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getById(@RequestBody CorporateEmployeeBean corporateEmployeeBean) throws JsonParseException {
        List<CorporateEmployeeBean> list = new ArrayList<CorporateEmployeeBean>();

        try {
            if (corporateEmployeeBean != null && corporateEmployeeBean.getCorporateId() != null) {
                Corporate corporate = corporateServices.findOneByIdAndStatus(corporateEmployeeBean.getCorporateId(), AppStatus.ACTIVE);
                if (corporate != null) {
                    List<CorporateEmployee> corporateEmployeeList = corporate.getCorporateEmployeeList();
                    if (corporateEmployeeList.size() > 0) {
                        for (CorporateEmployee corporateEmployee : corporateEmployeeList) {

                            double totalAmount = 0;

                            List<Payment> paymentList = paymentServices.findAllByUser(corporateEmployee.getUser());
                            for (Payment payment : paymentList) {
                                totalAmount += payment.getSettingPayment().getAmount();
                            }

                            CorporateEmployeeBean bean = new CorporateEmployeeBean();
                            bean.setTotalAmount(totalAmount);
                            bean.setId(corporateEmployee.getId());
                            bean.setDepartment(corporateEmployee.getDepartment());
                            bean.setUserId(corporateEmployee.getUser().getId());
                            bean.setDesignation(corporateEmployee.getDesignation());
                            bean.setFirstName(corporateEmployee.getFirstName());
                            bean.setLastName(corporateEmployee.getLastName());
                            bean.setEmailAddress(corporateEmployee.getEmailAddress());
                            bean.setCnicNo(corporateEmployee.getCnicNo());
                            bean.setPaymentByCorporate(corporateEmployee.getPaymentByCorporate());
                            bean.setCorporateId(corporateEmployee.getCorporate().getId());
                            list.add(bean);
                        }
                        StatusBean statusBean = new StatusBean(1, "successfully");
                        statusBean.setResponse(list);
                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<StatusBean>(new StatusBean(0, "invalid corporate"), HttpStatus.OK);
                }
            } // corporate bean
            return new ResponseEntity<StatusBean>(new StatusBean(0, "incomplete data"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<StatusBean>(new StatusBean(0, "failed"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getTaxforms", produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<StatusBean> getTaxformList(@RequestBody CorporateEmployeeBean corporateEmployeeBean) throws Exception {

        if (corporateEmployeeBean != null) {
            try {

                CorporateEmployee corporateEmployee;
                if (corporateEmployeeBean.getId() != null) {
                    corporateEmployee = corporateEmployeeServices.findByIdAndStatus(corporateEmployeeBean.getId(), AppStatus.ACTIVE);
                    if (corporateEmployee == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Please select employee"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Please select employee"), HttpStatus.OK);
                }

                User user;
                if (corporateEmployee.getUser() != null) {
                    user = corporateEmployee.getUser();
                } else {
                    /*user = usersServices.findOneByCnicNoAndStatus(corporateEmployee.getCnicNo(), usersServices.findUserStatusById(1));*/
                    user = usersServices.findOneByCnicNoAndStatus(corporateEmployee.getCnicNo(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Employee is not registered"), HttpStatus.OK);
                    }
                }

                List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                if (taxformList != null) {
                    List<TaxformBean> taxformBeanList = new ArrayList<>();
                    for (Taxform taxform : taxformList) {

                        if (taxform.getTaxformYear() != null) {
                            TaxformBean taxformBean = new TaxformBean();
                            taxformBean.setEmail(taxform.getEmail());
                            taxformBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                            taxformBean.setCnic(taxform.getCnic());
                            taxformBean.setStatusId(taxform.getStatus().getId());
                            taxformBean.setYear(taxform.getTaxformYear().getYear() + "");
                            taxformBean.setDateDifference(CommonUtil.daysBetweenTwoDates(taxform.getCurrentDate(),CommonUtil.getCurrentTimestamp()));
                            taxformBean.setCurrentDate(taxform.getCurrentDate());

                            Payment payment = paymentServices.checkForTaxformPayment(taxform);
                            if (payment != null) {
                                taxformBean.setAmount(Double.parseDouble(payment.getSettingPayment().getAmount() + ""));
                                if (payment.getPaymentCustomerInfo() != null) {
                                    if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.UBL) {
                                        taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgRegistrationResponse().getTransactionId());
                                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.SIMSIM) {
                                        taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getFinjaPaymentToMerchantResponse().getTransactionCode());
                                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.PROMO_CODE) {
                                        taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getPromoCode().getPromoCode());
                                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.KEENU) {
                                        taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getKeenuRequest().getKeenuResponse().getTransactionId());
                                    }
                                }
                            }
                            taxformBeanList.add(taxformBean);
                        }
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(taxformBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<>(new StatusBean(0, "failed"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data"), HttpStatus.OK);
    }
/*

    @RequestMapping(produces = "application/json",method = RequestMethod.DELETE)
    public StatusBean deleteCorporateEmployee(@RequestBody CorporateEmployeeBean corporateEmployeeBean)throws Exception{
        if(corporateEmployeeBean.getId() != null){
            CorporateEmployee corporateEmployee = corporateEmployeeServices.findOne(corporateEmployeeBean.getId());
            if(corporateEmployee != null){
                corporateEmployeeServices.delete(corporateEmployee);
                return new StatusBean(1,"successfully delete");
            }
            return new StatusBean(0,"corporate employee not found");
        }
        return new StatusBean(0,"Incomplete Data");
    }

*/


}

