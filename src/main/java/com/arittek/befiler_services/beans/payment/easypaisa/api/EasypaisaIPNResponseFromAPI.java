package com.arittek.befiler_services.beans.payment.easypaisa.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EasypaisaIPNResponseFromAPI {

    private String paidDateTime;
    private String transactionId;
    private String transactionStatus;
    private String tokenExpiryDateTime;
    private String msisdn;
    private String paymentMethod;
    private String paymentToken;
    private String storeName;
    private String transactionAmount;
    private String accountNumber;
    private String orderId;
    private String orderDateTime;
    private String storeId;




}
