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
public class CorporateBean implements Serializable {
    private Integer id;
    private Integer status;
    private String corporateInfo;
    private String corporateLogo;
    private String corporateName;
    private String ntnCnic;
    private String corporateLandLine;
    private String corporateContact;
    private String webAddress;
    private String corporateAddress;
    private String benificiaryName;
    private String personContact;
    private String personLandLine;
    private String designation;
    private String emailAddress;
    private Timestamp currentDate;
    private Integer userId;
    private Integer corporateCategoryId;
    private Double totalAmount;
}
