package com.arittek.befiler_services.beans.payment.finja;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FinjaResponseBean {

    private Integer code;
    private String message;

    private Integer registrationId;

    private Integer responseCode;
    private String responseMsg;

    private String customerName;

    private String transactionCode;
    private String orderId;

    public FinjaResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
