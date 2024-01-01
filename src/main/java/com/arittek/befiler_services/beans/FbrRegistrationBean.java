package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FbrRegistrationBean implements Serializable {
    private Integer id;
    private Integer userId;
    private  String cnic;
    private  Integer prefix;
    private  String firstName;
    private  String lastName;
    private String currentService;
    private String cellNo;
    private String cellNoCong;

    private  String email;
    private  String emailCong;

    private  String captcha;

    private  String smsPin;
    private String emailPin;

    private Boolean status;
    private Timestamp currDate;
}

