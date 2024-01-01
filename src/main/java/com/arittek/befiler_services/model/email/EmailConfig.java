package com.arittek.befiler_services.model.email;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
/*@Audited*/
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "email_config")
public class EmailConfig extends GenericModel{

    @Column(name = "user_form")
    private String userForm;

    @Column(name = "user_form_password")
    private String userFormPassword;

    @Column(name = "user_form_url")
    private String userFormUrl;

    @Column(name = "user_form_phone_no")
    private String userFormPhoneNo;

    @Column(name = "server_ip")
    private String serverIp;

    @Column(name = "server_port")
    private String serverPort;

    @Column(name = "curr_date")
    private Timestamp currDate;

    @Column(name = "value_date")
    private Date valueDate;

    @Column(name = "status")
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
