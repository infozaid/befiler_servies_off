package com.arittek.befiler_services.controller.payment.ipg;


import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.beans.payment.ipg.IPGResponseBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationResponse;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.notifications.EmailNotificationService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.ipg.*;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
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
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ipg")
public class IPGRegistrationController {/*

    private UsersServices usersServices;
    private IpgRegistrationRequestServices ipgRegistrationRequestServices;
    private IpgRegistrationResponseServices ipgRegistrationResponseServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;
    private SettingsServices settingsServices;
    private PaymentServices paymentServices;
    private PaymentCartServices paymentCartServices;
    private EmailNotificationService emailNotificationService;

    @Value("${ipg.url}")
    private String ipgUrl;
    @Value("${ipg.customerName}")
    private String customerName;

    @Autowired
    IPGRegistrationController(UsersServices usersServices, IpgRegistrationRequestServices ipgRegistrationRequestServices, IpgRegistrationResponseServices ipgRegistrationResponseServices, PaymentCustomerInfoServices paymentCustomerInfoServices, SettingsServices settingsServices, PaymentServices paymentServices, PaymentCartServices paymentCartServices, EmailNotificationService emailNotificationService) {
        this.usersServices = usersServices;
        this.ipgRegistrationRequestServices = ipgRegistrationRequestServices;
        this.ipgRegistrationResponseServices = ipgRegistrationResponseServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
        this.settingsServices = settingsServices;
        this.paymentServices = paymentServices;
        this.paymentCartServices = paymentCartServices;
        this.emailNotificationService = emailNotificationService;
    }

    @SuppressWarnings("unchecked")
    //@Transactional
    @RequestMapping(value = "/register", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<IPGResponseBean> ipgRegisterService(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {

        User user = null;
        try {

            if (!StringUtils.isNotEmpty(requestBean.getCustomer())) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Please enter name."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Please enter mobile no."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Please enter email address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Please enter residential address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Please enter billing address"), HttpStatus.OK);
            }

            if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new IPGResponseBean(0, "Empty cart"), HttpStatus.OK);
            }

            if (requestBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new IPGResponseBean(0, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new IPGResponseBean(0, "Session Expired"), HttpStatus.OK);
            }


            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new IPGResponseBean(0, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new IPGResponseBean(0, "Max attempts"), HttpStatus.OK);
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
                                    return new ResponseEntity<>(new IPGResponseBean(0, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new IPGResponseBean(0, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new IPGResponseBean(0, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new IPGResponseBean(0, "Payment cart error"), HttpStatus.OK);
                }
            }

            URL url = new URL(ipgUrl);
            System.out.println("Comtrust URL : " + url);

            MerchantAPI_Service m = new MerchantAPI_ServiceLocator();
            System.out.println(url.getAuthority());

            MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(url);
            if (port == null) {
                usersServices.increasePaymentCountByOne(user, "Register :: Server is down");
                return new ResponseEntity<>(new IPGResponseBean(0, "Server is down"), HttpStatus.OK);
            }

            *//*Save Customer Info data*//*
            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.UBL, requestBean);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new IPGResponseBean(0, "Customer Info Error"), HttpStatus.OK);
            }

            *//*Registration Request for Database*//*
            IpgRegistrationRequest ipgRegistrationRequest = new IpgRegistrationRequest();
            ipgRegistrationRequest.setPaymentCustomerInfo(paymentCustomerInfo);

            ipgRegistrationRequest.setLanguage("ENG");
            ipgRegistrationRequest.setIpgVersion(new Double(2.0));
            ipgRegistrationRequest.setCustomer(customerName);

            ipgRegistrationRequest.setOrderName("Digital case");
            ipgRegistrationRequest.setOrderInfo("Online return file");

            ipgRegistrationRequest.setCurrency("PKR");
            ipgRegistrationRequest.setAmount(amount);

            ipgRegistrationRequest.setReturnPath(requestBean.getReturnPath());
            ipgRegistrationRequest.setTransactionHint("CPT:Y;VCC:Y");

            ipgRegistrationRequest.setIpgStatus(1);

            *//*Generate Order ID*//*
            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() *//*+ "-"*//*
                    + sdf.format(paymentCustomerInfo.getCreatedDate()) *//*+ "-"*//*
                    + String.format("%05d", paymentCustomerInfo.getId());
            ipgRegistrationRequest.setOrderId(orderId);

            *//*Set Rerturn Path*//*
            ipgRegistrationRequest.setReturnPath(ipgRegistrationRequest.getReturnPath() + "?param=" + paymentCustomerInfo.getId());

            ipgRegistrationRequest = ipgRegistrationRequestServices.saveOrUpdate(ipgRegistrationRequest);
            if (ipgRegistrationRequest == null) {
                usersServices.increasePaymentCountByOne(user, "Registration Request for Database Error");
                return new ResponseEntity<>(new IPGResponseBean(0, "Registration Request for Database Error"), HttpStatus.OK);
            }

            *//*Registration Request to IPG*//*
            RegistrationRequest registrationRequest = new RegistrationRequest();

            registrationRequest.setLanguage(ipgRegistrationRequest.getLanguage());
            registrationRequest.setVersion(BigDecimal.valueOf(ipgRegistrationRequest.getIpgVersion()));
            registrationRequest.setCustomer(ipgRegistrationRequest.getCustomer());

            registrationRequest.setOrderID(ipgRegistrationRequest.getOrderId());
            registrationRequest.setOrderName(ipgRegistrationRequest.getOrderName());
            registrationRequest.setOrderInfo(ipgRegistrationRequest.getOrderInfo());

            registrationRequest.setCurrency(ipgRegistrationRequest.getCurrency());
            registrationRequest.setAmount(BigDecimal.valueOf(ipgRegistrationRequest.getAmount()));

            System.out.println("Return Path ::  " + ipgRegistrationRequest.getReturnPath());
            registrationRequest.setReturnPath(ipgRegistrationRequest.getReturnPath());
            registrationRequest.setTransactionHint(ipgRegistrationRequest.getTransactionHint());

            RegistrationResponse registrationResponse = port.register(registrationRequest);

            if (registrationResponse == null) {
               *//* String[] emails = (String[])emailNotificationService.getAllEmails(1).toArray();
                EmailSender.sendEmail("Payment gateway is not responding", "Payment Notification from BeFiler.com", emails);*//*

                usersServices.increasePaymentCountByOne(user, "Register :: Payment gateway is not responding");
                return new ResponseEntity<>(new IPGResponseBean(0, "Payment gateway is not responding"), HttpStatus.OK);
            }

            IpgRegistrationResponse ipgRegistrationResponse = new IpgRegistrationResponse();

            ipgRegistrationResponse.setIpgRegistrationRequest(ipgRegistrationRequest);
            ipgRegistrationResponse.setResponseCode(registrationResponse.getResponseCode());
            ipgRegistrationResponse.setResponseDescription(registrationResponse.getResponseDescription());
            ipgRegistrationResponse.setTransactionId(registrationResponse.getTransactionID());
            ipgRegistrationResponse.setUniqueId(registrationResponse.getUniqueID());
            ipgRegistrationResponse.setPaymentPortal(registrationResponse.getPaymentPortal());

            ipgRegistrationResponseServices.saveOrUpdate(ipgRegistrationResponse);

            ipgRegistrationRequest.setIpgStatus(2);
            ipgRegistrationRequestServices.saveOrUpdate(ipgRegistrationRequest);

            IPGResponseBean ipgResponseBean;
            if (registrationResponse.getResponseCode() == 0) {
                usersServices.resetPaymentCount(user, "Register :: " + registrationResponse.getResponseDescription());
                paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

                ipgResponseBean = new IPGResponseBean(1, registrationResponse.getResponseDescription());
            } else {
                usersServices.increasePaymentCountByOne(user, "Register :: " + registrationResponse.getResponseDescription());
                ipgResponseBean = new IPGResponseBean(2, registrationResponse.getResponseDescription());
            }

            ipgResponseBean.setResponseCode(registrationResponse.getResponseCode());
            ipgResponseBean.setResponseDescription(registrationResponse.getResponseDescription());
            ipgResponseBean.setTransactionId(registrationResponse.getTransactionID());
            ipgResponseBean.setUniqueId(registrationResponse.getUniqueID());
            ipgResponseBean.setPaymentPortal(registrationResponse.getPaymentPortal());

            String[] emails = emailNotificationService.getAllActiveEmails();
            EmailSender.sendEmail("Successful", "Payment Notification from BeFiler.com", emails);

            return new ResponseEntity<>(ipgResponseBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());

            String[] emails = emailNotificationService.getAllActiveEmails();
            EmailSender.sendEmail("UnSuccessfull Due to Incomplete data", "Payment Notification from BeFiler.com", emails);
            return new ResponseEntity<>(new IPGResponseBean(0, "Incomplete data"), HttpStatus.OK);
        }
    }*/
}
