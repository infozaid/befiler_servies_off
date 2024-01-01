package com.arittek.befiler_services.controller.payment.easypaisa;

import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.beans.payment.easypaisa.EasypaisaConfirmBean;
import com.arittek.befiler_services.beans.payment.easypaisa.EasypaisaIndexBean;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaConfirm;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIndex;
import com.arittek.befiler_services.services.payment.easypaisa.EasypaisaConfirmServices;
import com.arittek.befiler_services.services.payment.easypaisa.EasypaisaIndexServices;
import com.arittek.befiler_services.util.payment.easypaisa.EasypaisaUtil;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
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
@RequestMapping(value = "/easypaisa")
public class EasypaisaRedirectionController {

    private final UsersServices usersServices;
    private final PaymentServices paymentServices;
    private final TaxformServices taxformServices;
    private final SettingsServices settingsServices;
    private final PaymentCartServices paymentCartServices;
    private final NotificationServices notificationServices;
    private final EasypaisaIndexServices easypaisaIndexServices;
    private final EasypaisaConfirmServices easypaisaConfirmServices;
    private final FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private final PaymentCustomerInfoServices paymentCustomerInfoServices;


    @Value("${easypaisa.redirect.storeId}")
    private String STORE_ID;
    @Value("${easypaisa.index.url}")
    private String INDEX_URL;
    @Value("${easypaisa.confirm.url}")
    private String CONFIRM_URL;
    @Value("${easypaisa.redirect.hashkey}")
    private String HASH_KEY;

    @Autowired
    public EasypaisaRedirectionController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, NotificationServices notificationServices, EasypaisaIndexServices easypaisaIndexServices, EasypaisaConfirmServices easypaisaConfirmServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.taxformServices = taxformServices;
        this.settingsServices = settingsServices;
        this.paymentCartServices = paymentCartServices;
        this.notificationServices = notificationServices;
        this.easypaisaIndexServices = easypaisaIndexServices;
        this.easypaisaConfirmServices = easypaisaConfirmServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/index", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<EasypaisaIndexBean> redirectToIndex(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {

        User user = null;
        try {

            if (!StringUtils.isNotEmpty(requestBean.getCustomer())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Please enter name."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Please enter mobile no."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Please enter email address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Please enter residential address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Please enter billing address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getReturnPath())) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Postback URL is missing"), HttpStatus.OK);
            }

            if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Empty cart"), HttpStatus.OK);
            }

            if (requestBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new EasypaisaIndexBean(10, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Session Expired"), HttpStatus.OK);
            }


            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new EasypaisaIndexBean(10, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new EasypaisaIndexBean(10, "Max attempts"), HttpStatus.OK);
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
                                    return new ResponseEntity<>(new EasypaisaIndexBean(10, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new EasypaisaIndexBean(10, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new EasypaisaIndexBean(10, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new EasypaisaIndexBean(10, "Payment cart error"), HttpStatus.OK);
                }
            }


            /*Save Customer Info data*/
            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.EASYPAISA_REDIRECT, requestBean);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Customer Info Error"), HttpStatus.OK);
            }


            /*Generate Order ID*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() /*+ "-"*/
                    + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
                    + String.format("%05d", paymentCustomerInfo.getId());
            paymentCustomerInfo.setOrderId(orderId);

            paymentCustomerInfo = paymentCustomerInfoServices.update(paymentCustomerInfo);

            /*Easypaisa Index for Database*/
            EasypaisaIndex easypaisaIndex = new EasypaisaIndex();
            easypaisaIndex.setPaymentCustomerInfo(paymentCustomerInfo);

            easypaisaIndex.setAmount(String.format("%.1f", Double.parseDouble(amount + "")));
            easypaisaIndex.setStoreId(STORE_ID);
            easypaisaIndex.setRedirectUrl(INDEX_URL);
            easypaisaIndex.setPostBackUrl(requestBean.getReturnPath());
            easypaisaIndex.setOrderRefNum(orderId);
            easypaisaIndex.setPaymentMethod("CC_PAYMENT_METHOD");

            easypaisaIndex.setExpiryDate(EasypaisaUtil.getExpireDate());
            easypaisaIndex.setAutoRedirect("1");
            easypaisaIndex.setEmailAddr(requestBean.getEmailAddress());
            easypaisaIndex.setMobileNum(EasypaisaUtil.mobileNo(requestBean.getMobileNo()));

            easypaisaIndex.setMerchantHashedReq(EasypaisaUtil.getMerchantHashedRequest(HASH_KEY, easypaisaIndex.getStoreId(), easypaisaIndex.getAmount(), easypaisaIndex.getPostBackUrl(), easypaisaIndex.getOrderRefNum(), easypaisaIndex.getExpiryDate(), easypaisaIndex.getAutoRedirect(), easypaisaIndex.getPaymentMethod(), easypaisaIndex.getEmailAddr(), easypaisaIndex.getMobileNum()));

            easypaisaIndex = easypaisaIndexServices.saveOrUpdate(easypaisaIndex);
            if (easypaisaIndex == null) {
                usersServices.increasePaymentCountByOne(user, "Easypaisa Index for Database Error");
                return new ResponseEntity<>(new EasypaisaIndexBean(10, "Easypaisa Index  for Database Error"), HttpStatus.OK);
            }

            usersServices.resetPaymentCount(user, "Successful Easypaisa Index Redirect ");
            paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

            EasypaisaIndexBean easypaisaIndexBean = new EasypaisaIndexBean(11, "Successfull");

            easypaisaIndexBean.setAmount(easypaisaIndex.getAmount());
            easypaisaIndexBean.setStoreId(easypaisaIndex.getStoreId());
            easypaisaIndexBean.setRedirectUrl(easypaisaIndex.getRedirectUrl());
            easypaisaIndexBean.setPostBackUrl(easypaisaIndex.getPostBackUrl());
            easypaisaIndexBean.setOrderRefNum(easypaisaIndex.getOrderRefNum());
            easypaisaIndexBean.setMerchantHashedReq(easypaisaIndex.getMerchantHashedReq());
            easypaisaIndexBean.setPaymentMethod(easypaisaIndex.getPaymentMethod());

            easypaisaIndexBean.setExpiryDate(easypaisaIndex.getExpiryDate());
            easypaisaIndexBean.setAutoRedirect(easypaisaIndex.getAutoRedirect());
            easypaisaIndexBean.setEmailAddr(easypaisaIndex.getEmailAddr());
            easypaisaIndexBean.setMobileNum(easypaisaIndex.getMobileNum());

            return new ResponseEntity<>(easypaisaIndexBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());
            return new ResponseEntity<>(new EasypaisaIndexBean(10, "Incomplete data"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/confirm", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<EasypaisaConfirmBean> easypaisaConfirmResponse(@RequestBody EasypaisaConfirmBean easypaisaConfirmBean) throws Exception {

        try {

            if (easypaisaConfirmBean.getSuccess() == null) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Success"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getBatchNumber())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Batch Number"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getAuthorizeId())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Authorizer Id"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getTransactionNumber())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Transaction Number"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getAmount())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Amount"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getOrderRefNumber())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Order Refrence Number"), HttpStatus.OK);
            }
            if (!StringUtils.isNotEmpty(easypaisaConfirmBean.getTransactionResponse())) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete Information : Transaction Response"), HttpStatus.OK);
            }

            User user = usersServices.findOneByIdAndStatus(easypaisaConfirmBean.getUserId(), UserStatus.ACTIVE);
            if (user == null) {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Session Expired"), HttpStatus.OK);
            }

            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneByOrderId(easypaisaConfirmBean.getOrderRefNumber());
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Confirm :: Can't complete transaction");
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Can't complete transaction"), HttpStatus.OK);
            }

            EasypaisaIndex easypaisaIndex = paymentCustomerInfo.getEasypaisaIndex();
            if (easypaisaIndex == null) {
                usersServices.increasePaymentCountByOne(user, "Confirm :: Can't complete transaction");
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Can't complete transaction"), HttpStatus.OK);
            }

            List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
            if (paymentCartList == null || paymentCartList.size() <= 0) {
                usersServices.increasePaymentCountByOne(user, "Finalize :: Can't find cart");
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Can't find cart"), HttpStatus.OK);
            }

            EasypaisaConfirm easypaisaConfirm = new EasypaisaConfirm();
            easypaisaConfirm.setEasypaisaIndex(easypaisaIndex);

            easypaisaConfirm.setSuccess(easypaisaConfirmBean.getSuccess());
            easypaisaConfirm.setBatchNumber(easypaisaConfirmBean.getBatchNumber());
            easypaisaConfirm.setAuthorizeId(easypaisaConfirmBean.getAuthorizeId());
            easypaisaConfirm.setTransactionNumber(easypaisaConfirmBean.getTransactionNumber());
            easypaisaConfirm.setAmount(easypaisaConfirmBean.getAmount());
            easypaisaConfirm.setOrderRefNumber(easypaisaConfirmBean.getOrderRefNumber());
            easypaisaConfirm.setTransactionResponse(easypaisaConfirmBean.getTransactionResponse());

            easypaisaConfirmServices.saveOrUpdate(easypaisaConfirm);

            if (easypaisaConfirmBean.getSuccess() != null && easypaisaConfirmBean.getSuccess().equalsIgnoreCase("true")) {
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

                usersServices.resetPaymentCount(user, "Easypaisa Redirect");

                Notification notification = new Notification();
                notification.setArchiveFlag(0);
                notification.setReadFlag(0);
                notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                notification.setToUser(user.getId());
                notification.setNotificationTitle("Payment Receipt from BeFiler.com");
                notification.setNotificationDescription("We have successfully received your payment thru Keenu of PKR " + easypaisaIndex.getAmount());

                notificationServices.save(notification);

                EmailSender.sendEmail(EmailUtil.paymentTemaplateForEasypaisaRedirect(user.getFullName(), easypaisaIndex.getAmount() + "", easypaisaIndex.getOrderRefNum(), "Payment received successfully"), "Payment Receipt from BeFiler.com", user.getEmailAddress());

                EasypaisaConfirmBean easypaisaConfirmBean1 = new EasypaisaConfirmBean(1, "Successfull");
                easypaisaConfirmBean1.setTransactionNumber(easypaisaConfirm.getTransactionNumber());
                easypaisaConfirmBean1.setOrderRefNumber(easypaisaConfirm.getOrderRefNumber());
                easypaisaConfirmBean1.setTransactionResponse(easypaisaConfirm.getTransactionResponse());

                return new ResponseEntity<>(easypaisaConfirmBean1, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Failure"), HttpStatus.OK);
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new EasypaisaConfirmBean(0, "Incomplete data"), HttpStatus.OK);
        }

    }
}
