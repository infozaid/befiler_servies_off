package com.arittek.befiler_services.beans.finja_external_registration;


import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep2Request;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class FinjaExternalRegistrationStep2RequestBean {

    private Integer userId;
    private String appKey;
    private Integer appId;
    private Integer applicationVersion;
    private Integer merchantId;
    private String mobileNo;
    private String cnic;
    private Integer step;
    private Integer queueId;
    private String token;
    private Integer otp;
    private String fatherName;
    private String motherName;
    private String displayName;
    private String email;
    private String firstName;
    private String lastName;
    private Integer isBirthPlace;
    private Integer birthPlace;
    private String mailingAddress;
    private String operator;

    public FinjaExternalRegistrationStep2RequestBean(FinjaExternalRegistrationStep2Request finjaExternalRegistrationStep2Request) {
        this.userId = finjaExternalRegistrationStep2Request.getCreatedBy();
        this.appKey = finjaExternalRegistrationStep2Request.getAppKey();
        this.appId = finjaExternalRegistrationStep2Request.getAppId();
        this.applicationVersion = finjaExternalRegistrationStep2Request.getApplicationVersion();
        this.merchantId = finjaExternalRegistrationStep2Request.getMerchantId();
        this.mobileNo = finjaExternalRegistrationStep2Request.getMobileNo();
        this.cnic = finjaExternalRegistrationStep2Request.getCnic();
        this.step = finjaExternalRegistrationStep2Request.getStep();
        this.queueId = finjaExternalRegistrationStep2Request.getQueueId();
        this.token = finjaExternalRegistrationStep2Request.getToken();
        this.otp = finjaExternalRegistrationStep2Request.getOtp();
        this.fatherName = finjaExternalRegistrationStep2Request.getFatherName();
        this.motherName = finjaExternalRegistrationStep2Request.getMotherName();
        this.displayName = finjaExternalRegistrationStep2Request.getDisplayName();
        this.email = finjaExternalRegistrationStep2Request.getEmail();
        this.firstName = finjaExternalRegistrationStep2Request.getFirstName();
        this.lastName = finjaExternalRegistrationStep2Request.getLastName();
        this.isBirthPlace = finjaExternalRegistrationStep2Request.getIsBirthPlace();
        this.birthPlace = finjaExternalRegistrationStep2Request.getBirthPlace();
        this.mailingAddress = finjaExternalRegistrationStep2Request.getMailingAddress();
        this.operator = finjaExternalRegistrationStep2Request.getOperator();

}

}
