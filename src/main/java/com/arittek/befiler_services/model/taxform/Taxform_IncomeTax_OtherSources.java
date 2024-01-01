package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_income_tax_other_sources")
public class Taxform_IncomeTax_OtherSources extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "bonus_shares")
    private Double bonusShares;

    @Column(name = "bonus_shares_tax_deducted")
    private Double bonusSharesTaxDeducted;

    @Column(name = "dividend_by_power_companies")
    private Double dividentByPowerCompanies;

    @Column(name = "dividend_by_power_companies_tax_deducted")
    private Double dividentByPowerCompaniesTaxDeducted;

    @Column(name = "dividend_by_other_companies_stock_funds")
    private Double dividentByOtherCompaniesStockFund;

    @Column(name = "dividend_by_other_companies_stock_funds_tax_deducted")
    private Double dividentByOtherCompaniesStockFundTaxDeducted;

    @Column(name = "dividend_by_mutual_funds")
    private Double dividentByMutualFunds;

    @Column(name = "dividend_by_mutual_funds_tax_deducted")
    private Double dividentByMutualFundsTaxDeducted;

    @Column(name = "agricultural_income")
    private Double agriculturalIncome;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;
}
