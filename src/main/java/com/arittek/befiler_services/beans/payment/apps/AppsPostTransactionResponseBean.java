package com.arittek.befiler_services.beans.payment.apps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AppsPostTransactionResponseBean {

    private Integer code;
    private String message;

    private Integer registrationId;

    private String errorCode;
    private String errorMessage;
    private String transactionId;
    private String basketId;
    private String orderDate;
    private String rdvMessageKey;
    private String responseKey;

    public AppsPostTransactionResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
