package com.arittek.befiler_services.beans.taxform;

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
public class Taxform_PersonalInformation_Bean implements Serializable {
    private String cnic;
    private String nameAsPerCnic;
    private String email;
    private String mobileNo;
    private String occupation;
    private String dateOfBirth;
    private String residenceAddress;

}