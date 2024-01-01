package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.controller.taxform.TaxformController;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.PromoCodeType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.PromoCodeServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private TaxformServices taxformServices;
    private SettingsServices settingsServices;
    private PromoCodeServices promoCodeServices;
    private PaymentCartServices paymentCartServices;
    private TaxformYearsServices taxformYearsServices;
    private NotificationServices notificationServices;
    private CorporateEmployeeServices corporateEmployeeServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Autowired
    public PaymentController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, SettingsServices settingsServices, PromoCodeServices promoCodeServices, PaymentCartServices paymentCartServices, TaxformYearsServices taxformYearsServices, NotificationServices notificationServices, CorporateEmployeeServices corporateEmployeeServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.taxformServices = taxformServices;
        this.settingsServices = settingsServices;
        this.promoCodeServices = promoCodeServices;
        this.paymentCartServices = paymentCartServices;
        this.taxformYearsServices = taxformYearsServices;
        this.notificationServices = notificationServices;
        this.corporateEmployeeServices = corporateEmployeeServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> promoCode(@RequestBody PaymentCustomerInfoBean paymentCustomerInfoBean) throws Exception {

        User user = usersServices.getUserFromToken();
        if (user == null)
            return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);
        try {

            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getCustomer())) { return new ResponseEntity<>(new Status(0, "Please enter customer name."), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getMobileNo())) { return new ResponseEntity<>(new Status(0, "Please enter mobile no."), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getEmailAddress())) { return new ResponseEntity<>(new Status(0, "Please enter email address"), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getResidentialAddress())) { return new ResponseEntity<>(new Status(0, "Please enter residential address"), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getBillingAddress())) { return new ResponseEntity<>(new Status(0, "Please enter billing address"), HttpStatus.OK); }
            if (paymentCustomerInfoBean.getPaymentCartBeanList() == null || paymentCustomerInfoBean.getPaymentCartBeanList().size() < 0) { return new ResponseEntity<>(new Status(0, "Empty cart"), HttpStatus.OK); }

            PromoCode promoCode = promoCodeServices.findOneActiveRecordByPromoCode("PAYMENTSKIP");
            if (promoCode == null) { return new ResponseEntity<>(new Status(0, "Promo Code expired"), HttpStatus.OK); }
            if (promoCode.getPromoCodeType() != PromoCodeType.CASH) { return new ResponseEntity<>(new Status(0, "Promo Code expired"), HttpStatus.OK); }

            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new Status(0, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new Status(0, "Max attempts"), HttpStatus.OK);
                    }
                }
            }

            List<PaymentCart> paymentCartList = new ArrayList<>();
            Double amount = 0.0;
            for (PaymentCartBean paymentCartBean : paymentCustomerInfoBean.getPaymentCartBeanList()) {
                if (paymentCartBean.getPaymentCartId() != null) {
                    PaymentCart paymentCart = paymentCartServices.findOneByIdAndActiveStatus(paymentCartBean.getPaymentCartId());
                    if (paymentCart != null && paymentCart.getProductType() != null) {
                        paymentCartList.add(paymentCart);
                        if (paymentCart.getProductType() == ProductType.TAXFORM) {
                            if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new Status(0, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new Status(0, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new Status(0, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new Status(0, "Payment cart error"), HttpStatus.OK);
                }
            }

            if (amount.compareTo(0.0) != 0) {
                usersServices.increasePaymentCountByOne(user, "Amount is greated than 0");
                return new ResponseEntity<>(new Status(0, "Amount is greated than 0"), HttpStatus.OK);
            }

            /*Save Customer Info data*/
            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.PROMO_CODE, paymentCustomerInfoBean, promoCode);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new Status(0, "Customer Info Error"), HttpStatus.OK);
            }

            /*Generate Order ID*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() /*+ "-"*/
                    + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
                    + String.format("%05d", paymentCustomerInfo.getId());
            paymentCustomerInfo.setOrderId(orderId);

            paymentCustomerInfoServices.update(paymentCustomerInfo);

            usersServices.resetPaymentCount(user, "Promo Code Successfull Payment ");
            paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);
            List<PaymentCart> updatedPaymentCart = paymentCartServices.saveOrUpdateToPaymentStatus(paymentCartList);
            if (updatedPaymentCart != null && updatedPaymentCart.size() > 0) {
                for (PaymentCart paymentCart : paymentCartList) {
                    Payment payment = new Payment();

                    payment.setUser(user);
                    payment.setProductType(paymentCart.getProductType());
                    payment.setPaymentCustomerInfo(paymentCustomerInfo);
                    if (paymentCart.getProductType() == ProductType.TAXFORM) {
                        if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                            payment.setTaxform(paymentCart.getTaxform());
                            payment.setSettingPayment(paymentCart.getSettingPayment());

                            taxformServices.updateTaxformToAccountantStatus(paymentCart.getTaxform());
                        }
                    } else if (paymentCart.getProductType() == ProductType.NTN) {
                        if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                            payment.setFbrUserAccountInfo(paymentCart.getFbrUserAccountInfo());
                            payment.setSettingPayment(paymentCart.getSettingPayment());

                            fbrUserAccountInfoServices.updateFbrUserAccountInfoToAccountantStatus(paymentCart.getFbrUserAccountInfo());
                        }
                    }
                    paymentServices.save(payment);
                }
            }

            Notification notification = new Notification();
            notification.setArchiveFlag(0);
            notification.setReadFlag(0);
            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
            notification.setToUser(user.getId());
            notification.setNotificationTitle("Payment Receipt from BeFiler.com");
            notification.setNotificationDescription("We have successfully received your payment of PKR " + amount);

            notificationServices.save(notification);

            EmailSender.sendEmail(EmailUtil.paymentTemaplateForPromoCode(user.getFullName(), amount + "", paymentCustomerInfo.getOrderId(), "Payment Received Successfully"), "Payment Receipt from BeFiler.com", user.getEmailAddress());

            return new ResponseEntity<>(new Status(1, "Successfull"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());
            return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/checkForScreen", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> paymentCheckForScreen(@RequestBody InputBean inputBean) throws Exception {
        try {
            if (inputBean != null && inputBean.getUserId() != null/* && inputBean.getTaxformId() != null*/) {
                /*Taxform taxform = taxformRepository.findOne(inputBean.getTaxformId());*/
                /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);

                if (/*taxform != null && */user != null && user.getCnicNo() != null) {
                    CorporateEmployee corporateEmployee = corporateEmployeeServices.findCorporateEmployeeByCnicNo(user.getCnicNo());

                    if (corporateEmployee != null && corporateEmployee.getCorporate() != null
                            && corporateEmployee.getPaymentByCorporate() != null && corporateEmployee.getPaymentByCorporate()) {


                        // TODO change to accountant
                        //taxform.setStatus(taxformServices.findTaxfromStatusById(5));
                        /*taxform.setStatus(statusServices.findOneByLawyerNewStatus());*/

                        //TODO
                        /*taxform.setStatus(statusServices.findOneByAccountantStatus());
                        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
                        taxformRepository.save(taxform);*/
                        return new ResponseEntity<>(new Status(0, "Thank you"), HttpStatus.OK);

                    } else {
                        return new ResponseEntity<>(new Status(1, "Successful"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data ::: "), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0, "Incomplete Data ::: "), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    /*@RequestMapping(produces ="application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> savePayment(@RequestBody InputBean inputBean) throws Exception {
        try {
            if (inputBean != null && inputBean.getUserId() != null && inputBean.getTaxformId() != null) {
                Taxform taxform = taxformRepository.findOne(inputBean.getTaxformId());
                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));

                if (taxform != null && user != null && user.getCnicNo() != null) {
                   // taxform.setStatus(taxformServices.findTaxfromStatusById(5));
                   taxform.setStatus(statusServices.findOneByLawyerNewStatus());
                    taxformRepository.save(taxform);
                    return new ResponseEntity<>(new Status(1, "SettingTaxformPayment Successfully Saved"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data ::: "), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0, "Incomplete Data ::: "), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }*/

    // getByYear

    /*@RequestMapping(value = "/getByTaxformYear",produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getByYear(@RequestBody InputBean inputBean)throws Exception{
        if(inputBean != null && inputBean.getUserId() != null){
            User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));

            List<PaymentCartBean> paymentCartBeanList = new ArrayList<>();
            List<PaymentCart> paymentCartList = paymentCartServices.findAllActivePaymentCartsByUser(user);
            if (paymentCartList != null && paymentCartList.size() > 0) {
                for (PaymentCart paymentCart : paymentCartList) {
                    if (paymentCart != null && paymentCart.getProductType() != null) {
                        PaymentCartBean paymentCartBean = new PaymentCartBean();
                        if (paymentCart.getProductType() == 1) {
                            if (paymentCart.getTaxform() != null && paymentCart.getSettingTaxformPayment() != null) {
                                Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                if (payment == null) {
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription("Return file " + paymentCart.getTaxform().getTaxformYear().getYear() + "");
                                    paymentCartBean.setAmount(paymentCart.getSettingTaxformPayment().getAmount() + "");
                                    paymentCartBean.setTaxformId(paymentCart.getTaxform().getId() + "");
                                    paymentCartBean.setRemove(true);

                                    paymentCartBeanList.add(paymentCartBean);
                                } else {
                                    // TODO
                                }
                            }
                        } else if (paymentCart.getProductType() == 2) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingFbrPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription("NTN Registration");
                                    paymentCartBean.setAmount(paymentCart.getSettingFbrPayment().getAmount() + "");
                                    paymentCartBean.setFbrId(paymentCart.getFbrUserAccountInfo().getId() + "");
                                    paymentCartBean.setRemove(false);

                                    paymentCartBeanList.add(paymentCartBean);
                                } else {
                                    //TODO
                                }
                            }
                        }
                    }
                }
            }

            *//*Taxform taxform = taxformServices.findOne(inputBean.getTaxformId());

            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0,"Session expired."),HttpStatus.OK);
            }

            if (taxform == null) {
                return new ResponseEntity<>(new StatusBean(0,"Invalid Tax form"),HttpStatus.OK);
            }

            if (taxform.getTaxformYear() == null) {
                return new ResponseEntity<>(new StatusBean(0,"Invalid Tax form year"),HttpStatus.OK);
            }

            TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatus(taxform.getTaxformYear().getId(), appStatusServices.findOneByActiveStatus());
            if (taxformYears == null) {
                return new ResponseEntity<>(new StatusBean(0,"Payment is not defined for this year. Please contact support team."),HttpStatus.OK);
            }

            SettingTaxformPayment settingTaxformPayment = settingTaxformPaymentServices.findOneByTaxformYearAndStatus(taxformYears, appStatusServices.findOneByActiveStatus());
            if (settingTaxformPayment == null) {
                return new ResponseEntity<>(new StatusBean(0,"Payment is not defined for this year. Please contact support team."),HttpStatus.OK);
            }

            TaxformPaymentBean taxformPaymentBean = new TaxformPaymentBean();
            taxformPaymentBean.setTaxformPaymentId(settingTaxformPayment.getId());
            taxformPaymentBean.setDescription(taxformYears.getYear() + "");
            taxformPaymentBean.setAmount(settingTaxformPayment.getAmount() + "");

            List<TaxformPaymentBean> paymentBeanList = new ArrayList<>();
            paymentBeanList.add(taxformPaymentBean);

            FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findByUserIdAndStatus(user.getId(), true);
            if (fbrUserAccountInfo == null) {
                SettingFbrPayment settingFbrPayment = settingFbrPaymentServices.findOneByStatus(appStatusServices.findOneByActiveStatus());
                if (settingFbrPayment == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Payment for FBR registration is not defined. Please contact support team."),HttpStatus.OK);
                }
                TaxformPaymentBean fbrPaymentBean = new TaxformPaymentBean();
                fbrPaymentBean.setFbrPaymentId(settingFbrPayment.getId());
                fbrPaymentBean.setDescription("Fbr Registration");
                fbrPaymentBean.setAmount(settingFbrPayment.getAmount() + "");

                paymentBeanList.add(fbrPaymentBean);
            }*//*

            StatusBean statusBean = new StatusBean(1, "Successfully");
            statusBean.setResponse(paymentCartBeanList);

            return new ResponseEntity<>(statusBean,HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusBean(0,"incomplete data."),HttpStatus.OK);
    }*/

    /*@RequestMapping(value = "/savePayment", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> create(@RequestBody PaymentBean paymentBean) throws Exception {
        if (paymentBean != null) {
            *//*User user = usersServices.findOneByIdAndStatus(paymentBean.getUserId(), usersServices.findUserStatusById(1));*//*
            User user = usersServices.findOneByIdAndStatus(paymentBean.getUserId(), UserStatus.ACTIVE);
            *//*TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatus(paymentBean.getTaxformYearId(), appStatusServices.findOneByActiveStatus());*//*
            TaxformYears taxformYears = taxformYearsServices.findOneByIdAndStatus(paymentBean.getTaxformYearId(), AppStatus.ACTIVE);
            if (user != null && taxformYears != null) {
                SettingTaxformPayment settingTaxformPayment = new SettingTaxformPayment();
                settingTaxformPayment.setUser(user);
                settingTaxformPayment.setTaxformYear(taxformYears);
                settingTaxformPayment.setAmount(paymentBean.getAmount());
                settingTaxformPayment.setStatus(AppStatus.ACTIVE);
                settingTaxformPayment.setCurrentDate(CommonUtil.getCurrentTimestamp());
                settingTaxformPaymentServices.save(settingTaxformPayment);
                return new ResponseEntity<>(new StatusBean(1,"Successfully created"),HttpStatus.OK) ;
            }

            return new ResponseEntity<>(new StatusBean(0, "User not found! or Invalid year"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }*/

    @RequestMapping(value = "/getByUser", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getPaymentTransactionsOfUser(@RequestBody PaymentBean paymentBean) throws Exception {
        if (paymentBean != null) {
            User user;
            if (paymentBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(paymentBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Session Expired"), HttpStatus.OK);
            }

            List<Payment> paymentList = paymentServices.findAllByUser(user);
            List<PaymentBean> paymentBeanList = new ArrayList<>();
            if (paymentList != null && paymentList.size() > 0) {
                for (Payment payment : paymentList) {
                    PaymentBean paymentBean1 = new PaymentBean();

                    paymentBean1.setId(payment.getId());
                    paymentBean1.setPaymentDate(CommonUtil.changeTimestampToString(payment.getCreatedDate()));
                    if (payment.getProductType() == ProductType.TAXFORM) {
                        if (payment.getTaxform() != null && payment.getSettingPayment() != null) {
                            paymentBean1.setDescription(payment.getTaxform().getTaxformYear().getYear() + "");
                            paymentBean1.setDetail(payment.getTaxform().getTaxformYear().getYear() + " Tax will be filed on your behalf with Federal Board of Revenue(FBR)");
                            paymentBean1.setAmount(payment.getSettingPayment().getAmount());
                            paymentBean1.setTaxformId(payment.getTaxform().getId());
                        }
                    } else if (payment.getProductType() == ProductType.NTN) {
                        if (payment.getFbrUserAccountInfo() != null && payment.getSettingPayment() != null) {
                            paymentBean1.setDescription("NTN");
                            paymentBean1.setDetail("BeFiler will register your National Tax Number(NTN) with FBR on your behalf.");
                            paymentBean1.setAmount(payment.getSettingPayment().getAmount());
                            paymentBean1.setFbrId(payment.getFbrUserAccountInfo().getId());
                        }
                    }
                    paymentBean1.setPaymentMethod(payment.getPaymentCustomerInfo().getPaymentMethod().toString());
                    if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.UBL) {
                        paymentBean1.setTransactionId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgRegistrationResponse().getTransactionId());
                        paymentBean1.setOrderId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getOrderId());
                        paymentBean1.setApproveId(payment.getPaymentCustomerInfo().getIpgRegistrationRequest().getIpgFinalizationResponse().getApprovalCode());
                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.SIMSIM) {
                        paymentBean1.setTransactionId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getFinjaPaymentToMerchantResponse().getTransactionCode());
                        paymentBean1.setOrderId(payment.getPaymentCustomerInfo().getFinjaGetCustomerInfoRequest().getOrderId());
                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.PROMO_CODE) {
                        paymentBean1.setTransactionId(payment.getPaymentCustomerInfo().getPromoCode().getPromoCode());
                        paymentBean1.setOrderId(payment.getPaymentCustomerInfo().getOrderId());
                    } else if (payment.getPaymentCustomerInfo().getPaymentMethod() == PaymentMethod.KEENU) {
                        paymentBean1.setTransactionId(payment.getPaymentCustomerInfo().getKeenuRequest().getKeenuResponse().getTransactionId());
                        paymentBean1.setOrderId(payment.getPaymentCustomerInfo().getOrderId());
                    }
                    paymentBeanList.add(paymentBean1);
                }
            }

            StatusBean statusBean = new StatusBean(1, "Successfull");
            statusBean.setResponse(paymentBeanList);

            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }
}
