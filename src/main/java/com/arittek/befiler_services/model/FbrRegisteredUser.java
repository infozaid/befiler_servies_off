package com.arittek.befiler_services.model;

import com.arittek.befiler_services.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "fbr_registerd_user")
public class FbrRegisteredUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cnic")
    private String cnic;

    @Column(name = "prefix")
    private Integer prefix;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "service_provider")
    private String serviceProvider;

    @Column(name = "cell_no")
    private String cellNo;

    @Column(name = "cell_no_config")
    private String cellNoConfig;

    @Column(name = "email")
    private String email;

    @Column(name = "email_config")
    private String emailConfig;

    @Column(name = "sms_code")
    private String smsCode;

    @Column(name = "email_code")
    private String emailCode;

    @Column(name = "captcha")
    private String captcha;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "curr_date")
    private Timestamp currentDae;
}
