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
@Table(name = "taxform_welth_statement_other_receivables_or_assets")
public class Taxform_WelthStatement_OtherReceivablesOrAssets extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "form")
    private String form;

    @Column(name = "institution_name_or_individual_cnic")
    private String institutionNameOrIndividualCnic;

    @Column(name = "value_at_cost")
    private Double valueAtCost;

}
