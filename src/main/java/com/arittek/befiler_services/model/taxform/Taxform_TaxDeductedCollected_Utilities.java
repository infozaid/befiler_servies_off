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
@Table(name = "taxform_tax_deducted_collected_utilities")
public class Taxform_TaxDeductedCollected_Utilities extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "utility_type")
    private String utilityType;

    @Column(name = "provider")
    private String provider;

    @Column(name = "tax_deducted")
    private Double taxDeducted;

    @Column(name = "reference_or_consumer_no")
    private String referenceOrConsumerNo;

}
