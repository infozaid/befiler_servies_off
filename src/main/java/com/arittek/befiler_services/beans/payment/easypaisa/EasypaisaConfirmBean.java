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
public class EasypaisaConfirmBean {

    private Integer code;
    private String message;

    private Integer userId;

    /*private String status;
    private String description;
    private String orderRefNum;*/

    private String success;
    private String batchNumber;
    private String authorizeId;
    private String transactionNumber;
    private String amount;
    private String orderRefNumber;
    private String transactionResponse;

    public EasypaisaConfirmBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
