package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.beans.taxform.TaxformMinimalBean;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayer;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayerServices;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.services.AssignServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/taxform/view")
public class TaxformViewController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private PaymentServices paymentServices;
    private AssignServices assignServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;

    @Autowired
    public TaxformViewController(UsersServices usersServices, TaxformServices taxformServices, PaymentServices paymentServices, AssignServices assignServices, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.paymentServices = paymentServices;
        this.assignServices = assignServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/individual", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxfomsForIndividalCustomers(@RequestBody TaxformBean taxformBean) {
        if (taxformBean != null) {
            try {

                User user;
                if (taxformBean.getUserId() != null) {
                    /*user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                }

                List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                if (taxformList != null) {
                    List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
                    for (Taxform taxform : taxformList) {
                        if (taxform != null && taxform.getTaxformYear() != null && taxform.getStatus() != null) {
                            // Return only customer taxforms
                            if (taxform.getUser() != null && usersServices.checkIfUserIsCustomer(taxform.getUser())) {
                                TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();

                                taxformMinimalBean.setTaxformId(taxform.getId());
                                taxformMinimalBean.setYearId(taxform.getTaxformYear().getId());
                                taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());
                                taxformMinimalBean.setCnic(taxform.getCnic());
                                taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                                taxformMinimalBean.setDateDifference(CommonUtil.daysBetweenTwoDates(CommonUtil.getCurrentTimestamp(), taxform.getCurrentDate()));

                                if (taxform.getStatus().getId() != 0) {
                                    taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                                } else {
                                    taxformMinimalBean.setStatus("PERSONAL INFO");
                                    if (taxform.getTaxformIncomeTaxSalary() != null ||
                                            taxform.getTaxformIncomeTaxProperty() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                            taxform.getTaxformIncomeTaxOtherSources() != null ||
                                            (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                            (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                                        taxformMinimalBean.setStatus("INCOME TAX");
                                    }

                                    if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                                        taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                                    }

                                    if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                            (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                            (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                            taxform.getTaxformTaxDeductedCollectedOther() != null) {
                                        taxformMinimalBean.setStatus("TAX DEDUCTED");
                                    }

                                    if (taxform.getTaxformWelthStatement() != null) {
                                        taxformMinimalBean.setStatus("WEALTH STATEMENT");
                                    }
                                }

                                Payment payment = paymentServices.checkForTaxformPayment(taxform);
                                if (payment != null) {
                                    taxformMinimalBean.setAmount(Double.parseDouble(payment.getSettingPayment().getAmount() + ""));
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
                                taxformMinimalBeanList.add(taxformMinimalBean);
                            }
                        }
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(taxformMinimalBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/individual", params = {"orderBy", "direction", "page", "size"}, produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxfomsForIndividalCustomers(@RequestParam("orderBy") String orderBy,
                                                                      @RequestParam("direction") String direction,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size, @RequestBody TaxformBean taxformBean) {
        if (taxformBean != null) {
            try {

                User user;
                if (taxformBean.getUserId() != null) {
                    /*user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                }

//                List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                Page<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user, page - 1, size);
                PaginationBean paginationBean = new PaginationBean();
                paginationBean.setTotalElements((int) taxformList.getTotalElements());
                paginationBean.setTotalPages((int) taxformList.getTotalPages());
                paginationBean.setPageSize((int) taxformList.getSize());
                paginationBean.setPageNumber((int) taxformList.getNumber());
                long i = 1;
                if (page > 1) {
                    for (int count = 1; count < page; count++) {
                        i = i + size;
                        System.out.println("index " + i);
                    }
                }

                if (taxformList != null) {
                    List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
                    for (Taxform taxform : taxformList) {
                        if (taxform != null && taxform.getTaxformYear() != null && taxform.getStatus() != null) {
                            // Return only customer taxforms
                            if (taxform.getUser() != null && usersServices.checkIfUserIsCustomer(taxform.getUser())) {
                                TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();

                                while (i <= taxformList.getTotalElements()) {
                                    taxformMinimalBean.setSerialNo(i);
                                    break;
                                }
                                i++;

                                taxformMinimalBean.setTaxformId(taxform.getId());
                                taxformMinimalBean.setYearId(taxform.getTaxformYear().getId());
                                taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());
                                taxformMinimalBean.setCnic(taxform.getCnic());
                                taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                                taxformMinimalBean.setDateDifference(CommonUtil.daysBetweenTwoDates(CommonUtil.getCurrentTimestamp(), taxform.getCurrentDate()));

                                if (taxform.getStatus().getId() != 0) {
                                    taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                                } else {
                                    taxformMinimalBean.setStatus("PERSONAL INFO");
                                    if (taxform.getTaxformIncomeTaxSalary() != null ||
                                            taxform.getTaxformIncomeTaxProperty() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                            taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                            taxform.getTaxformIncomeTaxOtherSources() != null ||
                                            (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                            (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                                        taxformMinimalBean.setStatus("INCOME TAX");
                                    }

                                    if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                                        taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                                    }

                                    if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                            (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                            (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                            taxform.getTaxformTaxDeductedCollectedOther() != null) {
                                        taxformMinimalBean.setStatus("TAX DEDUCTED");
                                    }

                                    if (taxform.getTaxformWelthStatement() != null) {
                                        taxformMinimalBean.setStatus("WEALTH STATEMENT");
                                    }
                                }

                                Payment payment = paymentServices.checkForTaxformPayment(taxform);
                                if (payment != null) {
                                    taxformMinimalBean.setAmount(Double.parseDouble(payment.getSettingPayment().getAmount() + ""));
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
                                taxformMinimalBeanList.add(taxformMinimalBean);
                            }
                        }
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");

                    statusBean.setPagination(paginationBean);       // by maqsood

                    statusBean.setResponse(taxformMinimalBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/individual/payment", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxfomsPaymentsForIndividalCustomers(@RequestBody TaxformBean taxformBean) {
        if (taxformBean != null) {
            try {
                User user;
                if (taxformBean.getUserId() != null) {
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                }

                List<Payment> paymentList = paymentServices.findAllPaymentsDesc();
                if (paymentList != null && paymentList.size() > 0) {
                    List<PaymentBean> paymentBeanList = new ArrayList<>();
                    for (Payment payment : paymentList) {
                        PaymentBean paymentBean = new PaymentBean();
                        paymentBean.setId(payment.getId());
                        paymentBean.setUserName(payment.getUser().getFullName());
                        paymentBean.setUserCnicNo(payment.getUser().getCnicNo());
                        paymentBean.setCurrentDate(CommonUtil.changeTimestampToString(payment.getCreatedDate()));
                        paymentBean.setPaymentMethod(payment.getPaymentCustomerInfo().getPaymentMethod().toString());

                        if (payment.getProductType() == ProductType.TAXFORM) {
                            if (payment.getTaxform() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                                paymentBean.setDescription("Return file " + payment.getTaxform().getTaxformYear().getYear() + "");
                                paymentBean.setAmount(payment.getSettingPayment().getAmount());
                                paymentBean.setTaxformId(payment.getTaxform().getId());
                                paymentBean.setFileStatus(payment.getTaxform().getStatus().getStatus());
                                if (payment.getSettingPayment().getPromoCode() != null) {
                                    paymentBean.setPromoCode(payment.getSettingPayment().getPromoCode().getPromoCode());
                                }

                                Assign assign = assignServices.checkIfTaxformIsAssignedToLawyer(payment.getTaxform());
                                if (assign != null) {
                                    paymentBean.setLawyerName(assign.getLawyer().getFullName());
                                }
                            }
                        } else if (payment.getProductType() == ProductType.NTN) {
                            if (payment.getFbrUserAccountInfo() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                                paymentBean.setDescription("NTN Registration");
                                paymentBean.setAmount(payment.getSettingPayment().getAmount());
                                paymentBean.setFbrId(payment.getFbrUserAccountInfo().getId());
                                paymentBean.setFileStatus(payment.getFbrUserAccountInfo().getFbrUserAccountInfoStatus().name());
                                if (payment.getSettingPayment().getPromoCode() != null) {
                                    paymentBean.setPromoCode(payment.getSettingPayment().getPromoCode().getPromoCode());
                                }
                                Assign assign = assignServices.checkIfFbrUserAccountInfoIsAssignedToLawyer(payment.getFbrUserAccountInfo());
                                if (assign != null) {
                                    paymentBean.setLawyerName(assign.getLawyer().getFullName());
                                }
                            }
                        }
                        if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.UBL) {
                            paymentBean.setTransactionId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgRegistrationResponse().getTransactionId());
                            paymentBean.setOrderId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getOrderId());
                            paymentBean.setApproveId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgFinalizationResponse().getApprovalCode());
                        } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.SIMSIM) {
                            paymentBean.setTransactionId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getFinjaPaymentToMerchantResponse().getTransactionCode());
                            paymentBean.setOrderId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getOrderId());
                        } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.PROMO_CODE) {
                            paymentBean.setTransactionId(payment.getPaymentCustomerInfo().getPromoCode().getPromoCode());
                            paymentBean.setOrderId(payment.getPaymentCustomerInfo().getOrderId());
                        } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.KEENU) {
                            paymentBean.setTransactionId(payment.getPaymentCustomerInfo().getKeenuRequest().getKeenuResponse().getTransactionId());
                            paymentBean.setOrderId(payment.getPaymentCustomerInfo().getOrderId());
                        }

                        paymentBeanList.add(paymentBean);
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(paymentBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/corporate", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxformForCorporateCustomers(@RequestBody TaxformBean taxformBean) {
        if (taxformBean != null) {
            try {
                User user;
                if (taxformBean.getUserId() != null) {
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                }

                List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                if (taxformList != null) {
                    List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
                    for (Taxform taxform : taxformList) {
                        // TODO not sure about this condition
                        // TODO User Role
                        /*if (taxform != null && taxform.getStatus() != null && (taxform.getStatus().getId() != 0 || taxform.getStatus().getId() != 1)) {
                            // Return only customer taxforms
                            if (taxform.getUser() != null && taxform.getUser().getUserType() != null && taxform.getUser().getUserType().getId() == 2) {

                                if (taxform.getTaxformYear() != null) {
                                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();

                                    taxformMinimalBean.setTaxformId(taxform.getId());
                                    taxformMinimalBean.setYearId(taxform.getTaxformYear().getId());
                                    taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());
                                    taxformMinimalBean.setCnic(taxform.getCnic());
                                    taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                                    taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                                    taxformMinimalBean.setDateDifference(CommonUtil.daysBetweenTwoDates(CommonUtil.getCurrentTimestamp(),taxform.getCurrentDate()));

                                    Payment payment = paymentServices.checkForTaxformPayment(taxform);
                                    if (payment != null) {
                                        taxformBean.setAmount(Double.parseDouble(payment.getSettingTaxformPayment().getAmount() + ""));
                                        if (payment.getPaymentCustomerInfo().getPaymentMethod().getId() == 1) {
                                            taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgRegistrationResponse().getTransactionId());
                                        } else if (payment.getPaymentCustomerInfo().getPaymentMethod().getId() == 2) {
                                            taxformBean.setTransactionId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getFinjaPaymentToMerchantResponse().getTransactionCode());
                                        }
                                    }
                                    taxformMinimalBeanList.add(taxformMinimalBean);
                                }
                            }
                        }*/
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(taxformMinimalBeanList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @RequestMapping(value = "/individual/chart", produces = "application/json", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> showData(@RequestBody TaxformBean taxformBean) throws Exception {
        if (taxformBean != null) {
            try {
                User user;
                if (taxformBean.getUserId() != null) {
                    /*user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
                }

                IndividulChartBean individulChartBean = new IndividulChartBean();

                List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                if (taxformList != null) {
                    Integer totalTaxform = 0;
                    Integer complete = 0;
                    Integer incomplete = 0;
                    Integer ntn = 0;
                    Integer signUp = 0;
                    for (Taxform taxform : taxformList) {
                        if (taxform.getUser() != null && usersServices.checkIfUserIsCustomer(taxform.getUser())) {
                            if (taxform.getStatus() != null) {
                                totalTaxform++;
                                if (taxform.getStatus().getId() == 7) {
                                    complete++;
                                } else if (taxform.getStatus().getId() < 7) {
                                    incomplete++;
                                }
                            }
                        }
                    }
                    List<FbrUserAccountInfo> fbrUserAccountInfoList = fbrUserAccountInfoServices.findAllByUserRoleAndStatus(user);
                    for (FbrUserAccountInfo fbrUserAccountInfo : fbrUserAccountInfoList) {
                        if (fbrUserAccountInfo.getUser() != null && usersServices.checkIfUserIsCustomer(fbrUserAccountInfo.getUser())) {
                            if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() != null) {
                                if (fbrUserAccountInfo.getFbrUserAccountInfoStatus().getId() == 6) {
                                    ntn++;
                                }
                               /*  else if (taxform.getStatus().getId() < 7) {
                                    incomplete++;
                                }*/
                            }
                        }
                    }

                    List<User> userList = usersServices.findAllActiveCustomers();
                    if (userList != null) {
                        for (User customer : userList) {
                            if (customer.getCnicNo() != null && usersServices.checkIfUserIsCustomer(customer)) {
                                signUp++;
                            }
                        }
                    }

                    Double totalAmount = 0.0;
                    List<Payment> paymentList = paymentServices.findAllPayments();
                    for (Payment payment : paymentList) {
                        if (payment.getProductType() == ProductType.TAXFORM) {
                            totalAmount += payment.getSettingPayment().getAmount();
                        }
                        if (payment.getProductType() == ProductType.NTN) {
                            totalAmount += payment.getSettingPayment().getAmount();
                        }
                    }
                    individulChartBean.setTotalTaxform(totalTaxform);
                    individulChartBean.setTotalAmount(totalAmount);
                    individulChartBean.setTaxformComplete(complete);
                    individulChartBean.setTaxformIncomplete(incomplete);
                    individulChartBean.setNtn(ntn);
                    individulChartBean.setSignUp(signUp);
                }
                return new ResponseEntity<>(individulChartBean, HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }
}
