package com.arittek.befiler_services.controller.payment.apps;


import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.beans.payment.apps.AppsGetAccessTokenRequestBean;
import com.arittek.befiler_services.beans.payment.apps.AppsGetAccessTokenResponseBean;
import com.arittek.befiler_services.beans.payment.apps.AppsPostTransactionRequestBean;
import com.arittek.befiler_services.beans.payment.apps.AppsPostTransactionResponseBean;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenRequest;
import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenResponse;
import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionRequest;
import com.arittek.befiler_services.model.payment.apps.AppsPostTransactionResponse;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;
import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationResponse;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.notifications.EmailNotificationService;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.apps.AppsGetAccessTokenRequestService;
import com.arittek.befiler_services.services.payment.apps.AppsGetAccessTokenResponseService;
import com.arittek.befiler_services.services.payment.apps.AppsPostTransactionRequestService;
import com.arittek.befiler_services.services.payment.apps.AppsPostTransactionResponseService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.lang.StringUtils;

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
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/apps")
public class AppsController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private TaxformServices taxformServices;
    private SettingsServices settingsServices;
    private PaymentCartServices paymentCartServices;
    private NotificationServices notificationServices;
    private EmailNotificationService emailNotificationService;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;
    private AppsGetAccessTokenRequestService appsGetAccessTokenRequestService;
    private AppsGetAccessTokenResponseService appsGetAccessTokenResponseService;
    private AppsPostTransactionRequestService appsPostTransactionRequestService;
    private AppsPostTransactionResponseService appsPostTransactionResponseService;

    @Value("${apps.merchantId}")
    private String MERCHANT_ID;
    @Value("${apps.securedKey}")
    private String SECURED_KEY;
    @Value("${apps.url.getAccessToken}")
    private String URL_GET_ACCESS_TOKEN;
    @Value("${apps.url.postTransaction}")
    private String URL_POST_TRANSACTION;

    @Autowired
    public AppsController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, NotificationServices notificationServices, EmailNotificationService emailNotificationService, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices, AppsGetAccessTokenRequestService appsGetAccessTokenRequestService, AppsGetAccessTokenResponseService appsGetAccessTokenResponseService, AppsPostTransactionRequestService appsPostTransactionRequestService, AppsPostTransactionResponseService appsPostTransactionResponseService) {
	this.usersServices = usersServices;
	this.paymentServices = paymentServices;
	this.taxformServices = taxformServices;
	this.settingsServices = settingsServices;
	this.paymentCartServices = paymentCartServices;
	this.notificationServices = notificationServices;
	this.emailNotificationService = emailNotificationService;
	this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
	this.paymentCustomerInfoServices = paymentCustomerInfoServices;
	this.appsGetAccessTokenRequestService = appsGetAccessTokenRequestService;
	this.appsGetAccessTokenResponseService = appsGetAccessTokenResponseService;
	this.appsPostTransactionRequestService = appsPostTransactionRequestService;
	this.appsPostTransactionResponseService = appsPostTransactionResponseService;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/request", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<AppsPostTransactionRequestBean> appsRequest(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {

	User user = usersServices.getUserFromToken();
	if (user == null) {
	    return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Session Expired"), HttpStatus.OK);
	}
	try {
	    if (!StringUtils.isNotEmpty(requestBean.getCustomer())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Please enter name."), HttpStatus.OK); }
	    if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Please enter mobile no."), HttpStatus.OK); }
	    if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Please enter email address"), HttpStatus.OK); }
	    if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Please enter residential address"), HttpStatus.OK); }
	    if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Please enter billing address"), HttpStatus.OK); }
	    if (!StringUtils.isNotEmpty(requestBean.getReturnPath())) { return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Cann't find return path"), HttpStatus.OK); }

	    if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
		return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Empty cart"), HttpStatus.OK);
	    }

	    Settings settings = settingsServices.getActiveRecord();
	    UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
	    if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
		if (settings != null && settings.getMaxPaymentAttempts() != null) {
		    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
			return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Max attempts"), HttpStatus.OK);
		    }
		} else {
		    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
			return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Max attempts"), HttpStatus.OK);
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
				    return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
				}
			    }
			} else if (paymentCart.getProductType() == ProductType.NTN) {
			    if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
				Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
				if (payment == null) {
				    amount += paymentCart.getSettingPayment().getAmount();
				} else {
				    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
				    return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
				}
			    }
			}
		    } else {
			usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
			return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Payment cart error"), HttpStatus.OK);
		    }
		} else {
		    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
		    return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Payment cart error"), HttpStatus.OK);
		}
	    }

            /*Save Customer Info data*/
	    PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.APPS, requestBean);
	    if (paymentCustomerInfo == null) {
		usersServices.increasePaymentCountByOne(user, "Customer Info Error");
		return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Customer Info Error"), HttpStatus.OK);
	    }

	    /*Generate Order ID*/
	    SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
	    String orderId = user.getId() /*+ "-"*/
		    + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
		    + String.format("%05d", paymentCustomerInfo.getId());
	    paymentCustomerInfo.setOrderId(orderId);
	    paymentCustomerInfo = paymentCustomerInfoServices.update(paymentCustomerInfo);

            /*GetAccessToken Request for Database*/
	    AppsGetAccessTokenRequest appsGetAccessTokenRequest = new AppsGetAccessTokenRequest();
	    appsGetAccessTokenRequest.setPaymentCustomerInfo(paymentCustomerInfo);

	    appsGetAccessTokenRequest.setMerchantId(MERCHANT_ID);
	    appsGetAccessTokenRequest.setSecuredKey(SECURED_KEY);
	    appsGetAccessTokenRequest.setUrl(URL_GET_ACCESS_TOKEN);

	    appsGetAccessTokenRequest = appsGetAccessTokenRequestService.saveOrUpdate(appsGetAccessTokenRequest);
	    if (appsGetAccessTokenRequest == null) {
		usersServices.increasePaymentCountByOne(user, "getAccessToke Request for Database Error");
		return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "getAccessToke Request for Database Error"), HttpStatus.OK);
	    }

            /*getAccessToken Request*/
	    AppsGetAccessTokenRequestBean appsGetAccessTokenRequestBean = new AppsGetAccessTokenRequestBean();

	    appsGetAccessTokenRequestBean.setMerchant_id(appsGetAccessTokenRequest.getMerchantId());
	    appsGetAccessTokenRequestBean.setSecured_key(appsGetAccessTokenRequest.getSecuredKey());

	    HttpEntity<AppsGetAccessTokenRequestBean> request = new HttpEntity<>(appsGetAccessTokenRequestBean);
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<AppsGetAccessTokenResponseBean> appsGetAccessTokenResponseBean = restTemplate.postForEntity(appsGetAccessTokenRequest.getUrl(), request, AppsGetAccessTokenResponseBean.class);
	    if (appsGetAccessTokenResponseBean == null || appsGetAccessTokenResponseBean.getBody() == null) {
		usersServices.increasePaymentCountByOne(user, "GET_ACCESS_TOKEN :: Payment gateway is not responding");
		return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Payment gateway is not responding"), HttpStatus.OK);
	    }

	    AppsGetAccessTokenResponse appsGetAccessTokenResponse = new AppsGetAccessTokenResponse();
	    appsGetAccessTokenResponse.setAppsGetAccessTokenRequest(appsGetAccessTokenRequest);
	    if (StringUtils.isNotEmpty(appsGetAccessTokenResponseBean.getBody().getErrorCode()) && StringUtils.isNotEmpty(appsGetAccessTokenResponseBean.getBody().getErrorDescription())) {
		appsGetAccessTokenResponse.setErrorCode(appsGetAccessTokenResponseBean.getBody().getErrorCode());
		appsGetAccessTokenResponse.setErrorDescription(appsGetAccessTokenResponseBean.getBody().getErrorDescription());

		appsGetAccessTokenResponse = appsGetAccessTokenResponseService.saveOrUpdate(appsGetAccessTokenResponse);
		usersServices.increasePaymentCountByOne(user, "GET_ACCESS_TOKEN :: " + appsGetAccessTokenResponse.getErrorDescription());
		return new ResponseEntity<>(new AppsPostTransactionRequestBean(20, appsGetAccessTokenResponse.getErrorDescription()), HttpStatus.OK);
	    } else {
		appsGetAccessTokenResponse.setName(appsGetAccessTokenResponseBean.getBody().NAME);
		appsGetAccessTokenResponse.setAccessToken(appsGetAccessTokenResponseBean.getBody().ACCESS_TOKEN);
		appsGetAccessTokenResponse.setGeneratedDateTime(appsGetAccessTokenResponseBean.getBody().GENERATED_DATE_TIME);

		appsGetAccessTokenResponse = appsGetAccessTokenResponseService.saveOrUpdate(appsGetAccessTokenResponse);
	    }

	    AppsPostTransactionRequest appsPostTransactionRequest = new AppsPostTransactionRequest();
	    appsPostTransactionRequest.setAppsGetAccessTokenRequest(appsGetAccessTokenRequest);

	    appsPostTransactionRequest.setRedirectUrl(URL_POST_TRANSACTION);
	    appsPostTransactionRequest.setMerchantId(appsGetAccessTokenRequest.getMerchantId());
	    appsPostTransactionRequest.setMerchantName("BEFILER");
	    appsPostTransactionRequest.setToken(appsGetAccessTokenResponse.getAccessToken());
	    appsPostTransactionRequest.setProCode("00");
	    appsPostTransactionRequest.setTxnamt(amount);
	    appsPostTransactionRequest.setCustomerMobileNo("+"+paymentCustomerInfo.getMobileNo());
	    appsPostTransactionRequest.setCustomerEmailAddress(paymentCustomerInfo.getEmailAddress());
	    appsPostTransactionRequest.setSignature("RANDOMSTRINGVALUE");
	    appsPostTransactionRequest.setAppsVersion("1.0");
	    appsPostTransactionRequest.setTxndesc("Online return file");
	    appsPostTransactionRequest.setSuccessUrl(requestBean.getReturnPath() + "?param=" + paymentCustomerInfo.getId());
	    appsPostTransactionRequest.setFailureUrl(requestBean.getReturnPath() + "?param=" + paymentCustomerInfo.getId());
	    appsPostTransactionRequest.setBasketId(paymentCustomerInfo.getOrderId());
	    SimpleDateFormat orderDate = new SimpleDateFormat("yyyy-MM-dd");
	    appsPostTransactionRequest.setOrderDate(orderDate.format(paymentCustomerInfo.getCreatedDate()));
	    appsPostTransactionRequest.setCheckoutUrl("http://merchantsite.com/order/backend/confirm");

	    appsPostTransactionRequestService.saveOrUpdate(appsPostTransactionRequest);

	    usersServices.resetPaymentCount(user, "APPS REQUEST :: SUCCESSFULL");
	    paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

	    AppsPostTransactionRequestBean appsPostTransactionRequestBean = new AppsPostTransactionRequestBean(19, "Successfull");

	    appsPostTransactionRequestBean.setRedirectUrl(appsPostTransactionRequest.getRedirectUrl());
	    appsPostTransactionRequestBean.setMerchantId(appsPostTransactionRequest.getMerchantId());
	    appsPostTransactionRequestBean.setMerchantName(appsPostTransactionRequest.getMerchantName());
	    appsPostTransactionRequestBean.setToken(appsPostTransactionRequest.getToken());
	    appsPostTransactionRequestBean.setProCode(appsPostTransactionRequest.getProCode());
	    appsPostTransactionRequestBean.setTxnamt(appsPostTransactionRequest.getTxnamt());
	    appsPostTransactionRequestBean.setCustomerMobileNumber(appsPostTransactionRequest.getCustomerMobileNo());
	    appsPostTransactionRequestBean.setCustomerEmailAddress(appsPostTransactionRequest.getCustomerEmailAddress());
	    appsPostTransactionRequestBean.setSignature(appsPostTransactionRequest.getSignature());
	    appsPostTransactionRequestBean.setVersion(appsPostTransactionRequest.getAppsVersion());
	    appsPostTransactionRequestBean.setTxndesc(appsPostTransactionRequest.getTxndesc());
	    appsPostTransactionRequestBean.setSuccessUrl(appsPostTransactionRequest.getSuccessUrl());
	    appsPostTransactionRequestBean.setFailureUrl(appsPostTransactionRequest.getFailureUrl());
	    appsPostTransactionRequestBean.setBasketId(appsPostTransactionRequest.getBasketId());
	    appsPostTransactionRequestBean.setOrderDate(appsPostTransactionRequest.getOrderDate());
	    appsPostTransactionRequestBean.setCheckoutUrl(appsPostTransactionRequest.getCheckoutUrl());


	    String[] emails = emailNotificationService.getAllActiveEmails();
	    EmailSender.sendEmail("Successful", "Payment Notification from BeFiler.com", emails);

	    return new ResponseEntity<>(appsPostTransactionRequestBean, HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : ", e);
	    if (user != null)
		usersServices.increasePaymentCountByOne(user, "Register :: " + e.getMessage());

	    String[] emails = emailNotificationService.getAllActiveEmails();
	    EmailSender.sendEmail("UnSuccessfull Due to Incomplete data", "Payment Notification from BeFiler.com", emails);
	    return new ResponseEntity<>(new AppsPostTransactionRequestBean(18, "Incomplete data"), HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/response", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<AppsPostTransactionResponseBean> appsResponse(@RequestBody AppsPostTransactionResponseBean appsPostTransactionResponseBean) throws Exception {

	User user = usersServices.getUserFromToken();
	if (user == null) {
	    return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Session Expired"), HttpStatus.OK);
	}
	try {

	    if (appsPostTransactionResponseBean.getRegistrationId() == null) return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
	    if (appsPostTransactionResponseBean.getErrorCode() == null) return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
	    if (appsPostTransactionResponseBean.getTransactionId() == null) return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't complete transaction"), HttpStatus.OK);

	    PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneById(appsPostTransactionResponseBean.getRegistrationId());
	    if (paymentCustomerInfo == null) {
		usersServices.increasePaymentCountByOne(user, "APPS RESPONSE :: Can't complete transaction");
		return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
	    }

	    AppsGetAccessTokenRequest appsGetAccessTokenRequest = paymentCustomerInfo.getAppsGetAccessTokenRequest();
	    if (appsGetAccessTokenRequest == null) {
		usersServices.increasePaymentCountByOne(user, "APPS RESPONSE :: Can't complete transaction");
		return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't complete transaction"), HttpStatus.OK);
	    }

	    List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
	    if (paymentCartList == null || paymentCartList.size() <= 0) {
		usersServices.increasePaymentCountByOne(user, "APPS RESPONSE :: Can't find cart");
		return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Can't find cart"), HttpStatus.OK);
	    }

	    AppsPostTransactionResponse appsPostTransactionResponse = new AppsPostTransactionResponse();

	    appsPostTransactionResponse.setAppsGetAccessTokenRequest(appsGetAccessTokenRequest);

	    appsPostTransactionResponse.setErrorCode(appsPostTransactionResponseBean.getErrorCode());
	    if (StringUtils.isNotEmpty(appsPostTransactionResponseBean.getErrorMessage()))
	    	appsPostTransactionResponse.setErrorMessage(appsPostTransactionResponseBean.getErrorMessage().replace("%20", " "));
	    if (StringUtils.isNotEmpty(appsPostTransactionResponseBean.getTransactionId()))
		appsPostTransactionResponse.setTransactionId(appsPostTransactionResponseBean.getTransactionId());
	    appsPostTransactionResponse.setBasketId(appsPostTransactionResponseBean.getBasketId());
	    appsPostTransactionResponse.setOrderDate(appsPostTransactionResponseBean.getOrderDate());
	    if (StringUtils.isNotEmpty(appsPostTransactionResponseBean.getRdvMessageKey()))
		appsPostTransactionResponse.setRdvMessageKey(appsPostTransactionResponseBean.getRdvMessageKey());
	    if (StringUtils.isNotEmpty(appsPostTransactionResponseBean.getResponseKey()))
		appsPostTransactionResponse.setResponseKey(appsPostTransactionResponseBean.getResponseKey());

	    appsPostTransactionResponseService.saveOrUpdate(appsPostTransactionResponse);

	    AppsPostTransactionResponseBean responseBean;
	    if (appsPostTransactionResponse.getErrorCode().equalsIgnoreCase("000")) {

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

		usersServices.resetPaymentCount(user, "Finalize :: " + appsPostTransactionResponse.getErrorMessage());

		responseBean = new AppsPostTransactionResponseBean(1, appsPostTransactionResponse.getErrorMessage());
		responseBean.setErrorCode(appsPostTransactionResponse.getErrorCode());
		responseBean.setErrorMessage(appsPostTransactionResponse.getErrorMessage());
		responseBean.setTransactionId(appsPostTransactionResponse.getTransactionId());
		responseBean.setBasketId(appsPostTransactionResponse.getBasketId());
		responseBean.setOrderDate(appsPostTransactionResponse.getOrderDate());

		Notification notification = new Notification();
		notification.setArchiveFlag(0);
		notification.setReadFlag(0);
		notification.setCreateDate(CommonUtil.getCurrentTimestamp());
		notification.setToUser(user.getId());
		notification.setNotificationTitle("Payment Receipt from BeFiler.com");
		notification.setNotificationDescription("We have successfully received your payment thru [Payment Method e.g. Credit Card] of PKR " + appsGetAccessTokenRequest.getAppsPostTransactionRequest().getTxnamt());

		notificationServices.save(notification);

		EmailSender.sendEmail(EmailUtil.paymentTemaplateForApps(user.getFullName(), appsGetAccessTokenRequest.getAppsPostTransactionRequest().getTxnamt() + "", appsPostTransactionResponse.getBasketId(), appsPostTransactionResponse.getTransactionId(), appsPostTransactionResponse.getErrorMessage()), "Payment Receipt from BeFiler.com", user.getEmailAddress());

	    } else {
		usersServices.increasePaymentCountByOne(user, "Finalize :: " + appsPostTransactionResponse.getErrorMessage());

		responseBean = new AppsPostTransactionResponseBean(2, appsPostTransactionResponse.getErrorMessage());
		responseBean.setErrorCode(appsPostTransactionResponse.getErrorCode());
		responseBean.setErrorMessage(appsPostTransactionResponse.getErrorMessage());
	    }

	    String[] emails = emailNotificationService.getAllActiveEmails();
	    EmailSender.sendEmail("Successfull", "Payment Notification from BeFiler.com", emails);

	    return new ResponseEntity<>(responseBean, HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : ", e);
	    usersServices.increasePaymentCountByOne(user, "Finalize :: " + e.getMessage());
	    return new ResponseEntity<>(new AppsPostTransactionResponseBean(0, "Incomplete data"), HttpStatus.OK);
	}

    }
}
