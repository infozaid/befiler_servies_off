package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by HP-PC on 6/7/2018.
 */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    @NoArgsConstructor
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    public class NtnCaptureCodeBean implements Serializable {
        private int code;
        private String message;
        private  String caputreUrl;
        private String captchaUrl;
    }


