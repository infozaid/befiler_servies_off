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
public class EasypaisaMAResponseBean {

    private Integer code;
    private String message;

    private String responseCode;
    private String responseDesc;
    private String orderId;
    private String storeId;
    private String transactionId;
    private String transactionDateTime;

    public EasypaisaMAResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
