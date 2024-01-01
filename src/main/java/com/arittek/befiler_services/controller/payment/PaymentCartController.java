package com.arittek.befiler_services.controller.payment;

import com.arittek.befiler_services.beans.InputBean;
import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.PromoCodeType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import com.arittek.befiler_services.services.notifications.EmailNotificationService;
import com.arittek.befiler_services.services.payment.settings.SettingPaymentServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.PromoCodeServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/payment/cart")
public class PaymentCartController {

    private UsersServices usersServices;
    private PaymentServices paymentServices;
    private PromoCodeServices promoCodeServices;
    private PaymentCartServices paymentCartServices;
    private SettingPaymentServices settingPaymentServices;
    private EmailNotificationService emailNotificationService;

    @Autowired
    public PaymentCartController(UsersServices usersServices, PaymentServices paymentServices, PromoCodeServices promoCodeServices, PaymentCartServices paymentCartServices, SettingPaymentServices settingPaymentServices, EmailNotificationService emailNotificationService) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.promoCodeServices = promoCodeServices;
        this.paymentCartServices = paymentCartServices;
        this.settingPaymentServices = settingPaymentServices;
        this.emailNotificationService = emailNotificationService;
    }


    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getCart(@RequestBody InputBean inputBean) {
        try {
            User user;
            if (inputBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
            }

            paymentCartServices.checkForPaymentCartByUser(user);

            List<PaymentCartBean> paymentCartBeanList = new ArrayList<>();
            List<PaymentCart> paymentCartList = paymentCartServices.findAllActivePaymentCartsByUser(user);
            if (paymentCartList != null && paymentCartList.size() > 0) {
                for (PaymentCart paymentCart : paymentCartList) {
                    if (paymentCart != null && paymentCart.getProductType() != null) {
                        PaymentCartBean paymentCartBean = new PaymentCartBean();
                        if (paymentCart.getProductType() == ProductType.TAXFORM) {
                            if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                if (payment == null) {
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription(paymentCart.getTaxform().getTaxformYear().getYear() + "");
                                    paymentCartBean.setDetail(paymentCart.getTaxform().getTaxformYear().getYear() + " Tax will be filed on your behalf with Federal Board of Revenue(FBR)");
                                    paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
                                    paymentCartBean.setTaxformId(paymentCart.getTaxform().getId() + "");
                                    paymentCartBean.setRemove(true);

                                    paymentCartBeanList.add(paymentCartBean);
                                } else {
                                    // TODO
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription("NTN");
                                    paymentCartBean.setDetail("BeFiler will register your National Tax Number(NTN) with FBR on your behalf.");
                                    paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
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

            StatusBean statusBean = new StatusBean(1, "Successfully");
            statusBean.setResponse(paymentCartBeanList);

            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data."), HttpStatus.OK);
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<StatusBean> updateCart(@RequestBody InputBean inputBean) {
        try {
            User user;
            if (inputBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
            }

            PromoCode promoCode = null;
            if (StringUtils.isNotEmpty(inputBean.getPromoCode())) {
                promoCode = promoCodeServices.findOneActiveRecordByPromoCode(inputBean.getPromoCode());
                if (promoCode == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Promo code is expired"), HttpStatus.OK);
                } else {
                    if (promoCode.getPromoCodeType() == PromoCodeType.SPECIFIC) {
                        if (promoCode.getPromoCodeAssignList() != null &&
                                promoCode.getPromoCodeAssignList().size() > 0) {
                            Boolean check = promoCodeServices.checkIfPromoCodeIsAssignedToUser(promoCode, user);
                            if (!check) {
                                return new ResponseEntity<>(new StatusBean(0, "Promo code is expired"), HttpStatus.OK);
                            }
                        } else {
                            return new ResponseEntity<>(new StatusBean(0, "Promo code is expired"), HttpStatus.OK);
                        }
                    }
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Please enter promo code"), HttpStatus.OK);
            }

            paymentCartServices.checkForPaymentCartByUser(user);

            List<PaymentCartBean> paymentCartBeanList = new ArrayList<>();
            List<PaymentCart> paymentCartList = paymentCartServices.findAllActivePaymentCartsByUser(user);
            if (paymentCartList != null && paymentCartList.size() > 0) {
                for (PaymentCart paymentCart : paymentCartList) {
                    if (paymentCart != null && paymentCart.getProductType() != null) {
                        PaymentCartBean paymentCartBean = new PaymentCartBean();
                        if (paymentCart.getProductType() == ProductType.TAXFORM) {
                            if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                if (payment == null) {
                                    /*SettingTaxformPayment settingTaxformPayment = settingTaxformPaymentServices.findOneByTaxformYearAndPromoCode(paymentCart.getTaxform().getTaxformYear(), promoCode);*/
                                    SettingPayment settingPayment = settingPaymentServices.findPaymentByPromoCodeAndYearForTaxform(promoCode, paymentCart.getTaxform().getTaxformYear());
                                    if (settingPayment != null) {
                                        paymentCart.setSettingPayment(settingPayment);
                                        paymentCart = paymentCartServices.saveOrUpdateToActiveStatus(paymentCart);

                                        if (promoCode.getPromoCodeType() == PromoCodeType.SINGLE_USE) {
                                            promoCodeServices.disablePromoCode(promoCode);
                                        }
                                    }
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription(paymentCart.getTaxform().getTaxformYear().getYear() + "");
                                    paymentCartBean.setDetail(paymentCart.getTaxform().getTaxformYear().getYear() + " Tax will be filed on your behalf with Federal Board of Revenue(FBR)");
                                    paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
                                    paymentCartBean.setTaxformId(paymentCart.getTaxform().getId() + "");
                                    paymentCartBean.setRemove(true);

                                    paymentCartBeanList.add(paymentCartBean);
                                } else {
                                    // TODO
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    SettingPayment settingPayment = settingPaymentServices.findPaymentByPromoCodeForNTNRegistration(promoCode);
                                    /*SettingFbrPayment settingFbrPayment = settingFbrPaymentServices.findOnePromoCode(promoCode);*/
                                    if (settingPayment != null) {
                                        paymentCart.setSettingPayment(settingPayment);

                                        paymentCartServices.saveOrUpdateToActiveStatus(paymentCart);

                                        if (promoCode.getPromoCodeType() == PromoCodeType.SINGLE_USE) {
                                            promoCodeServices.disablePromoCode(promoCode);
                                        }
                                    }
                                    paymentCartBean.setPaymentCartId(paymentCart.getId());
                                    paymentCartBean.setDescription("NTN");
                                    paymentCartBean.setDetail("BeFiler will register your National Tax Number(NTN) with FBR on your behalf.");
                                    paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
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

            StatusBean statusBean = new StatusBean(1, "Successfully");
            statusBean.setResponse(paymentCartBeanList);

            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data."), HttpStatus.OK);
    }

}
