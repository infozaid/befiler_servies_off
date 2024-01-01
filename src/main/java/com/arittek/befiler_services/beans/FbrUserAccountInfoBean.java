package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FbrUserAccountInfoBean implements Serializable {
    private Integer id;
    private Boolean registredWithFbr;
    private Integer taxformId;
    private String fbrUsername;
    private String fbrPassword;
    private String fbrPin;
    private String fbrNtnNumber;
    private Boolean status;
    private Timestamp currentDate;

    private String NtnStatus;

    private Integer assignId;

    private Integer userId;
    private String userEmail;
    private String userName;
    private String userCnic;
    private String userMobileNo;

    List<FbrUserAccountInfoDocumentsBean> virusScanList;
    List<FbrUserAccountInfoDocumentsBean> proofOfResidence;
    FbrUserAccountInfoDocumentsBean utilityBillOfResidence;
    List<FbrUserAccountInfoDocumentsBean> cnicList;

}
