package com.arittek.befiler_services.controller.payment.easypaisa;

import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;

/*
@RestController
@RequestMapping(value = "/easypaisa/ma")
public class EasypaisaMAController {
    private final UsersServices usersServices;
    private final PaymentServices paymentServices;
    private final SettingsServices settingsServices;
    private final PaymentCartServices paymentCartServices;
    private final EasypaisaMAServices easypaisaMAServices;
    private final PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${easypaisa.redirect.storeId}")
    private String STORE_ID;
    @Value("${easypaisa.username}")
    private String USERNAME;
    @Value("${easypaisa.password}")
    private String PASSWORD;
    @Value("${easypaisa.ma.url}")
    private String MA_URL;

    @Autowired
    public EasypaisaMAController(UsersServices usersServices, PaymentServices paymentServices, SettingsServices settingsServices, PaymentCartServices paymentCartServices, EasypaisaMAServices easypaisaMAServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.paymentServices = paymentServices;
        this.settingsServices = settingsServices;
        this.paymentCartServices = paymentCartServices;
        this.easypaisaMAServices = easypaisaMAServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<EasypaisaMAResponseBean> easypaisaMAEndpoint(@RequestBody PaymentCustomerInfoBean requestBean) throws Exception {
        User user = null;
        try {

            if (!StringUtils.isNotEmpty(requestBean.getCustomer())) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Please enter name."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getMobileNo())) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Please enter mobile no."), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getEmailAddress())) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Please enter email address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getResidentialAddress())) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Please enter residential address"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(requestBean.getBillingAddress())) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Please enter billing address"), HttpStatus.OK);
            }

            if (requestBean.getPaymentCartBeanList() == null || requestBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Empty cart"), HttpStatus.OK);
            }

            if (requestBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(requestBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Session Expired"), HttpStatus.OK);
            }

            Settings settings = settingsServices.getActiveRecord();
            UserAttemptsCount userAttemptsCount = usersServices.findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null && userAttemptsCount.getPaymentAttemptsCount() != null) {
                if (settings != null && settings.getMaxPaymentAttempts() != null) {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= settings.getMaxPaymentAttempts()) {
                        return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Max attempts"), HttpStatus.OK);
                    }
                } else {
                    if (userAttemptsCount.getPaymentAttemptsCount() >= 5) {
                        return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Max attempts"), HttpStatus.OK);
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
                                    return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    usersServices.increasePaymentCountByOne(user, "Register :: NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear());
                                    return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                        return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    usersServices.increasePaymentCountByOne(user, "Register :: Incomplete Payment Cart");
                    return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Payment cart error"), HttpStatus.OK);
                }
            }

            */
/*Save Customer Info data*//*

            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(PaymentMethod.EASYPAISA_MA, user, requestBean);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Customer Info Error"), HttpStatus.OK);
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

            EasypaisaMARequest easypaisaMARequest = new EasypaisaMARequest();
            easypaisaMARequest.setPaymentCustomerInfo(paymentCustomerInfo);

            easypaisaMARequest.setCredentials(EasypaisaUtil.getCredentials(USERNAME, PASSWORD));
            easypaisaMARequest.setOrderId(paymentCustomerInfo.getOrderId());
            easypaisaMARequest.setStoreId(STORE_ID);
            easypaisaMARequest.setTransactionAmount(amount + "");
            easypaisaMARequest.setTransactionType("MA");
            easypaisaMARequest.setMobileAccountNo(EasypaisaUtil.mobileNo(paymentCustomerInfo.getMobileNo()));
            easypaisaMARequest.setEmailAddress(paymentCustomerInfo.getEmailAddress());

            easypaisaMARequest.setCurrentDate(CommonUtil.getCurrentTimestamp());
            easypaisaMARequest.setUser(user);

            easypaisaMARequest = easypaisaMAServices.saveOrUpdateRequest(easypaisaMARequest);
            if (easypaisaMARequest == null) {
                usersServices.increasePaymentCountByOne(user, "Database Error");
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Database Error"), HttpStatus.OK);
            }

            EasypaisaMARequestToAPI easypaisaMARequestToAPI = new EasypaisaMARequestToAPI(easypaisaMARequest);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Credentials", easypaisaMARequest.getCredentials());
            HttpEntity<EasypaisaMARequestToAPI> request = new HttpEntity<>(easypaisaMARequestToAPI, headers);
            ResponseEntity<EasypaisaMAResponseFromAPI> easypaisaMAResponseFromAPIResponseEntity = restTemplate.postForEntity(MA_URL, request, EasypaisaMAResponseFromAPI.class);

            if (easypaisaMAResponseFromAPIResponseEntity == null || easypaisaMAResponseFromAPIResponseEntity.getBody() == null) {
                usersServices.increasePaymentCountByOne(user, "MA Transaction :: Payment gateway is not responding");
                return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Payment gateway is not responding"), HttpStatus.OK);
            }

            EasypaisaMAResponse easypaisaMAResponse = new EasypaisaMAResponse();
            easypaisaMAResponse.setEasypaisaMARequest(easypaisaMARequest);

            easypaisaMAResponse.setResponseCode(easypaisaMAResponseFromAPIResponseEntity.getBody().getResponseCode());
            easypaisaMAResponse.setResponseDesc(easypaisaMAResponseFromAPIResponseEntity.getBody().getResponseDesc());

            if (StringUtils.isNotEmpty(easypaisaMAResponseFromAPIResponseEntity.getBody().getOrderId()))
                easypaisaMAResponse.setOrderId(easypaisaMAResponseFromAPIResponseEntity.getBody().getOrderId());
            if (StringUtils.isNotEmpty(easypaisaMAResponseFromAPIResponseEntity.getBody().getStoreId()))
                easypaisaMAResponse.setStoreId(easypaisaMAResponseFromAPIResponseEntity.getBody().getStoreId());
            if (StringUtils.isNotEmpty(easypaisaMAResponseFromAPIResponseEntity.getBody().getTransactionId()))
                easypaisaMAResponse.setTransactionId(easypaisaMAResponseFromAPIResponseEntity.getBody().getTransactionId());
            if (StringUtils.isNotEmpty(easypaisaMAResponseFromAPIResponseEntity.getBody().getTransactionDateTime()))
                easypaisaMAResponse.setTransactionDateTime(easypaisaMAResponseFromAPIResponseEntity.getBody().getTransactionDateTime());

            easypaisaMAResponse.setCurrentDate(CommonUtil.getCurrentTimestamp());

            easypaisaMAServices.saveOrUpdateResponse(easypaisaMAResponse);

            if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0000")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(16, "Successfull");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                easypaisaMAResponseBean.setOrderId(easypaisaMAResponse.getOrderId());
                easypaisaMAResponseBean.setTransactionId(easypaisaMAResponse.getTransactionId());
                easypaisaMAResponseBean.setTransactionDateTime(easypaisaMAResponse.getTransactionDateTime());

                usersServices.resetPaymentCount(user, "MA Transaction :: " + easypaisaMAResponseBean.getResponseDesc());
                paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);

            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0001")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "SYSTEM ERROR");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0002")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "REQUIRED FIELD MISSING");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0005")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "MERCHANT ACCOUNT NOT ACTIVE");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0006")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "INVALID STORE ID");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0007")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "STORE NOT ACTIVE");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0008")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "PAYMENT METHOD NOT ENABLED");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0010")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "INVALID CREDENTIALS");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0013")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "LOW BALANCE");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0014")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "ACCOUNT DOES NOT EXIST");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            } else if (easypaisaMAResponse.getResponseCode().equalsIgnoreCase("0017")) {
                EasypaisaMAResponseBean easypaisaMAResponseBean = new EasypaisaMAResponseBean(17, "Incomplete merchant information");

                easypaisaMAResponseBean.setResponseCode(easypaisaMAResponse.getResponseCode());
                easypaisaMAResponseBean.setResponseDesc(easypaisaMAResponse.getResponseDesc());

                return new ResponseEntity<>(easypaisaMAResponseBean, HttpStatus.OK);
            }

            return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Incomplete data"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            if (user != null)
                usersServices.increasePaymentCountByOne(user, CommonUtil.getRootCause(e).getMessage());
            return new ResponseEntity<>(new EasypaisaMAResponseBean(15, "Incomplete data"), HttpStatus.OK);
        }
    }
}
*/
