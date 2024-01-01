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
@Table( name = "easypaisa_ma_response")
public class EasypaisaMAResponse extends GenericModelAudit {

    @Id
    @Column(name="easypaisa_ma_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="easypaisaMARequest"))
    private Integer easypaisaMARequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private EasypaisaMARequest easypaisaMARequest;

    private String responseCode;
    private String responseDesc;
    private String orderId;
    private String storeId;
    private String transactionId;
    private String transactionDateTime;
}
