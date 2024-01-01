package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
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
@Table(name = "taxform_welth_statement_own_vehicle")
public class Taxform_WelthStatement_OwnVehicle extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "form")
    private String form;

    @Column(name = "etd_registration_no")
    private String etdRegistrationNo;

    @Column(name = "maker")
    private String maker;

    @Column(name = "capacity")
    private String capacity;

    @Column(name = "value_at_cost")
    private Double valueAtCost;

}
