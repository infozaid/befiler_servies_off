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
public class DetailedUserBean implements Serializable {
    private Integer userId;
    private Integer userDetailId;
    private Integer authorizerId;

    private String firstName;
    private String lastName;
    private String cnic;
    private String email;
    private String mobileNo;
    private String address;
    private String status;

    private Integer roleId;
    private String roleName;
    private String createdDate;
}
