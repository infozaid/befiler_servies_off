package com.arittek.befiler_services.model.payment.finja;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
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
@Table(name = "finja_payment_to_merchant_request")
public class FinjaPaymentToMerchantRequest extends GenericModelAudit {

    @Id
    @Column(name="finja_get_customer_info_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="finjaGetCustomerInfoRequest"))
    private Integer finjaGetCustomerInfoRequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest;

    private String deviceId;
    private String device;
    private String mobileNo;
    private String customerIdMerchant;
    private String amount;
    private String otp;
    private String customerId;
    private String customerName;

}
