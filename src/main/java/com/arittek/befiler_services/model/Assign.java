package com.arittek.befiler_services.model;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.enums.AssignType;
import com.arittek.befiler_services.model.enums.AssignTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "assign")
public class Assign extends GenericModel {

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;*/

    @Convert(converter = AssignTypeConverter.class)
    private AssignType assignType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id")
    private User lawyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fbr_user_account_info_id")
    private FbrUserAccountInfo fbrUserAccountInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizer_id")
    private User authorizer;*/

    @Convert(converter = AppStatusConverter.class)
    private AppStatus appStatus;

    /*@Column(name = "curr_date")
    private Timestamp currentDate;*/
}

