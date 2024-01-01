package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserExperienceLevelBean {

    private Integer code;
    private String message;

    private Integer experienceLevel;

    public UserExperienceLevelBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
