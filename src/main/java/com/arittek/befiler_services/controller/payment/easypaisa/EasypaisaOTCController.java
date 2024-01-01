package com.arittek.befiler_services.controller.payment.easypaisa;

import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;

/*
@RestController
@RequestMapping(value = "/easypaisa/otc")
public class EasypaisaOTCController {

    private final UsersServices usersServices;
    private final PaymentServices paymentServices;
    private final SettingsServices settingsServices;
    private final PaymentCartServices paymentCartServices;
    private final EasypaisaOTCServices easypaisaOTCServices;
    private final PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${easypaisa.redirect.storeId}")
    private String STORE_ID;
    @Value("${easypaisa.username}")
    private String USERNAME;
    @Value("${easypaisa.password}")
    private String PASSWORD;
    @Value("${easypaisa.otc.url}")
    private String OTC_URL;


    @Autowired
    public EasypaisaOTCController(UsersServices usersServices, PaymentServices paymentServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, EasypaisaOTCServices easypaisaOTCServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.settingsServices = settingsServices;
        this.paymentCartServices = paymentCartServices;
        this.easypaisaOTCServices = easypaisaOTCServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<EasypaisaOTCResponseBean> easypaisaOTCEndpoint(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {

        User user = null;
        try {

            if (!StringUtils.isNotEmpty(requestBean.getCustomer())) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Please enter name."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Please enter mobile no."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Please enter email address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Please enter residential address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Please enter billing address"), HttpStatus.OK);
            }

            if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Empty cart"), HttpStatus.OK);
            }

            if (requestBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Session Expired"), HttpStatus.OK);
            }

            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Max attempts"), HttpStatus.OK);
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
                                    return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Payment cart error"), HttpStatus.OK);
                }
            }

            */
/*Save Customer Info data*//*

            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(PaymentMethod.EASYPAISA_OTC, user, requestBean);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Customer Info Error"), HttpStatus.OK);
            }

            */
/*Generate Order ID*//*

            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() */
/*+ "-"*//*

                    + sdf.format(paymentCustomerInfo.getCurrentDate()) */
/*+ "-"*//*

                    + String.format("%05d", paymentCustomerInfo.getId());
            paymentCustomerInfo.setOrderId(orderId);

            paymentCustomerInfo = paymentCustomerInfoServices.update(paymentCustomerInfo);


            */
/*Save Request in Database*//*

            EasypaisaOTCRequest easypaisaOTCRequest = new EasypaisaOTCRequest();
            easypaisaOTCRequest.setPaymentCustomerInfo(paymentCustomerInfo);

            easypaisaOTCRequest.setCredentials(EasypaisaUtil.getCredentials(USERNAME, PASSWORD));
            easypaisaOTCRequest.setOrderId(paymentCustomerInfo.getOrderId());
            easypaisaOTCRequest.setStoreId(STORE_ID);
            easypaisaOTCRequest.setTransactionAmount(amount + "");
            easypaisaOTCRequest.setTransactionType("OTC");
            easypaisaOTCRequest.setMsisdn(EasypaisaUtil.mobileNo(paymentCustomerInfo.getMobileNo()));
            easypaisaOTCRequest.setEmailAddress(paymentCustomerInfo.getEmailAddress());
            easypaisaOTCRequest.setTokenExpiry(EasypaisaUtil.getExpireDateForOTPTransaction());

            easypaisaOTCRequest.setCurrentDate(CommonUtil.getCurrentTimestamp());
            easypaisaOTCRequest.setUser(user);

            easypaisaOTCRequest = easypaisaOTCServices.saveOrUpdateRequest(easypaisaOTCRequest);
            if (easypaisaOTCRequest == null) {
                usersServices.increasePaymentCountByOne(user, "Database Error");
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Database Error"), HttpStatus.OK);
            }

            EasypaisaOTCRequestToAPI easypaisaOTCRequestToAPI = new EasypaisaOTCRequestToAPI(easypaisaOTCRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Credentials", easypaisaOTCRequest.getCredentials());
            HttpEntity<EasypaisaOTCRequestToAPI> request = new HttpEntity<>(easypaisaOTCRequestToAPI, headers);

            ResponseEntity<EasypaisaOTCResponseFromAPI> easypaisaOTCResponseFromAPIResponseEntity = restTemplate.postForEntity(OTC_URL, request, EasypaisaOTCResponseFromAPI.class);

            if (easypaisaOTCResponseFromAPIResponseEntity == null || easypaisaOTCResponseFromAPIResponseEntity.getBody() == null) {
                usersServices.increasePaymentCountByOne(user, "OTC Transaction :: Payment gateway is not responding");
                return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Payment gateway is not responding"), HttpStatus.OK);
            }

            EasypaisaOTCResponse easypaisaOTCResponse = new EasypaisaOTCResponse();
            easypaisaOTCResponse.setEasypaisaOTCRequest(easypaisaOTCRequest);

            easypaisaOTCResponse.setResponseCode(easypaisaOTCResponseFromAPIResponseEntity.getBody().getResponseCode());
            easypaisaOTCResponse.setResponseDesc(easypaisaOTCResponseFromAPIResponseEntity.getBody().getResponseDesc());

            if (StringUtils.isNotEmpty(easypaisaOTCResponseFromAPIResponseEntity.getBody().getOrderId()))
                easypaisaOTCResponse.setOrderId(easypaisaOTCResponseFromAPIResponseEntity.getBody().getOrderId());
            if (StringUtils.isNotEmpty(easypaisaOTCResponseFromAPIResponseEntity.getBody().getStoreId()))
                easypaisaOTCResponse.setStoreId(easypaisaOTCResponseFromAPIResponseEntity.getBody().getStoreId());
            if (StringUtils.isNotEmpty(easypaisaOTCResponseFromAPIResponseEntity.getBody().getPaymentToken()))
                easypaisaOTCResponse.setPaymentToken(easypaisaOTCResponseFromAPIResponseEntity.getBody().getPaymentToken());
            if (StringUtils.isNotEmpty(easypaisaOTCResponseFromAPIResponseEntity.getBody().getTransactionDateTime()))
                easypaisaOTCResponse.setTransactionDateTime(easypaisaOTCResponseFromAPIResponseEntity.getBody().getTransactionDateTime());
            if (StringUtils.isNotEmpty(easypaisaOTCResponseFromAPIResponseEntity.getBody().getPaymentTokenExpiryDateTime()))
                easypaisaOTCResponse.setPaymentTokenExpiryDateTime(easypaisaOTCResponseFromAPIResponseEntity.getBody().getPaymentTokenExpiryDateTime());

            easypaisaOTCResponse.setCurrentDate(CommonUtil.getCurrentTimestamp());

            easypaisaOTCServices.saveOrUpdateResponse(easypaisaOTCResponse);


            if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0000")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(13, "Successfull");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                easypaisaOTCResponseBean.setOrderId(easypaisaOTCResponse.getOrderId());
                easypaisaOTCResponseBean.setPaymentToken(easypaisaOTCResponse.getPaymentToken());
                easypaisaOTCResponseBean.setTransactionDateTime(easypaisaOTCResponse.getTransactionDateTime());
                easypaisaOTCResponseBean.setPaymentTokenExpiryDateTime(easypaisaOTCResponse.getPaymentTokenExpiryDateTime());

                usersServices.resetPaymentCount(user, "OTC Transaction :: " + easypaisaOTCResponse.getResponseDesc());
                paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);

            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0001")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "SYSTEM ERROR");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0002")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "REQUIRED FIELD MISSING");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0005")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "MERCHANT ACCOUNT NOT ACTIVE");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0006")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "INVALID STORE ID");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0007")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "STORE NOT ACTIVE");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0008")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "PAYMENT METHOD NOT ENABLED");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0010")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "INVALID CREDENTIALS");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0015")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "INVALID_TOKEN_EXPIRY");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            } else if (easypaisaOTCResponse.getResponseCode().equalsIgnoreCase("0016")) {
                EasypaisaOTCResponseBean easypaisaOTCResponseBean = new EasypaisaOTCResponseBean(14, "Expiry date should be future date");

                easypaisaOTCResponseBean.setResponseCode(easypaisaOTCResponse.getResponseCode());
                easypaisaOTCResponseBean.setResponseDesc(easypaisaOTCResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaOTCResponseBean, HttpStatus.OK);
            }

            return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Incomplete data"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, CommonUtil.getRootCause(e).getMessage());
            return new ResponseEntity<>(new EasypaisaOTCResponseBean(12, "Incomplete data"), HttpStatus.OK);
        }
    }



}
*/
