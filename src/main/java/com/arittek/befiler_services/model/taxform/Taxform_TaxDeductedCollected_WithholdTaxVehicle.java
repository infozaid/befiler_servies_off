package com.arittek.befiler_services.model.taxform;

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
@Table(name = "taxform_tax_deducted_collected_withhold_tax_vehicle")
public class Taxform_TaxDeductedCollected_WithholdTaxVehicle extends GenericModel{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "type")
    private String type;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_registration_no")
    private String vehicleRegistrationNo;

    @Column(name = "tax_deducted")
    private Double taxDeducted;
}
