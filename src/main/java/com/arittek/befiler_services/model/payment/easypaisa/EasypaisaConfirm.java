package com.arittek.befiler_services.model.payment.easypaisa;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
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
@Table( name = "easypaisa_confirm")
public class EasypaisaConfirm extends GenericModelAudit {
    @Id
    @Column(name="easypaisa_index_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="easypaisaIndex"))
    private Integer easypaisaIndexId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private EasypaisaIndex easypaisaIndex;

    /*private String status;
    private String desc;
    private String orderRefNum;*/

    private String success;
    private String batchNumber;
    private String authorizeId;
    private String transactionNumber;
    private String amount;
    private String orderRefNumber;
    private String transactionResponse;
}
