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
public class FinjaPaymentToMerchantResponseBean {
    private Integer code;
    private String msg;

    private FinjaPaymentToMerchantResponseBeanData data;
}

