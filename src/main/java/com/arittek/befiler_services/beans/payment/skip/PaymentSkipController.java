package com.arittek.befiler_services.beans.payment.skip;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.PromoCodeType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.services.CorporateEmployeeServices;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.TaxformYearsServices;
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
@RequestMapping(value = "/payment/skip")
public class PaymentSkipController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private TaxformServices taxformServices;
    private SettingsServices settingsServices;
    private PromoCodeServices promoCodeServices;
    private PaymentCartServices paymentCartServices;
    private NotificationServices notificationServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Autowired
    public PaymentSkipController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, SettingsServices settingsServices, PromoCodeServices promoCodeServices, PaymentCartServices paymentCartServices, NotificationServices notificationServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
	this.usersServices = usersServices;
	this.paymentServices = paymentServices;
	this.taxformServices = taxformServices;
	this.settingsServices = settingsServices;
	this.promoCodeServices = promoCodeServices;
	this.paymentCartServices = paymentCartServices;
	this.notificationServices = notificationServices;
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
}
