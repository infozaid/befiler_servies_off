package com.arittek.befiler_services.controller.payment.finja;

import com.arittek.befiler_services.beans.payment.finja.FinjaPaymentToMerchantRequestBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaPaymentToMerchantResponseBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaRequestBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaResponseBean;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantRequest;
import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantResponse;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.finja.FinjaGetCustomerInfoRequestServices;
import com.arittek.befiler_services.services.payment.finja.FinjaPaymentToMerchantRequestServices;
import com.arittek.befiler_services.services.payment.finja.FinjaPaymentToMerchantResponseServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping(value = "/finja/otp")
public class FinjaOTPPaymentToMerchantController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private PaymentServices paymentServices;
    private PaymentCartServices paymentCartServices;
    private NotificationServices notificationServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;
    private FinjaGetCustomerInfoRequestServices finjaGetCustomerInfoRequestServices;
    private FinjaPaymentToMerchantRequestServices finjaPaymentToMerchantRequestServices;
    private FinjaPaymentToMerchantResponseServices finjaPaymentToMerchantResponseServices;

    @Value("${finja.otp.customerIdMerchant}")
    private String CUSTOMER_ID_MERCHANT;
    @Value("${finja.otp.paymentToMerchant.url}")
    private String PAYMENT_TO_MERCHANT_URL;

    @Autowired
    public FinjaOTPPaymentToMerchantController(UsersServices usersServices, TaxformServices taxformServices, PaymentServices paymentServices, PaymentCartServices paymentCartServices, NotificationServices notificationServices, PaymentCustomerInfoServices paymentCustomerInfoServices, FinjaGetCustomerInfoRequestServices finjaGetCustomerInfoRequestServices, FinjaPaymentToMerchantRequestServices finjaPaymentToMerchantRequestServices, FinjaPaymentToMerchantResponseServices finjaPaymentToMerchantResponseServices, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.paymentServices = paymentServices;
        this.paymentCartServices = paymentCartServices;
        this.notificationServices = notificationServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
        this.finjaGetCustomerInfoRequestServices = finjaGetCustomerInfoRequestServices;
        this.finjaPaymentToMerchantRequestServices = finjaPaymentToMerchantRequestServices;
        this.finjaPaymentToMerchantResponseServices = finjaPaymentToMerchantResponseServices;
    }


    @RequestMapping(value = "/paymentToMerchant", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FinjaResponseBean> ipgFinalizeService(@RequestBody FinjaRequestBean finjaRequestBean) throws Exception {

        if (finjaRequestBean != null &&
                finjaRequestBean.getRegistrationId() != null &&
                finjaRequestBean.getUserId() != null) {

            User user = null;
            try {

                if (finjaRequestBean.getOtp() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: OTP");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "OTP field can't be empty"), HttpStatus.OK);
                }

                /*user = usersServices.findOneByIdAndStatus(finjaRequestBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(finjaRequestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Session Expired"), HttpStatus.OK);
                }

                PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneById(finjaRequestBean.getRegistrationId());
                if (paymentCustomerInfo == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }


                FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest = paymentCustomerInfo.getFinjaGetCustomerInfoRequest();
                if (finjaGetCustomerInfoRequest == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                if (finjaGetCustomerInfoRequest.getMobileNo() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                if (finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
                Long amount = 0L;
                if (paymentCartList != null && paymentCartList.size() > 0) {
                    for (PaymentCart paymentCart : paymentCartList) {
                        if (paymentCart != null && paymentCart.getProductType() != null) {
                            if (paymentCart.getProductType() == ProductType.TAXFORM) {
                                if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                    Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                    if (payment == null) {
                                        amount += Math.round(paymentCart.getSettingPayment().getAmount());
                                    } else {
                                        usersServices.increasePaymentCountByOne(user, "Register :: Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                        return new ResponseEntity<>(new FinjaResponseBean(0, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                    }
                                }
                            } else if (paymentCart.getProductType() == ProductType.NTN) {
                                if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                    Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                    if (payment == null) {
                                        amount += Math.round(paymentCart.getSettingPayment().getAmount());
                                    } else {
                                        usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                        return new ResponseEntity<>(new FinjaResponseBean(0, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                    }
                                }
                            }
                        } else {
                            usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                            return new ResponseEntity<>(new FinjaResponseBean(0, "Payment cart error"), HttpStatus.OK);
                        }
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't find cart");
                    return new ResponseEntity<>(new FinjaResponseBean(0, "Can't find cart"), HttpStatus.OK);
                }

                FinjaPaymentToMerchantRequest finjaPaymentToMerchantRequest = new FinjaPaymentToMerchantRequest();

                finjaPaymentToMerchantRequest.setCustomerIdMerchant(CUSTOMER_ID_MERCHANT);
                finjaPaymentToMerchantRequest.setMobileNo(finjaGetCustomerInfoRequest.getMobileNo());
                finjaPaymentToMerchantRequest.setAmount(amount + "");
                finjaPaymentToMerchantRequest.setOtp(finjaRequestBean.getOtp());
                finjaPaymentToMerchantRequest.setCustomerId(finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse().getCustomerId());
                finjaPaymentToMerchantRequest.setCustomerIdMerchant(finjaGetCustomerInfoRequest.getCustomerIdMerchant());
                finjaPaymentToMerchantRequest.setCustomerName(finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse().getCustomerName());

                finjaPaymentToMerchantRequest.setFinjaGetCustomerInfoRequest(finjaGetCustomerInfoRequest);

                finjaPaymentToMerchantRequest = finjaPaymentToMerchantRequestServices.saveOrUpdate(finjaPaymentToMerchantRequest);

                finjaGetCustomerInfoRequest.setFinjaStatus(3);
                finjaGetCustomerInfoRequest = finjaGetCustomerInfoRequestServices.saveOrUpdate(finjaGetCustomerInfoRequest);

                if (finjaGetCustomerInfoRequest != null && finjaPaymentToMerchantRequest != null) {
                    FinjaPaymentToMerchantRequestBean finjaPaymentToMerchantRequestBean = new FinjaPaymentToMerchantRequestBean();

                    finjaPaymentToMerchantRequestBean.setCustomerIdMerchant(Long.parseLong(CUSTOMER_ID_MERCHANT));
                    finjaPaymentToMerchantRequestBean.setMobileNo(Long.parseLong(finjaGetCustomerInfoRequest.getMobileNo()));
                    finjaPaymentToMerchantRequestBean.setAmount(Long.parseLong(finjaPaymentToMerchantRequest.getAmount()) * 100);
                    finjaPaymentToMerchantRequestBean.setOtp(Long.parseLong(finjaRequestBean.getOtp()));
                    finjaPaymentToMerchantRequestBean.setCustomerId(Long.parseLong(finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse().getCustomerId()));
                    finjaPaymentToMerchantRequestBean.setCustomerIdMerchant(Long.parseLong(finjaGetCustomerInfoRequest.getCustomerIdMerchant()));
                    finjaPaymentToMerchantRequestBean.setCustomerName(finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse().getCustomerName());

                    /*String url = "http://devmerchant.finpay.pk/public/api/paymentToMerchant";*/
                    HttpHeaders headers = new HttpHeaders();

                    headers.set("FN-Token", finjaGetCustomerInfoRequest.getFinjaGetCustomerInfoResponse().getFnToken());

                    HttpEntity<FinjaPaymentToMerchantRequestBean> request = new HttpEntity<>(finjaPaymentToMerchantRequestBean, headers);
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<FinjaPaymentToMerchantResponseBean> finjaPaymentToMerchantResponseBean = restTemplate.postForEntity(PAYMENT_TO_MERCHANT_URL, request, FinjaPaymentToMerchantResponseBean.class);

                    if (finjaPaymentToMerchantResponseBean == null || finjaPaymentToMerchantResponseBean.getBody() == null || finjaPaymentToMerchantResponseBean.getBody().getData() == null) {
                        usersServices.increasePaymentCountByOne(user, "Finalize :: Can't get response from server");
                        return new ResponseEntity<>(new FinjaResponseBean(0, "Can't get response from server"), HttpStatus.OK);
                    }

                    FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse = new FinjaPaymentToMerchantResponse();

                    finjaPaymentToMerchantResponse.setCode(finjaPaymentToMerchantResponseBean.getBody().getCode() + "");
                    finjaPaymentToMerchantResponse.setMsg(finjaPaymentToMerchantResponseBean.getBody().getMsg());

                    finjaPaymentToMerchantResponse.setStatusCode(finjaPaymentToMerchantResponseBean.getBody().getData().getStatusCode());
                    finjaPaymentToMerchantResponse.setTransactionCode(finjaPaymentToMerchantResponseBean.getBody().getData().getTransactionCode());
                    finjaPaymentToMerchantResponse.setTransactionFee(finjaPaymentToMerchantResponseBean.getBody().getData().getTransactionFee());
                    finjaPaymentToMerchantResponse.setInvoiceID(finjaPaymentToMerchantResponseBean.getBody().getData().getInvoiceID());

                    finjaPaymentToMerchantResponse.setFinjaGetCustomerInfoRequest(finjaGetCustomerInfoRequest);

                    finjaPaymentToMerchantResponse = finjaPaymentToMerchantResponseServices.create(finjaPaymentToMerchantResponse);

                    finjaGetCustomerInfoRequest.setFinjaStatus(4);
                    finjaGetCustomerInfoRequestServices.saveOrUpdate(finjaGetCustomerInfoRequest);

                    FinjaResponseBean finjaResponseBean;
                    if (finjaPaymentToMerchantResponseBean.getBody().getCode() == 200) {

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

                        usersServices.resetPaymentCount(user, "Finalize :: " + finjaPaymentToMerchantResponseBean.getBody().getMsg());

                        finjaResponseBean = new FinjaResponseBean(1, finjaPaymentToMerchantResponseBean.getBody().getMsg());
                        finjaResponseBean.setResponseCode(finjaPaymentToMerchantResponseBean.getBody().getCode());
                        finjaResponseBean.setOrderId(finjaGetCustomerInfoRequest.getOrderId());
                        finjaResponseBean.setResponseMsg(finjaPaymentToMerchantResponseBean.getBody().getMsg());
                        finjaResponseBean.setTransactionCode(finjaPaymentToMerchantResponseBean.getBody().getData().getTransactionCode());

                        Notification notification = new Notification();
                        notification.setArchiveFlag(0);
                        notification.setReadFlag(0);
                        notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                        notification.setToUser(user.getId());
                        notification.setNotificationTitle("Payment Receipt from BeFiler.com");
                        notification.setNotificationDescription("We have successfully received your payment thru Finja of PKR " + amount);

                        notificationServices.save(notification);

                        /*EmailSender.sendEmail(EmailUtil.paymentTemaplateForFinja(user.getUserDetail().getFirstName(), amount + "", finjaGetCustomerInfoRequest.getOrderId(), finjaResponseBean.getTransactionCode(), finjaResponseBean.getResponseMsg()), "Payment Receipt from BeFiler.com", user.getEmailAddress());*/
                        EmailSender.sendEmail(EmailUtil.paymentTemaplateForFinja(user.getFullName(), amount + "", finjaGetCustomerInfoRequest.getOrderId(), finjaResponseBean.getTransactionCode(), finjaResponseBean.getResponseMsg()), "Payment Receipt from BeFiler.com", user.getEmailAddress());

                    } else {
                        usersServices.increasePaymentCountByOne(user, "Finalize :: " + finjaPaymentToMerchantResponseBean.getBody().getMsg());

                        finjaResponseBean = new FinjaResponseBean(2, finjaPaymentToMerchantResponseBean.getBody().getMsg());
                        finjaResponseBean.setResponseCode(finjaPaymentToMerchantResponseBean.getBody().getCode());
                        finjaResponseBean.setResponseMsg(finjaPaymentToMerchantResponseBean.getBody().getMsg());
                        finjaResponseBean.setTransactionCode(finjaPaymentToMerchantResponseBean.getBody().getData().getTransactionCode());
                    }

                    return new ResponseEntity<>(finjaResponseBean, HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                usersServices.increasePaymentCountByOne(user, "Finalize :: " + e.getMessage());
                return new ResponseEntity<>(new FinjaResponseBean(0, "Incomplete data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new FinjaResponseBean(0, "Incomplete data"), HttpStatus.OK);
    }
}
