package com.arittek.befiler_services.model.finja_external_registration;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "finja_external_registration_step2_request")
public class FinjaExternalRegistrationStep2Request extends GenericModelAudit {
    @Id
    @Column(name="finja_external_registration_step1_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="finjaExternalRegistrationStep1Request"))
    private Integer finjaExternalRegistrationStep1RequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private FinjaExternalRegistrationStep1Request finjaExternalRegistrationStep1Request;

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

}
