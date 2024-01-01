package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FbrLoginBean implements Serializable {
    private Integer id;
    private Integer userId;
    private String cnic;
    private Integer prefix;
    private String firstName;
    private String lastName;
    private String serviceProvider;
    private String cellNo;
    private String email;
    private String captcha;
    private String cellNoConfig;
    private String emailConfig;
    private String smsCode;
    private String emailCode;

    private Boolean status;
}
