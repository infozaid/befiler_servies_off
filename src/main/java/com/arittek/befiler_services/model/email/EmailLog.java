package com.arittek.befiler_services.model.email;

import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "email_logs")
public class EmailLog extends GenericModel {

    private String emailAddress;
    private Integer emailStatus;
    private String emailSubject;
    private String emailBody;
    private String filesPath;

    public EmailLog(String emailAddress, String emailSubject, String emailBody) {
        this.emailAddress = emailAddress;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
    }

    public EmailLog(String emailAddress, Integer emailStatus, String emailSubject, String emailBody) {
        this.emailAddress = emailAddress;
        this.emailStatus = emailStatus;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
    }

    public EmailLog(String emailAddress, String emailSubject, String emailBody, String filesPath) {
        this.emailAddress = emailAddress;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.filesPath = filesPath;
    }
}
