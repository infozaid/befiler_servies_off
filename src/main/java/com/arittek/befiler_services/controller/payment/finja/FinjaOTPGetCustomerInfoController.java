package com.arittek.befiler_services.controller.payment.finja;

import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaGetCustomerInfoRequestBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaGetCustomerInfoResponseBean;
import com.arittek.befiler_services.beans.payment.finja.FinjaResponseBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoResponse;
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
import com.arittek.befiler_services.services.payment.finja.FinjaGetCustomerInfoRequestServices;
import com.arittek.befiler_services.services.payment.finja.FinjaGetCustomerInfoResponseServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/finja/otp")
public class FinjaOTPGetCustomerInfoController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private SettingsServices settingsServices;
    private PaymentCartServices paymentCartServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;
    private FinjaGetCustomerInfoRequestServices finjaGetCustomerInfoRequestServices;
    private FinjaGetCustomerInfoResponseServices finjaGetCustomerInfoResponseServices;
    private EmailNotificationService emailNotificationService;

    @Value("${finja.otp.customerIdMerchant}")
    private String CUSTOMER_ID_MERCHANT;
    @Value("${finja.otp.getCustInfo.url}")
    private String GETCUSTOMER_INFO_URL;

    @Autowired
    public FinjaOTPGetCustomerInfoController(UsersServices usersServices, PaymentServices paymentServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, PaymentCustomerInfoServices paymentCustomerInfoServices, FinjaGetCustomerInfoRequestServices finjaGetCustomerInfoRequestServices, FinjaGetCustomerInfoResponseServices finjaGetCustomerInfoResponseServices, EmailNotificationService emailNotificationService) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.settingsServices = settingsServices;
        this.paymentCartServices = paymentCartServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
        this.finjaGetCustomerInfoRequestServices = finjaGetCustomerInfoRequestServices;
        this.finjaGetCustomerInfoResponseServices = finjaGetCustomerInfoResponseServices;
        this.emailNotificationService = emailNotificationService;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/getCustomerInfo", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FinjaResponseBean> ipgRegisterService(@RequestBody PaymentCustomerInfoBean finjaRequestBean) throws Exception {
        if (finjaRequestBean != null &&
                finjaRequestBean.getCustomer() != null &&
                finjaRequestBean.getUserId() != null &&
                finjaRequestBean.getPaymentCartBeanList() != null &&
                finjaRequestBean.getPaymentCartBeanList().size() > 0) {

            User user = null;
            try {

                if (finjaRequestBean.getMobileNo() == null) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Please enter mobile no.", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Please enter mobile no."), HttpStatus.OK);
                }

                if (finjaRequestBean.getEmailAddress() == null) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Please enter email address", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Please enter email address"), HttpStatus.OK);
                }

                if (finjaRequestBean.getResidentialAddress() == null) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Please enter residential address", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Please enter residential address"), HttpStatus.OK);
                }

                if (finjaRequestBean.getBillingAddress() == null) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Please enter billing address", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Please enter billing address"), HttpStatus.OK);
                }

                /*user = usersServices.findOneByIdAndStatus(finjaRequestBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(finjaRequestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Session Expired", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Session Expired"), HttpStatus.OK);
                }

                Settings settings = settingsServices.getActiveRecord();
                UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
                if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                    if (settings != null && settings.getMaxPaymentAttempts() != null) {
                        if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Max attempts", "Payment Notification from Befiler.com", emails );
                            return new ResponseEntity<>(new FinjaResponseBean(3, "Max attempts"), HttpStatus.OK);
                        }
                    } else {
                        if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
//                    String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                    EmailSender.sendEmail("Max attempts", "Payment Notification from Befiler.com", emails );
                            return new ResponseEntity<>(new FinjaResponseBean(3, "Max attempts"), HttpStatus.OK);
                        }
                    }
                }

                List<PaymentCart> paymentCartList = new ArrayList<>();
                Double amount = 0.0;
                for (PaymentCartBean paymentCartBean : finjaRequestBean.getPaymentCartBeanList()) {
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
//                                        String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                                        EmailSender.sendEmail("Tax Form is already paid", "Payment Notification from Befiler.com", emails );
                                        return new ResponseEntity<>(new FinjaResponseBean(3, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                    }
                                }
                            } else if (paymentCart.getProductType() == ProductType.NTN) {
                                if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                    Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                    if (payment == null) {
                                        amount += paymentCart.getSettingPayment().getAmount();
                                    } else {
                                        usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
//                                        String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                                        EmailSender.sendEmail("NTN Registration is already paid", "Payment Notification from Befiler.com", emails );
                                        return new ResponseEntity<>(new FinjaResponseBean(3, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                    }
                                }
                            }
                        } else {
                            usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
//                          String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                          EmailSender.sendEmail("Payment cart error", "Payment Notification from Befiler.com", emails );
                            return new ResponseEntity<>(new FinjaResponseBean(3, "Payment cart error"), HttpStatus.OK);
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
//                          String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                          EmailSender.sendEmail("Payment cart error", "Payment Notification from Befiler.com", emails );
                        return new ResponseEntity<>(new FinjaResponseBean(3, "Payment cart error"), HttpStatus.OK);
                    }
                }

                /*Save Customer Info data*/
                PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.SIMSIM, finjaRequestBean);
                if (paymentCustomerInfo == null) {
                    usersServices.increasePaymentCountByOne(user, "Customer Info Error");
//                  String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                  EmailSender.sendEmail("Customer Info Error", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Customer Info Error"), HttpStatus.OK);
                }

                /*Get Customer Info Request for Database*/
                FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest = new FinjaGetCustomerInfoRequest();
                finjaGetCustomerInfoRequest.setPaymentCustomerInfo(paymentCustomerInfo);

                finjaGetCustomerInfoRequest.setMobileNo(finjaRequestBean.getMobileNo());
                finjaGetCustomerInfoRequest.setCustomerIdMerchant(CUSTOMER_ID_MERCHANT);

                finjaGetCustomerInfoRequest.setFinjaStatus(1);

                /*Generate Order ID*/
                SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
                String orderId = user.getId() /*+ "-"*/
                        + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
                        + String.format("%05d", paymentCustomerInfo.getId());
                finjaGetCustomerInfoRequest.setOrderId(orderId);

                finjaGetCustomerInfoRequest = finjaGetCustomerInfoRequestServices.saveOrUpdate(finjaGetCustomerInfoRequest);
                if (finjaGetCustomerInfoRequest == null) {
                    usersServices.increasePaymentCountByOne(user, "Get Customer Info for Database Error");
//                  String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                  EmailSender.sendEmail("Get Customer Info for Database Error", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Get Customer Info for Database Error"), HttpStatus.OK);
                }


                /*String url = "http://devmerchant.finpay.pk/public/api/getCustInfo";*/
                FinjaGetCustomerInfoRequestBean finjaGetCustomerInfoRequestBean = new FinjaGetCustomerInfoRequestBean();
                finjaGetCustomerInfoRequestBean.setCustomerIdMerchant(Long.parseLong(CUSTOMER_ID_MERCHANT));
                finjaGetCustomerInfoRequestBean.setMobileNo(Long.parseLong(finjaRequestBean.getMobileNo()));

                HttpEntity<FinjaGetCustomerInfoRequestBean> request = new HttpEntity<>(finjaGetCustomerInfoRequestBean);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<FinjaGetCustomerInfoResponseBean> finjaGetCustomerInfoResponseBean = restTemplate.postForEntity(GETCUSTOMER_INFO_URL, request, FinjaGetCustomerInfoResponseBean.class);

                if (finjaGetCustomerInfoResponseBean == null || finjaGetCustomerInfoResponseBean.getBody() == null) {
                    usersServices.increasePaymentCountByOne(user, "Register :: Payment gateway is not responding");
//                  String[] emails =(String[]) emailNotificationService.getAllActiveEmails().toArray();
//                  EmailSender.sendEmail("Payment gateway is not responding", "Payment Notification from Befiler.com", emails );
                    return new ResponseEntity<>(new FinjaResponseBean(3, "Payment gateway is not responding"), HttpStatus.OK);
                }

                FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse = new FinjaGetCustomerInfoResponse();

                finjaGetCustomerInfoResponse.setCode(Integer.toString(finjaGetCustomerInfoResponseBean.getBody().getCode()));
                finjaGetCustomerInfoResponse.setMsg(finjaGetCustomerInfoResponseBean.getBody().getMsg());

                finjaGetCustomerInfoResponse.setFinjaGetCustomerInfoRequest(finjaGetCustomerInfoRequest);

                finjaGetCustomerInfoResponse = finjaGetCustomerInfoResponseServices.create(finjaGetCustomerInfoResponse);

                finjaGetCustomerInfoRequest.setFinjaStatus(2);
                finjaGetCustomerInfoRequestServices.saveOrUpdate(finjaGetCustomerInfoRequest);

                FinjaResponseBean finjaResponseBean;
                if (finjaGetCustomerInfoResponseBean.getBody().getCode() == 200) {

                    finjaGetCustomerInfoResponse.setFnToken(finjaGetCustomerInfoResponseBean.getHeaders().getFirst("FN-Token"));

                    finjaGetCustomerInfoResponse.setCustomerId(Integer.toString(finjaGetCustomerInfoResponseBean.getBody().getData().getCustomerId()));
                    finjaGetCustomerInfoResponse.setCustomerName(finjaGetCustomerInfoResponseBean.getBody().getData().getCustomerName());
                    finjaGetCustomerInfoResponse.setEmail(finjaGetCustomerInfoResponseBean.getBody().getData().getEmail());

                    usersServices.resetPaymentCount(user, "Register :: " + finjaGetCustomerInfoResponseBean.getBody().getMsg());
                    paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

                    finjaResponseBean = new FinjaResponseBean(4, finjaGetCustomerInfoResponseBean.getBody().getMsg());
                    finjaResponseBean.setCustomerName(finjaGetCustomerInfoResponseBean.getBody().getData().getCustomerName());
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: " + finjaGetCustomerInfoResponseBean.getBody().getMsg());
                    finjaResponseBean = new FinjaResponseBean(5, finjaGetCustomerInfoResponseBean.getBody().getMsg());
                }

                finjaGetCustomerInfoResponseServices.create(finjaGetCustomerInfoResponse);

                finjaGetCustomerInfoRequest.setFinjaStatus(2);
                finjaGetCustomerInfoRequestServices.saveOrUpdate(finjaGetCustomerInfoRequest);

                finjaResponseBean.setResponseCode(finjaGetCustomerInfoResponseBean.getBody().getCode());
                finjaResponseBean.setResponseMsg(finjaGetCustomerInfoResponseBean.getBody().getMsg());

                finjaResponseBean.setRegistrationId(finjaGetCustomerInfoRequest.getPaymentCustomerInfoId());

                String[] emails = emailNotificationService.getAllActiveEmails();
                EmailSender.sendEmail("Successfull", "Payment Notification from Befiler.com", emails);

                return new ResponseEntity<>(finjaResponseBean, HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());
                return new ResponseEntity<>(new FinjaResponseBean(3, "Incomplete data"), HttpStatus.OK);
            }
        }
        String[] emails = emailNotificationService.getAllActiveEmails();
        EmailSender.sendEmail("UnSuccessful, Due to Incomplete data", "Payment Notification from Befiler.com", emails);
        return new ResponseEntity<>(new FinjaResponseBean(3, "Incomplete data"), HttpStatus.OK);
    }
}
