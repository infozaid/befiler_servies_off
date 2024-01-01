package com.arittek.befiler_services.beans.payment.keenu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class KeenuRequestBean {

    private Integer code;
    private String message;

    private String url;

    private String merchantId;
    private String orderNo;
    private String orderAmount;
    private String date;
    private String time;
    private String checksum;
    private String transactionDesc;

    public KeenuRequestBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
