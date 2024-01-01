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
@Table(name = "finja_external_registration_step1_response")
public class FinjaExternalRegistrationStep1Response extends GenericModelAudit {

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
    private String token;
    private Integer queueId;
    private Integer logType;
    private Integer refId;
    private Integer errorCode;

    public   FinjaExternalRegistrationStep1Response(FinjaExternalRegistrationStep1ResponseBean finjaExternalRegistrationStep1ResponseBean) {
        this.setCode(finjaExternalRegistrationStep1ResponseBean.getCode());
        this.setMsg(finjaExternalRegistrationStep1ResponseBean.getMsg());
        this.setLogType(finjaExternalRegistrationStep1ResponseBean.getLogType());
        this.setRefId(finjaExternalRegistrationStep1ResponseBean.getRefId());
        if (finjaExternalRegistrationStep1ResponseBean.getCode() == 200) {
            this.setToken(finjaExternalRegistrationStep1ResponseBean.getData().getToken());
            this.setQueueId(finjaExternalRegistrationStep1ResponseBean.getData().getQueueId());
        } else {
            // TODO NULL POINTER EXCEPTION
            /*this.setErrorCode(finjaExternalRegistrationStep1ResponseBean.getData().getError_code());*/
        }
    }
}
