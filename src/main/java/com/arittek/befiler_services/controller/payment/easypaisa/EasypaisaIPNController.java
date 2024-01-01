package com.arittek.befiler_services.controller.payment.easypaisa;

import com.arittek.befiler_services.model.enums.ProductType;

/*
@RestController
@RequestMapping(value = "/easypaisa/ipn")
public class EasypaisaIPNController {

    private final UsersServices usersServices;
    private final PaymentServices paymentServices;
    private final TaxformServices taxformServices;
    private final PaymentCartServices paymentCartServices;
    private final EasypaisaMAServices easypaisaMAServices;
    private final EasypaisaOTCServices easypaisaOTCServices;
    private final EasypaisaIPNServices easypaisaIPNServices;
    private final NotificationServices notificationServices;
    private final FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private final PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public EasypaisaIPNController(UsersServices usersServices, PaymentServices paymentServices, TaxformServices taxformServices, PaymentCartServices paymentCartServices, EasypaisaMAServices easypaisaMAServices, EasypaisaOTCServices easypaisaOTCServices, EasypaisaIPNServices easypaisaIPNServices, NotificationServices notificationServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.taxformServices = taxformServices;
        this.paymentCartServices = paymentCartServices;
        this.easypaisaMAServices = easypaisaMAServices;
        this.easypaisaOTCServices = easypaisaOTCServices;
        this.easypaisaIPNServices = easypaisaIPNServices;
        this.notificationServices = notificationServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }




    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST)
    public void getIPN(String url) {
        Logger4j.getLogger().info("=================IPN HANDLER=================");
        try {
            if (StringUtils.isNotEmpty(url)) {
                Logger4j.getLogger().info("=================URL : " + url + "=================");

                ResponseEntity<EasypaisaIPNResponseFromAPI> easypaisaIPNResponseFromAPIResponseEntity = restTemplate.getForEntity(url, EasypaisaIPNResponseFromAPI.class);

                Logger4j.getLogger().info(easypaisaIPNResponseFromAPIResponseEntity);

                EasypaisaIPNResponseFromAPI easypaisaIPNResponseFromAPI = easypaisaIPNResponseFromAPIResponseEntity.getBody();

                if (easypaisaIPNResponseFromAPI != null) {
                    Logger4j.getLogger().info("=================BODY : " + easypaisaIPNResponseFromAPI + "=================");

                    if (!StringUtils.isNotEmpty(easypaisaIPNResponseFromAPI.getOrderId())) {
                        Logger4j.getLogger().info("=================ORDER ID : NOT FOUND=================");
                        return;
                    }
                    Logger4j.getLogger().info("=================ORDER ID : " + easypaisaIPNResponseFromAPI.getOrderId() + "=================");

                    PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.findOneByOrderId(easypaisaIPNResponseFromAPI.getOrderId());
                    if (paymentCustomerInfo == null) {
                        Logger4j.getLogger().info("=================ORDER : NOT FOUND=================");
                        return;
                    }
                    Logger4j.getLogger().info("=================ORDER : " + paymentCustomerInfo.getId() + "=================");

                    if (!StringUtils.isNotEmpty(easypaisaIPNResponseFromAPI.getPaymentMethod())) {
                        Logger4j.getLogger().info("=================PAYMENT METHOD : NOT FOUND=================");
                        return;
                    }
                    Logger4j.getLogger().info("=================PAYMENT METHOD : " + easypaisaIPNResponseFromAPI.getPaymentMethod() + "=================");

                    if (easypaisaIPNResponseFromAPI.getPaymentMethod().equals("OTC")) {
                        Logger4j.getLogger().info("=================PAYMENT METHOD : OTC RETURN=================");
                        EasypaisaOTCRequest easypaisaOTCRequest = paymentCustomerInfo.getEasypaisaOTCRequest();
                        if (easypaisaOTCRequest == null) {
                            Logger4j.getLogger().info("=================OTC REQUEST : NULL=================");
                            return;
                        }
                        Logger4j.getLogger().info("=================OTC REQUEST : NOT NULL=================");

                    } else if (easypaisaIPNResponseFromAPI.getPaymentMethod().equals("MA")) {
                        Logger4j.getLogger().info("=================PAYMENT METHOD : MA RETURN=================");
                        EasypaisaMARequest easypaisaMARequest = paymentCustomerInfo.getEasypaisaMARequest();
                        if (easypaisaMARequest == null) {
                            Logger4j.getLogger().info("=================MA REQUEST : NULL=================");
                            return;
                        }
                        Logger4j.getLogger().info("=================MA REQUEST : NOT NULL=================");
                    } else {
                        Logger4j.getLogger().info("=================PAYMENT METHOD : " + easypaisaIPNResponseFromAPI.getPaymentMethod() + " RETURN=================");
                    }


                    List<PaymentCart> paymentCartList = paymentCartServices.findAllByPaymentCustomerInfoRequestAndActiveStatus(paymentCustomerInfo);
                    Integer amount = 0L;
                    if (paymentCartList != null && paymentCartList.size() > 0) {
                        for (PaymentCart paymentCart : paymentCartList) {
                            if (paymentCart != null && paymentCart.getProductType() != null) {
                                if (paymentCart.getProductType() == ProductType.TAXFORM) {
                                    if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                        Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                        if (payment == null) {
                                            amount += Math.round(paymentCart.getSettingPayment().getAmount());
                                        } else {
                                            Logger4j.getLogger().info("=================PAYMENT CART : TAXFORM ALREADY PAID=================");
                                            return;
                                        }
                                    }
                                } else if (paymentCart.getProductType() == ProductType.NTN) {
                                    if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                        Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                        if (payment == null) {
                                            amount += Math.round(paymentCart.getSettingPayment().getAmount());
                                        } else {
                                            Logger4j.getLogger().info("=================PAYMENT CART : NTN REGISTRATION ALREADY PAID=================");
                                            return;
                                        }
                                    }
                                }
                            } else {
                                Logger4j.getLogger().info("=================PAYMENT CART : NOT FOUND=================");
                                return;
                            }
                        }
                    } else {
                        Logger4j.getLogger().info("=================PAYMENT CART : NOT FOUND=================");
                        return;
                    }

                    EasypaisaIPN easypaisaIPN = new EasypaisaIPN();
                    easypaisaIPN.setPaymentCustomerInfo(paymentCustomerInfo);

                    easypaisaIPN.setPaidDateTime(easypaisaIPN.getPaidDateTime());
                    easypaisaIPN.setTransactionId(easypaisaIPN.getTransactionId());
                    easypaisaIPN.setTransactionStatus(easypaisaIPN.getTransactionStatus());
                    easypaisaIPN.setMsisdn(easypaisaIPN.getMsisdn());
                    easypaisaIPN.setPaymentMethod(easypaisaIPN.getPaymentMethod());
                    easypaisaIPN.setPaymentToken(easypaisaIPN.getPaymentToken());
                    easypaisaIPN.setStoreName(easypaisaIPN.getStoreName());
                    easypaisaIPN.setTransactionAmount(easypaisaIPN.getTransactionAmount());
                    easypaisaIPN.setAccountNumber(easypaisaIPN.getAccountNumber());
                    easypaisaIPN.setOrderId(easypaisaIPN.getOrderId());
                    easypaisaIPN.setOrderDateTime(easypaisaIPN.getOrderDateTime());
                    easypaisaIPN.setStoreId(easypaisaIPN.getStoreId());

                    easypaisaIPNServices.saveOrUpdate(easypaisaIPN);

                    List<PaymentCart> updatedPaymentCart = paymentCartServices.saveOrUpdateToPaymentStatus(paymentCartList);
                    if (updatedPaymentCart != null && updatedPaymentCart.size() > 0) {
                        for (PaymentCart paymentCart : paymentCartList) {
                            Payment payment = new Payment();

                            payment.setUser(paymentCustomerInfo.getUser());
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

                    usersServices.resetPaymentCount(paymentCustomerInfo.getUser(), "IPN Handler");

                    Notification notification = new Notification();
                    notification.setArchiveFlag(0);
                    notification.setReadFlag(0);
                    notification.setCreateDate(CommonUtil.getCurrentTimestamp());
                    notification.setToUser(paymentCustomerInfo.getUser().getId());
                    notification.setNotificationTitle("Payment Receipt from BeFiler.com");
                    notification.setNotificationDescription("We have successfully received your payment thru Finja of PKR " + amount);

                    notificationServices.save(notification);

                    EmailConfiguration.sendEmail(EmailUtil.paymentTemaplateForFinja(paymentCustomerInfo.getUser().getFullName(), amount + "", paymentCustomerInfo.getOrderId(), easypaisaIPN.getTransactionId(), easypaisaIPN.getTransactionStatus()), "Payment Receipt from BeFiler.com", paymentCustomerInfo.getUser().getEmailAddress());

                } else {
                    Logger4j.getLogger().info("=================BODY : NOT FOUND=================");
                }
            } else {
                Logger4j.getLogger().info("=================URL : NOT FOUND=================");
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
        }
    }
}
*/
