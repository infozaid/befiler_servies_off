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
public class UserPersonalInformationBean implements Serializable {
    private String userId;
    private String cnic;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String occupation;
    private String dateOfBirth;
    private String residenceAddress;
    private String residenceStatus;
    private String creationDate;
}
