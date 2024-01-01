package com.arittek.befiler_services.model.payment.finja;

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
@Table(name = "finja_get_customer_info_request")
public class FinjaGetCustomerInfoRequest extends GenericModelAudit {

    @Id
    @Column(name="payment_customer_info_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="paymentCustomerInfo"))
    private Integer paymentCustomerInfoId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private PaymentCustomerInfo paymentCustomerInfo;

    private Integer finjaStatus;

    private String deviceId;

    private String device;

    private String mobileNo;

    private String orderId;

    private String customerIdMerchant;

    /*ONE TO ONE RELATIONS*/
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaGetCustomerInfoRequest")
    private FinjaGetCustomerInfoResponse finjaGetCustomerInfoResponse;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaGetCustomerInfoRequest")
    private FinjaPaymentToMerchantRequest finjaPaymentToMerchantRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "finjaGetCustomerInfoRequest")
    private FinjaPaymentToMerchantResponse finjaPaymentToMerchantResponse;

}
