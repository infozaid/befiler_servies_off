package com.arittek.befiler_services.model.payment.keenu;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.user.User;
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
@Table( name = "keenu_request")
public class KeenuRequest extends GenericModelAudit {
    @Id
    @Column(name="payment_customer_info_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="paymentCustomerInfo"))
    private Integer paymentCustomerInfoId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private PaymentCustomerInfo paymentCustomerInfo;

    private String keenuUrl;
    private String merchantId;
    private String secretKey;

    private String orderNo;
    private String orderAmount;

    private String checksum;

    private String date;
    private String time;

    private String currency;

    private String transactionDescription;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "keenuRequest")
    private KeenuResponse keenuResponse;
}
