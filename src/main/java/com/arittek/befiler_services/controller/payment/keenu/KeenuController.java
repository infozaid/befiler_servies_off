package com.arittek.befiler_services.controller.payment.keenu;

import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.beans.payment.keenu.KeenuRequestBean;
import com.arittek.befiler_services.beans.payment.keenu.KeenuResponseBean;
import com.arittek.befiler_services.model.payment.keenu.KeenuRequest;
import com.arittek.befiler_services.model.payment.keenu.KeenuResponse;
import com.arittek.befiler_services.services.payment.keenu.KeenuRequestServices;
import com.arittek.befiler_services.services.payment.keenu.KeenuResponseServices;
import com.arittek.befiler_services.util.payment.keenu.KeenuUtil;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping(value = "/keenu")
public class KeenuController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private TaxformServices taxformServices;
    private SettingsServices settingsServices;
    private PaymentCartServices paymentCartServices;
    private KeenuRequestServices keenuRequestServices;
    private NotificationServices notificationServices;
    private KeenuResponseServices keenuResponseServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Value("${keenu.merchantId}")
    private String KEENU_MERCHANT_ID;
    @Value("${keenu.secretKey}")
    private String KEENU_SECRET_KEY;
    @Value("${keenu.url}")
    private String KEENU_URL;

    @Autowired
    public KeenuController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, KeenuRequestServices keenuRequestServices, NotificationServices notificationServices, KeenuResponseServices keenuResponseServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.taxformServices = taxformServices;
        this.settingsServices = settingsServices;
        this.paymentCartServices = paymentCartServices;
        this.keenuRequestServices = keenuRequestServices;
        this.notificationServices = notificationServices;
        this.keenuResponseServices = keenuResponseServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/request", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<KeenuRequestBean> keenuRequest(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {

        User user = null;
        try {

            if (!StringUtils.isNotEmpty(requestBean.getCustomer())) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Please enter name."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Please enter mobile no."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Please enter email address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Please enter residential address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Please enter billing address"), HttpStatus.OK);
            }

            if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Empty cart"), HttpStatus.OK);
            }

            if (requestBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new KeenuRequestBean(8, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new KeenuRequestBean(8, "Session Expired"), HttpStatus.OK);
            }


            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new KeenuRequestBean(8, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new KeenuRequestBean(8, "Max attempts"), HttpStatus.OK);
                    }
                }
            }

            List<PaymentCart> paymentCartList = new ArrayList<>();
            Double amount = 0.0;
            for (PaymentCartBean paymentCartBean : requestBean.getPaymentCartBeanList()) {
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
                                    return new ResponseEntity<>(new KeenuRequestBean(8, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new KeenuRequestBean(8, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new KeenuRequestBean(8, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new KeenuRequestBean(8, "Payment cart error"), HttpStatus.OK);
                }
            }


            /*Save Customer Info data*/
            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.KEENU, requestBean);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new KeenuRequestBean(8, "Customer Info Error"), HttpStatus.OK);
            }

            /*Generate Order ID*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() /*+ "-"*/
                    + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
                    + String.format("%05d", paymentCustomerInfo.getId());
            paymentCustomerInfo.setOrderId(orderId);

            paymentCustomerInfo = paymentCustomerInfoServices.update(paymentCustomerInfo);

            /*Keenu Request for Database*/
            KeenuRequest keenuRequest = new KeenuRequest();
            keenuRequest.setPaymentCustomerInfo(paymentCustomerInfo);

            keenuRequest.setKeenuUrl(KEENU_URL);
            keenuRequest.setMerchantId(KEENU_MERCHANT_ID);
            keenuRequest.setSecretKey(KEENU_SECRET_KEY);

            keenuRequest.setTransactionDescription("Digital case");

            keenuRequest.setCurrency("PKR");
            keenuRequest.setOrderNo(paymentCustomerInfo.getOrderId());
            keenuRequest.setOrderAmount(String.format("%.2f", Double.parseDouble(amount + "")));

            keenuRequest.setDate(KeenuUtil.getKeenuDate());
            keenuRequest.setTime(KeenuUtil.getKeenuTime());

            keenuRequest.setChecksum(KeenuUtil.generateRequestCheckSum(KEENU_SECRET_KEY, KEENU_MERCHANT_ID, keenuRequest.getOrderNo(), String.format("%.2f", Double.parseDouble(keenuRequest.getOrderAmount())), keenuRequest.getDate(), keenuRequest.getTime()));

            keenuRequest = keenuRequestServices.saveOrUpdate(keenuRequest);
            if (keenuRequest == null) {
                usersServices.increasePaymentCountByOne(user, "Keenu Request for Database Error");
                return new ResponseEntity<>(new KeenuRequestBean(8, "Keenu Request for Database Error"), HttpStatus.OK);
            }

            usersServices.resetPaymentCount(user, "Successful Keenu Request ");
            paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

            KeenuRequestBean keenuRequestBean = new KeenuRequestBean(9, "Successfull");

            keenuRequestBean.setUrl(keenuRequest.getKeenuUrl());
            keenuRequestBean.setMerchantId(keenuRequest.getMerchantId());
            keenuRequestBean.setOrderNo(keenuRequest.getOrderNo());
            keenuRequestBean.setOrderAmount(keenuRequest.getOrderAmount());
            keenuRequestBean.setChecksum(keenuRequest.getChecksum());
            keenuRequestBean.setDate(keenuRequest.getDate());
            keenuRequestBean.setTime(keenuRequest.getTime());
            keenuRequestBean.setTransactionDesc(keenuRequest.getTransactionDescription());

            return new ResponseEntity<>(keenuRequestBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());
            return new ResponseEntity<>(new KeenuRequestBean(8, "Incomplete data"), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/response", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<KeenuResponseBean> keenuResponse(@RequestBody KeenuResponseBean responseBean) throws Exception {

        /*System.out.println(responseBean.getStatus());
        System.out.println(responseBean.getAuthStatusNo());
        System.out.println(responseBean.getOrderNo());
        System.out.println(responseBean.getTransactionId());
        System.out.println(responseBean.getCheckSum());
        System.out.println(responseBean.getBankName());
        System.out.println(responseBean.getDate());
        System.out.println(responseBean.getTime());

        return new ResponseEntity<>(new KeenuResponseBean(1, "Successfull") , HttpStatus.OK);*/

        try {

            if (!StringUtils.isNotEmpty(responseBean.getStatus())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : Status"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getAuthStatusNo())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : AuthStatusNo"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getOrderNo())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : OrderNo"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getTransactionId())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : TransactionId"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getCheckSum())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : CheckSum"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getBankName())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : BankName"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getDate())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : Date"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(responseBean.getTime())) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Information : Time"), HttpStatus.OK);
            }

            User user = usersServices.findOneByIdAndStatus(responseBean.getUserId(), UserStatus.ACTIVE);
            if (user == null) {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Session Expired"), HttpStatus.OK);
            }

            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneByOrderId(responseBean.getOrderNo());
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                return new ResponseEntity<>(new KeenuResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
            }

            KeenuRequest keenuRequest = paymentCustomerInfo.getKeenuRequest();
            if (keenuRequest == null) {
                usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                return new ResponseEntity<>(new KeenuResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
            }

            List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
            if (paymentCartList == null || paymentCartList.size() <= 0) {
                usersServices.increasePaymentCountByOne(user, "Finalize :: Can't find cart");
                return new ResponseEntity<>(new KeenuResponseBean(0, "Can't find cart"), HttpStatus.OK);
            }

            KeenuResponse keenuResponse = new KeenuResponse();
            keenuResponse.setKeenuRequest(keenuRequest);

            keenuResponse.setStatus(responseBean.getStatus());
            keenuResponse.setAuthStatusNo(responseBean.getAuthStatusNo());
            keenuResponse.setOrderNo(responseBean.getOrderNo());
            keenuResponse.setTransactionId(responseBean.getTransactionId());
            keenuResponse.setChecksum(responseBean.getCheckSum());
            keenuResponse.setBankName(responseBean.getBankName());
            keenuResponse.setDate(responseBean.getDate());
            keenuResponse.setTime(responseBean.getTime());

            keenuResponseServices.saveOrUpdate(keenuResponse);

            Boolean checkSum = KeenuUtil.verifyCheckSum(KEENU_SECRET_KEY, KEENU_MERCHANT_ID, keenuResponse.getOrderNo(), String.format("%.2f", Double.parseDouble(keenuRequest.getOrderAmount())), keenuResponse.getBankName(), keenuResponse.getStatus(), keenuResponse.getDate(), keenuResponse.getTime(), keenuResponse.getChecksum());
            if (checkSum == true) {

                if (keenuResponse.getAuthStatusNo().equals("00")) {
                    return new ResponseEntity<>(new KeenuResponseBean(0, "System Error"), HttpStatus.OK);
                } else if (keenuResponse.getAuthStatusNo().equals("01")) {
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
                    } else {

                    }

                    usersServices.resetPaymentCount(user, "Keenu Payment");

                    Notification notification = new Notification();
                    notification.setArchiveFlag(0);
                    notification.setReadFlag(0);
                    notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                    notification.setToUser(user.getId());
                    notification.setNotificationTitle("Payment Receipt from BeFiler.com");
                    notification.setNotificationDescription("We have successfully received your payment thru Keenu of PKR " + keenuRequest.getOrderAmount());

                    notificationServices.save(notification);

                    EmailSender.sendEmail(EmailUtil.paymentTemaplateForKeenu(user.getFullName(), keenuRequest.getOrderAmount() + "", keenuRequest.getOrderNo(), keenuResponse.getTransactionId(), "Payment received successfully"), "Payment Receipt from BeFiler.com", user.getEmailAddress());

                    return new ResponseEntity<>(new KeenuResponseBean(1, "Successfull"), HttpStatus.OK);
                } else if (keenuResponse.getAuthStatusNo().equals("02")) {
                    return new ResponseEntity<>(new KeenuResponseBean(0, "Order canceled by customer"), HttpStatus.OK);
                } else if (keenuResponse.getAuthStatusNo().equals("03")) {
                    return new ResponseEntity<>(new KeenuResponseBean(0, "Declined by Bank"), HttpStatus.OK);
                }
                return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete Data"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new KeenuResponseBean(0, "Invalid Response :: Data doesn't match"), HttpStatus.OK);
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new KeenuResponseBean(0, "Incomplete data"), HttpStatus.OK);
        }

    }
}
