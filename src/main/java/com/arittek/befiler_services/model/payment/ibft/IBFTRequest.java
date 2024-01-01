package com.arittek.befiler_services.model.payment.ibft;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.enums.UserStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ibft_request")
public class IBFTRequest extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "status")
    private AppStatus status;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_number")
    private String mobileNo;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "residential_address")
    private String residentialAddress;

    @Column(name = "billing_address")
    private String billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ibftRequest")
    private List<IBFTRequestDocument> ibftRequestDocumentList = new ArrayList<>();


}
