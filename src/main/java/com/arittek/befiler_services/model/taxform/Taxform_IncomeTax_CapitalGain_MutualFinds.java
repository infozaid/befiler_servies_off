package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_income_tax_capital_gain_mutual_funds")
public class Taxform_IncomeTax_CapitalGain_MutualFinds extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "0_12_months_fields_capital_gain")
    private Double lessThan12MonthsFieldsCapitalGain;

    @Column(name = "0_12_months_tax_deducted")
    private Double lessThan12MonthsTaxDeducted;

    @Column(name = "12_24_months_fields_capital_gain")
    private Double moreThan12ButLessThan24MonthsFieldsCapitalGain;

    @Column(name = "12_24_months_tax_deducted")
    private Double moreThan12ButLessThan24MonthsTaxDeducted;

    @Column(name = "after_1_july_fields_capital_gain")
    private Double moreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain;

    @Column(name = "after_1_july_tax_deducted")
    private Double moreThan24MonthsButAquiredAfter1July2012TaxDeducted;

    @Column(name = "before_1_july_fields_capital_gain")
    private Double aquiredBefore1July2012FieldsCapitalGain;

    @Column(name = "before_1_july_tax_deducted")
    private Double aquiredBefore1July2012TaxDeducted;


    /*FOR TAX YEAR 2018*/
    @Column(name = "on_or_after_1_july_capital_gain")
    private Double acquiredOnOrAfter1JulyCapitalGain;
    @Column(name = "on_or_after_1_july_tax_deducted")
    private Double acquiredOnOrAfter1JulyTaxDeducted;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;
}
