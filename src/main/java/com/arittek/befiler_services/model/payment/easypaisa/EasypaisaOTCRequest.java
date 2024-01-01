package com.arittek.befiler_services.model.payment.easypaisa;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table( name = "easypaisa_otc_request")
public class EasypaisaOTCRequest extends GenericModelAudit {

    @Id
    @Column(name="payment_customer_info_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="paymentCustomerInfo"))
    private Integer paymentCustomerInfoId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private PaymentCustomerInfo paymentCustomerInfo;

    private String credentials;
    private String orderId;
    private String storeId;
    private String transactionAmount;
    private String transactionType;
    private String msisdn;
    private String emailAddress;
    private String tokenExpiry;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "easypaisaOTCRequest")
    private EasypaisaOTCResponse easypaisaOTCResponse;
}
