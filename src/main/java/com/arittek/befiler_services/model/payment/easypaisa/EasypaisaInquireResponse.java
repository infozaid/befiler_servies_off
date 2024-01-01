package com.arittek.befiler_services.model.payment.easypaisa;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
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
@Table( name = "easypaisa_inquire_response")
public class EasypaisaInquireResponse extends GenericModelAudit {

    @Id
    @Column(name="easypaisa_inquire_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="easypaisaInquireRequest"))
    private Integer easypaisaInquireRequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private EasypaisaInquireRequest easypaisaInquireRequest;

    private String responseCode;
    private String responseMessage;

    private String orderId;
    private String accountNum;
    private String storeId;
    private String paymentToken;
    private String transactionStatus;
    private String transactionAmount;
    private String paymentTokenExpiryDateTime;
    private String msisdn;
    private String paymentMethod;
}
