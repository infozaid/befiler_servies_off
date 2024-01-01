package com.arittek.befiler_services.model.payment.keenu;

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
@Table( name = "keenu_response")
public class KeenuResponse extends GenericModelAudit {
    @Id
    @Column(name="keenu_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="keenuRequest"))
    private Integer keenuRequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private KeenuRequest keenuRequest;

    private String status;
    private String authStatusNo;
    private String orderNo;
    private String transactionId;
    private String checksum;
    private String bankName;
    private String date;
    private String time;

}
