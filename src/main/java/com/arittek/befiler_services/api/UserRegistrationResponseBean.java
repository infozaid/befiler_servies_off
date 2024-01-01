package com.arittek.befiler_services.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserRegistrationResponseBean {

    private Integer code;
    private String message;

    public UserRegistrationResponseBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
