package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.model.email.EmailLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EmailLogBean {

    private Integer id;

    private String emailAddress;
    private String emailSubject;
    private String emailBody;
    private Integer emailStatus;

    public EmailLogBean(EmailLog emailLog) {
        this.id = emailLog.getId();
        this.emailAddress = emailLog.getEmailAddress();
        this.emailSubject = emailLog.getEmailSubject();
        this.emailBody = emailLog.getEmailBody();
        this.emailStatus = emailLog.getEmailStatus();
    }
}
