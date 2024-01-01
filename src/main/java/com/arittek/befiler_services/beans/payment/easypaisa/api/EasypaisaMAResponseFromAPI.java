package com.arittek.befiler_services.beans.payment.easypaisa.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EasypaisaMAResponseFromAPI {

    private String responseCode;
    private String responseDesc;
    private String orderId;
    private String storeId;
    private String transactionId;
    private String transactionDateTime;
}
