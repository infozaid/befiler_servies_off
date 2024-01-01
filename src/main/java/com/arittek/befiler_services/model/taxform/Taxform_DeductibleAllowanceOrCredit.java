package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_deductible_allowance_and_credits")
public class Taxform_DeductibleAllowanceOrCredit extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;


    /*DONATION START*/
    @Column(name = "donations_under_section_61_check")
    private Boolean donationsUnderSection61Check;

    @Column(name = "donations_under_section_61")
    private Double donationsUnderSection61;

    @Column(name = "donations_under_clause_61_check")
    private Boolean donationsUnderClause61Check;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donations_under_clause_61_approved_donee_id")
    private ApprovedDonee donationsUnderClause61ApprovedDonee;

    @Column(name = "donations_under_clause_61")
    private Double donationsUnderClause61;

    /*DONATION END*/

    @Column(name = "investment_in_shares_mutual_funds_life_insurance")
    private Double investmentInSharesMutualFundsAndLifeInsurance;

    @Column(name = "investment_in_approved_pension_fund")
    private Double investmentInApprovedPensionFund;

    @Column(name = "interest_or_rate_on_house_holds")
    private Double interestOrRateOnHouseHolds;

    @Column(name = "health_insurance_premium")
    private Double helthInsurancePremium;

    @Column(name = "education_allowance_tution_fees")
    private Double educationAllowanceTutionFees;

    @Column(name = "education_allownace_no_of_childrens")
    private Integer educationAllowanceNoOfChildrens;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;

}
