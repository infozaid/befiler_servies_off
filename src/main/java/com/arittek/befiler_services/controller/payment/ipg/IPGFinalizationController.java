package com.arittek.befiler_services.controller.payment.ipg;


import com.arittek.befiler_services.beans.payment.ipg.IPGRequestBean;
import com.arittek.befiler_services.beans.payment.ipg.IPGResponseBean;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationResponse;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.notifications.EmailNotificationService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.ipg.*;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping(value = "/ipg")
public class IPGFinalizationController {
/*
    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private PaymentServices paymentServices;
    private PaymentCartServices paymentCartServices;
    private NotificationServices notificationServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;
    private IpgRegistrationRequestServices ipgRegistrationRequestServices;
    private IpgFinalizationRequestServices ipgFinalizationRequestServices;
    private IpgFinalizationResponseServices ipgFinalizationResponseServices;
    private EmailNotificationService emailNotificationService;

    @Value("${ipg.url}")
    private String ipgUrl;

    @Autowired
    public IPGFinalizationController(UsersServices usersServices, TaxformServices taxformServices, PaymentServices paymentServices, PaymentCartServices paymentCartServices, NotificationServices notificationServices, PaymentCustomerInfoServices paymentCustomerInfoServices, IpgRegistrationRequestServices ipgRegistrationRequestServices, IpgFinalizationRequestServices ipgFinalizationRequestServices, IpgFinalizationResponseServices ipgFinalizationResponseServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, EmailNotificationService emailNotificationService) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.paymentServices = paymentServices;
        this.paymentCartServices = paymentCartServices;
        this.notificationServices = notificationServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
        this.ipgRegistrationRequestServices = ipgRegistrationRequestServices;
        this.ipgFinalizationRequestServices = ipgFinalizationRequestServices;
        this.ipgFinalizationResponseServices = ipgFinalizationResponseServices;
        this.emailNotificationService = emailNotificationService;
    }

    @RequestMapping(value = "/finalize", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<IPGResponseBean> ipgFinalizeService(@RequestBody IPGRequestBean requestBean) throws Exception {

        if (requestBean != null &&
                requestBean.getRegistrationId() != null &&
                requestBean.getUserId() != null) {

            User user = null;
            try {

                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new IPGResponseBean(0, "Session Expired"), HttpStatus.OK);
                }

                PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneById(requestBean.getRegistrationId());
                if (paymentCustomerInfo == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                IpgRegistrationRequest ipgRegistrationRequest = paymentCustomerInfo.getIpgRegistrationRequest();
                if (ipgRegistrationRequest == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                if (ipgRegistrationRequest.getCustomer() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                if (ipgRegistrationRequest.getIpgRegistrationResponse() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                if (ipgRegistrationRequest.getIpgRegistrationResponse().getTransactionId() == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't complete transaction");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
                }

                List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
                if (paymentCartList == null || paymentCartList.size() <= 0) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Can't find cart");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Can't find cart"), HttpStatus.OK);
                }

                URL url = new URL(ipgUrl);
                System.out.println("Comtrust URL : " + url);

                MerchantAPI_Service m = new MerchantAPI_ServiceLocator();
                System.out.println(url.getAuthority());

                MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(url);
                if (port == null) {
                    usersServices.increasePaymentCountByOne(user, "Finalize :: Server is down");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Server is down"), HttpStatus.OK);
                }

                IpgFinalizationRequest ipgFinalizationRequest = new IpgFinalizationRequest();

                ipgFinalizationRequest.setLanguage(ipgRegistrationRequest.getLanguage());
                ipgFinalizationRequest.setIpgVersion(ipgRegistrationRequest.getIpgVersion());
                ipgFinalizationRequest.setCustomer(ipgRegistrationRequest.getCustomer());
                ipgFinalizationRequest.setTransactionId(ipgRegistrationRequest.getIpgRegistrationResponse().getTransactionId());

                ipgFinalizationRequest.setIpgRegistrationRequest(ipgRegistrationRequest);

                ipgFinalizationRequest = ipgFinalizationRequestServices.saveOrUpdate(ipgFinalizationRequest);

                ipgRegistrationRequest.setIpgStatus(3);
                ipgRegistrationRequest = ipgRegistrationRequestServices.saveOrUpdate(ipgRegistrationRequest);

                if (ipgFinalizationRequest != null) {
                    FinalizationRequest finalizationRequest = new FinalizationRequest();

                    finalizationRequest.setLanguage(ipgRegistrationRequest.getLanguage());
                    finalizationRequest.setVersion(BigDecimal.valueOf(ipgRegistrationRequest.getIpgVersion()));
                    finalizationRequest.setCustomer(ipgRegistrationRequest.getCustomer());
                    finalizationRequest.setTransactionID(ipgRegistrationRequest.getIpgRegistrationResponse().getTransactionId());

                    FinalizationResponse finalizationResponse = port.finalize(finalizationRequest);

                    if (finalizationResponse == null) {
                        usersServices.increasePaymentCountByOne(user, "Finalize :: Can't get response from server");
                        return new ResponseEntity<>(new IPGResponseBean(0, "Can't get response from server"), HttpStatus.OK);
                    }

                    IpgFinalizationResponse ipgFinalizationResponse = new IpgFinalizationResponse();

                    ipgFinalizationResponse.setIpgRegistrationRequest(ipgRegistrationRequest);
                    ipgFinalizationResponse.setResponseCode(finalizationResponse.getResponseCode());
                    ipgFinalizationResponse.setResponseDescription(finalizationResponse.getResponseDescription());
                    ipgFinalizationResponse.setUniqueId(finalizationResponse.getUniqueID());
                    if (finalizationResponse.getApprovalCode() != null) {
                        ipgFinalizationResponse.setApprovalCode(finalizationResponse.getApprovalCode());
                    }
                    if (finalizationResponse.getCardNumber() != null) {
                        ipgFinalizationResponse.setCardNumber(finalizationResponse.getCardNumber());
                    }
                    if (finalizationResponse.getCardToken() != null) {
                        ipgFinalizationResponse.setCardToken(finalizationResponse.getCardToken());
                    }

                    ipgFinalizationResponseServices.create(ipgFinalizationResponse);

                    ipgRegistrationRequest.setIpgStatus(4);
                    ipgRegistrationRequestServices.saveOrUpdate(ipgRegistrationRequest);

                    IPGResponseBean ipgResponseBean;
                    if (finalizationResponse.getResponseCode() == 0) {

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

                        usersServices.resetPaymentCount(user, "Finalize :: " + finalizationResponse.getResponseDescription());

                        ipgResponseBean = new IPGResponseBean(1, finalizationResponse.getResponseDescription());
                        ipgResponseBean.setResponseCode(finalizationResponse.getResponseCode());
                        ipgResponseBean.setResponseDescription(finalizationResponse.getResponseDescription());
                        ipgResponseBean.setUniqueId(finalizationResponse.getUniqueID());
                        ipgResponseBean.setApprovalCode(finalizationResponse.getApprovalCode());
                        ipgResponseBean.setTransactionId(ipgFinalizationResponse.getIpgRegistrationRequest().getIpgRegistrationResponse().getTransactionId());
                        ipgResponseBean.setOrderId(ipgFinalizationResponse.getIpgRegistrationRequest().getOrderId());

                        Notification notification = new Notification();
                        notification.setArchiveFlag(0);
                        notification.setReadFlag(0);
                        notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                        notification.setToUser(user.getId());
                        notification.setNotificationTitle("Payment Receipt from BeFiler.com");
                        notification.setNotificationDescription("We have successfully received your payment thru [Payment Method e.g. Credit Card] of PKR " + ipgRegistrationRequest.getAmount());

                        notificationServices.save(notification);

                        EmailSender.sendEmail(EmailUtil.paymentTemaplate(user.getFullName(), ipgRegistrationRequest.getAmount() + "", ipgResponseBean.getOrderId(), ipgResponseBean.getTransactionId(), ipgResponseBean.getApprovalCode(), ipgResponseBean.getResponseDescription()), "Payment Receipt from BeFiler.com", user.getEmailAddress());

                    } else {
                        usersServices.increasePaymentCountByOne(user, "Finalize :: " + finalizationResponse.getResponseDescription());

                        ipgResponseBean = new IPGResponseBean(2, finalizationResponse.getResponseDescription());
                        ipgResponseBean.setResponseCode(finalizationResponse.getResponseCode());
                        ipgResponseBean.setResponseDescription(finalizationResponse.getResponseDescription());
                        ipgResponseBean.setUniqueId(finalizationResponse.getUniqueID());
                    }

                    String[] emails = emailNotificationService.getAllActiveEmails();
                    EmailSender.sendEmail("Successfull", "Payment Notification from BeFiler.com", emails);

                    return new ResponseEntity<>(ipgResponseBean, HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                usersServices.increasePaymentCountByOne(user, "Finalize :: " + e.getMessage());
                return new ResponseEntity<>(new IPGResponseBean(0, "Incomplete data"), HttpStatus.OK);
            }
        }
        String[] emails = emailNotificationService.getAllActiveEmails();
        EmailSender.sendEmail("UnSuccessfull Due to Incomplete data", "Payment Notification from BeFiler.com", emails);
        return new ResponseEntity<>(new IPGResponseBean(0, "Incomplete data"), HttpStatus.OK);
    }*/
}
