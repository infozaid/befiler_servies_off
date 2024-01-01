package com.arittek.befiler_services.model.payment.ipg;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ipg_finalization_request")
public class IpgFinalizationRequest extends GenericModelAudit {

    @Id
    @Column(name="ipg_registration_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="ipgRegistrationRequest"))
    private Integer ipgRegistrationRequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private IpgRegistrationRequest ipgRegistrationRequest;

    @Column(name = "customer")
    private String customer;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "language")
    private String language;

    @Column(name = "ipg_version")
    private Double ipgVersion;

}
