package com.arittek.befiler_services.model.finja_external_registration;

import com.arittek.befiler_services.beans.finja_external_registration.FinjaExternalRegistrationStep1ResponseBean;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
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
@Table(name = "finja_external_registration_step2_response")
public class FinjaExternalRegistrationStep2Response extends GenericModelAudit {
    @Id
    @Column(name="finja_external_registration_step1_request_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="finjaExternalRegistrationStep1Request"))
    private Integer finjaExternalRegistrationStep1RequestId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private FinjaExternalRegistrationStep1Request finjaExternalRegistrationStep1Request;

    private Integer code;
    private String msg;
    private Integer logType;
    private Integer refId;

    @Column(name = "curr_date")
    private Timestamp currentDate;

    public FinjaExternalRegistrationStep2Response(FinjaExternalRegistrationStep1ResponseBean responseBean){
        this.code = responseBean.getCode();
        this.msg = responseBean.getMsg();
        this.logType = responseBean.getLogType();
        this.refId = responseBean.getRefId();
    }

}
