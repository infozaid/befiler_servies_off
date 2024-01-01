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
public class EasypaisaIndexBean {

    private Integer code;
    private String message;

    private String amount;
    private String storeId;
    private String redirectUrl;
    private String postBackUrl;
    private String orderRefNum;
    private String merchantHashedReq;
    private String paymentMethod;

    private String expiryDate;
    private String autoRedirect;
    private String emailAddr;
    private String mobileNum;

    public EasypaisaIndexBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
