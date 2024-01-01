package com.arittek.befiler_services.beans.finja_external_registration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FinjaExternalRegistrationResponseBean {
    
    private Integer code;
    private String message;
    private Integer finjaStep1RequestId;
    private Integer responseCode;
    private String responseMessage;

    private List<FinjaRegionBean> finjaRegionBeanList = new ArrayList<>();

    public FinjaExternalRegistrationResponseBean(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public FinjaExternalRegistrationResponseBean(Integer code,Integer finjaStep1RequestId ,String message) {
        this.finjaStep1RequestId = finjaStep1RequestId;
        this.code = code;
        this.message = message;
    }
}
