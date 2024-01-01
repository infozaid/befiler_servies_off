package com.arittek.befiler_services.beans.finja_external_registration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FinjaExternalRegistrationStep1ResponseBean {

    private Integer code;
    private String msg;
    private Data data;
    private Integer logType;
    private Integer refId;


    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    @NoArgsConstructor
    public class Data {
        private Integer error_code;
        private String token;
        private Integer queueId;
    }

}
