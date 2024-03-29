package com.arittek.befiler_services.beans.payment.easypaisa.api;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMARequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EasypaisaMARequestToAPI {
    private String orderId;
    private String storeId;
    private String transactionAmount;
    private String transactionType;
    private String mobileAccountNo;
    private String emailAddress;

    public EasypaisaMARequestToAPI(EasypaisaMARequest easypaisaMARequest) {
        this.orderId = easypaisaMARequest.getOrderId();
        this.storeId = easypaisaMARequest.getStoreId();
        this.transactionAmount = easypaisaMARequest.getTransactionAmount();
        this.transactionType = easypaisaMARequest.getTransactionType();
        this.mobileAccountNo = easypaisaMARequest.getMobileAccountNo();
        this.emailAddress = easypaisaMARequest.getEmailAddress();
    }
}
