package com.arittek.befiler_services.model.finja_external_registration;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "finja_external_registration_step1_request")
public class FinjaExternalRegistrationStep1Request extends GenericModel {

    private String appKey;
    private String appId;
    private String applicationVersion;
    private String merchantId;
    private String mobileNo;
    private String cnic;
    private String step;

    /*One To One*/

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaExternalRegistrationStep1Request")
    private FinjaExternalRegistrationStep1Response finjaExternalRegistrationStep1Response;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaExternalRegistrationStep1Request")
    private FinjaExternalRegistrationStep2Request finjaExternalRegistrationStep2Request;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaExternalRegistrationStep1Request")
    private FinjaExternalRegistrationStep2Response finjaExternalRegistrationStep2Response;
}
