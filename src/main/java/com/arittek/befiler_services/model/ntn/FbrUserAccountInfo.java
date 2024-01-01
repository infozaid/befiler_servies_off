package com.arittek.befiler_services.model.ntn;

import com.arittek.befiler_services.config.converters.StringCryptoConverter;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "fbr_user_account_info")
public class FbrUserAccountInfo extends GenericModel {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = FbrUserAccountInfoStatusConverter.class)
    @Column(name = "fbr_user_account_info_status")
    private FbrUserAccountInfoStatus fbrUserAccountInfoStatus;

    /*@Column(name = "registered_with_fbr")
    private Integer registeredWithFbr;*/

    @Column(name = "fbr_ntn_number")
    private String fbrNtnNumber;

    @Column(name = "fbr_username")
    private String fbrUsername;

    @Column(name = "fbr_password")
    @Convert(converter = StringCryptoConverter.class)
    private String fbrPassword;

    @Column(name = "fbr_pin")
    @Convert(converter = StringCryptoConverter.class)
    private String fbrPin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fbrUserAccountInfo")
    private List<Payment> paymentList = new ArrayList<>();

}
