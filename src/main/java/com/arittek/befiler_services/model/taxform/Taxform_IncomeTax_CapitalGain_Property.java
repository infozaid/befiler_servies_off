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
@Table(name = "taxform_income_tax_capital_gain_property")
public class Taxform_IncomeTax_CapitalGain_Property extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "holding_period_before_1_july_and_upto_3_years_purchase_cost")
    private Double before1JulyAndUpto3YearsPurchaseCost;
    @Column(name = "holding_period_before_1_july_and_upto_3_years_sale_cost")
    private Double before1JulyAndUpto3YearsSaleCost;
    @Column(name = "holding_period_before_1_july_and_upto_3_years_location")
    private String before1JulyAndUpto3YearsLocation;

    @Column(name = "holding_period_before_1_july_and_more_than_3_years_purchase_cost")
    private Double before1JulyAndMoreThan3YearsPurchaseCost;
    @Column(name = "holding_period_before_1_july_and_more_than_3_years_sale_cost")
    private Double before1JulyAndMoreThan3YearsSaleCost;
    @Column(name = "holding_period_before_1_july_and_more_than_3_years_location")
    private String before1JulyAndMoreThan3YearsLocation;

    /*FOR TAX YEAR 2017*/
    @Column(name = "holding_period_after_1_july_purchase_cost")
    private Double after1JulyPurchaseCost;
    @Column(name = "holding_period_after_1_july_sale_cost")
    private Double after1JulySaleCost;
    @Column(name = "holding_period_after_1_july_location")
    private String after1JulyLocation;

    /*FOR TAX YEAR 2018-2019*/
    @Column(name = "holding_period_after_1_july_upto_1_year_purchase_cost")
    private Double after1JulyUpto1YearPurchaseCost;
    @Column(name = "holding_period_after_1_july_upto_1_year_sale_cost")
    private Double after1JulyUpto1YearSaleCost;
    @Column(name = "holding_period_after_1_july_upto_1_year_location")
    private String after1JulyUpto1YearLocation;

    @Column(name = "holding_period_after_1_july_1_to_2_years_purchase_cost")
    private Double after1July1To2YearsPurchaseCost;
    @Column(name = "holding_period_after_1_july_1_to_2_years_sale_cost")
    private Double after1July1To2YearsSaleCost;
    @Column(name = "holding_period_after_1_july_1_to_2_years_location")
    private String after1July1To2YearsLocation;

    @Column(name = "holding_period_after_1_july_2_to_3_years_purchase_cost")
    private Double after1July2To3YearsPurchaseCost;
    @Column(name = "holding_period_after_1_july_2_to_3_years_sale_cost")
    private Double after1July2To3YearsSaleCost;
    @Column(name = "holding_period_after_1_july_2_to_3_years_location")
    private String after1July2To3YearsLocation;

    @Column(name = "holding_period_after_1_july_and_more_than_3_years_purchase_cost")
    private Double after1JulyAndMoreThan3YearsPurchaseCost;
    @Column(name = "holding_period_after_1_july_and_more_than_3_years_sale_cost")
    private Double after1JulyAndMoreThan3YearsSaleCost;
    @Column(name = "holding_period_after_1_july_and_more_than_3_years_location")
    private String after1JulyAndMoreThan3YearsLocation;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;
}
