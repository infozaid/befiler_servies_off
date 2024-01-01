package com.arittek.befiler_services.model.payment.ipg;

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
@Table( name = "ipg_registration_response")
public class IpgRegistrationResponse extends GenericModelAudit {

    @Id
    @Column(name="ipg_registration_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="ipgRegistrationRequest"))
    private Integer ipgRegistrationRequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private IpgRegistrationRequest ipgRegistrationRequest;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_description")
    private String responseDescription;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "payment_portal")
    private String paymentPortal;
}
