package com.arittek.befiler_services.model.user;

import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.enums.UserStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/*@Data*/
@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
/*@EqualsAndHashCode(callSuper = true)*/
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "um_user")
public class User extends GenericModel {



    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "cnic_no")
    private String cnicNo;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "address")
    private String address;

    @Column(name = "experience_level")
    private Integer experienceLevel;

    @Convert(converter = UserStatusConverter.class)
    @Column(name = "user_status")
    private UserStatus status;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "last_password_reset_date")
    private Timestamp lastPasswordResetDate;

    @Column(name = "login_attempts")
    private Integer loginAttempts;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch=FetchType.EAGER)
    @JoinTable(name = "rp_um_role_user",  joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id") } )
    private Set<Role> roles  = new HashSet<>();

  /*  @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<IBFTRequest> ibftRequests = new HashSet<>();*/
}
