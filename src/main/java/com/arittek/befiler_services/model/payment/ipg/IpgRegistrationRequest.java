package com.arittek.befiler_services.model.payment.ipg;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
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
@Table( name = "ipg_registration_request")
public class IpgRegistrationRequest extends GenericModelAudit {

    @Id
    @Column(name="payment_customer_info_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="paymentCustomerInfo"))
    private Integer paymentCustomerInfoId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private PaymentCustomerInfo paymentCustomerInfo;

    private Integer ipgStatus;

    /*ONE TO ONE RELATIONS*/
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "ipgRegistrationRequest")
    private IpgRegistrationResponse ipgRegistrationResponse;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "ipgRegistrationRequest")
    private IpgFinalizationRequest ipgFinalizationRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "ipgRegistrationRequest")
    private IpgFinalizationResponse ipgFinalizationResponse;

    /*===============PAYMENT===============*/

    /*IPG REQUEST PARAMETERS*/
    @Column(name = "customer")
    private String customer;

    @Column(name = "currency")
    private String currency;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "language")
    private String language;

    @Column(name = "ipg_version")
    private Double ipgVersion;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "order_info")
    private String orderInfo;

    @Column(name = "return_path")
    private String returnPath;

    @Column(name = "transaction_hint")
    private String transactionHint;
}
