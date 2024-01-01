package com.arittek.befiler_services.beans.finja_external_registration;

import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep1Request;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FinjaExternalRegistrationStep1RequestBean {

    private Long applicationVersion;
    private Long merchantId;
    private Long mobileNo;
    private Long cnic;
    private Long step;

    public FinjaExternalRegistrationStep1RequestBean(FinjaExternalRegistrationStep1Request finjaExternalRegistrationStep1Request) {
        this.applicationVersion = Long.parseLong(finjaExternalRegistrationStep1Request.getApplicationVersion());
        this.merchantId = Long.parseLong(finjaExternalRegistrationStep1Request.getMerchantId());
        this.mobileNo = Long.parseLong(finjaExternalRegistrationStep1Request.getMobileNo());
        this.cnic = Long.parseLong(finjaExternalRegistrationStep1Request.getCnic());
        this.step = Long.parseLong(finjaExternalRegistrationStep1Request.getStep());
    }
}
