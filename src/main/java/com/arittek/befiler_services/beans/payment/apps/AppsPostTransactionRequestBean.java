package com.arittek.befiler_services.beans.payment.apps;

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
public class AppsPostTransactionRequestBean implements Serializable {

    private Integer code;
    private String message;

    private String redirectUrl;
    private String merchantId;
    private String merchantName;
    private String token;
    private String proCode;
    private Double txnamt;
    private String customerMobileNumber;
    private String customerEmailAddress;
    private String signature;
    private String version;
    private String txndesc;
    private String successUrl;
    private String failureUrl;
    private String basketId;
    private String orderDate;
    private String checkoutUrl;

    public AppsPostTransactionRequestBean(Integer code, String message) {
	this.code = code;
	this.message = message;
    }
}
