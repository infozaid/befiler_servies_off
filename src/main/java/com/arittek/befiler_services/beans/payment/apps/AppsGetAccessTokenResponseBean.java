package com.arittek.befiler_services.beans.payment.apps;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AppsGetAccessTokenResponseBean {

    public String errorCode;
    public String errorDescription;

    public String MERCHANT_ID;
    public String ACCESS_TOKEN;
    public String NAME;
    public String GENERATED_DATE_TIME;
}
