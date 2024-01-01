package com.arittek.befiler_services.beans.payment.easypaisa.api;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EasypaisaOTCRequestToAPI {

    private String orderId;
    private String storeId;
    private String transactionAmount;
    private String transactionType;
    private String msisdn;
    private String emailAddress;
    private String tokenExpiry;

    public EasypaisaOTCRequestToAPI(EasypaisaOTCRequest easypaisaOTCRequest) {
        this.orderId = easypaisaOTCRequest.getOrderId();
        this.storeId = easypaisaOTCRequest.getStoreId();
        this.transactionAmount = easypaisaOTCRequest.getTransactionAmount();
        this.transactionType = easypaisaOTCRequest.getTransactionType();
        this.msisdn = easypaisaOTCRequest.getMsisdn();
        this.emailAddress = easypaisaOTCRequest.getEmailAddress();
        this.tokenExpiry = easypaisaOTCRequest.getTokenExpiry();
    }
}
