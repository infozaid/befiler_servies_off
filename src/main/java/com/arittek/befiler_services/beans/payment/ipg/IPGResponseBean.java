package com.arittek.befiler_services.beans.payment.ipg;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class IPGResponseBean implements Serializable {

    private Integer code;
    private String message;

    private Integer responseCode;
    private String responseDescription;

    private String orderId;
    private String approvalCode;
    private String transactionId;
    private String uniqueId;

    private String paymentPortal;

    public IPGResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
