package com.arittek.befiler_services.beans.payment.easypaisa;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EasypaisaOTCResponseBean {

    private Integer code;
    private String message;

    private String responseCode;
    private String responseDesc;
    private String orderId;
    private String paymentToken;
    private String transactionDateTime;
    private String paymentTokenExpiryDateTime;

    public EasypaisaOTCResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
