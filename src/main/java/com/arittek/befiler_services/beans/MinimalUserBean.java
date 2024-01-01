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
public class MinimalUserBean implements Serializable {
    private Integer status;
    private String message;
    private String token;
    private Integer userId;
    private Integer userTypeId;
    private String cnic;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private Integer experienceLevel;

}
