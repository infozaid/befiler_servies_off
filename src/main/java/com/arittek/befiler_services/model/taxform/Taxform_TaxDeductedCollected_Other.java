package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_tax_deducted_collected_other")
public class Taxform_TaxDeductedCollected_Other extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "property_purchase_tax_deducted")
    private Double propertyPurchaseTaxDeducted;

    @Column(name = "property_sale_tax_deducted")
    private Double propertySaleTaxDeducted;

    @Column(name = "education_fee_tax_deducted")
    private Double educationFeeTaxDeductedTaxDeducted;

    @Column(name = "air_ticket_in_year_tax_deducted")
    private Double airTicketInYearTaxDeducted;

    @Column(name = "withdrawal_from_pension_fund")
    private Double withdrawalFromPensionFund;

    @Column(name = "withdrawal_from_pension_fund_tax_deducted")
    private Double withdrawalFromPensionFundTaxDeducted;

    @Column(name = "functions_gatherings")
    private Double functionsAndGatherings;

    @Column(name = "tax_refund_of_prior_year")
    private Double taxRefundOfPriorYear;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;
}
