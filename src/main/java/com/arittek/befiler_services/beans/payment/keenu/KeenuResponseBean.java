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
public class KeenuResponseBean {

    private Integer code;
    private String message;

    private Integer userId;

    private String status;
    private String authStatusNo;
    private String orderNo;
    private String transactionId;
    private String checkSum;
    private String bankName;
    private String date;
    private String time;

    public KeenuResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
