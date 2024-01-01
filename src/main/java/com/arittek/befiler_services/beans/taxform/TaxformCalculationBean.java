package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformCalculationBean implements Serializable {
    private Integer code;
    private String message;

    /*for lawyer taxform view screen*/
    private Double basicSalary;
    private Double taxOnTax;
    private Double vehicleAllowance;

    private String taxableIncome;
    private String withholdingTax;
    private String taxCharge;

    private String taxRefundableOrPayable;

    private String taxCredit;
    private String donationsTaxCredit;
    private String investementsTaxCredit;
    private String pensionFundsTaxCredit;
    private String healthInsuranceTaxCredit;

    private String pensionFundWithdrawal;
    private String deductableAllowances;

    private String salaryTaxableIncome;
    private String salaryWithholdingTax;
    private String salaryTaxCharge;

    private String propertyTaxableIncome;
    private String propertyWithholdingTax;
    private String propertyTaxCharge;

    private String onShareTaxableIncome;
    private String onShareWithholdingTax;
    private String onShareTaxCharge;

    private String mutualFundsTaxableIncome;
    private String mutualFundsWithholdingTax;
    private String mutualFundsTaxCharge;

    private String capitalGainPropertyTaxableIncome;
    private String capitalGainPropertyWithholdingTax;
    private String capitalGainPropertyTaxCharge;

    private String agriculturalIncomeTaxableIncome;

    private String otherInflowsTaxableIncome;

    private String bonusSharesTaxableIncome;
    private String bonusSharesWithholdingTax;
    private String bonusSharesTaxCharge;

    private String dividendsTaxableIncome;
    private String dividendsWithholdingTax;
    private String dividendsTaxCharge;

    private String profitOnBankDepositTaxableIncome;
    private String profitOnBankDepositWithholdingTax;
    private String profitOnBankDepositTaxCharge;

    private String deductibleAllowanceOrCreditTaxableIncome;
    private String deductibleAllowanceOrCreditWithholdingTax;
    private String deductibleAllowanceOrCreditTaxCharge;

    private String bankingTransactionTaxableIncome;
    private String bankingTransactionWithholdingTax;
    private String bankingTranxactionTaxCharge;

    private String withholdTaxVehicleTaxableIncome;
    private String withholdTaxVehicleWithholdingTax;
    private String withholdTaxVehicleTaxCharge;

    private String utilitiesTaxableIncome;
    private String utilitiesWithholdingTax;
    private String utilitiesTaxCharge;

    private String propertyPurchaseTaxableIncome;
    private String propertyPurchaseWithholdingTax;
    private String propertyPurchaseTaxCharge;

    private String propertySaleTaxableIncome;
    private String propertySaleWithholdingTax;
    private String propertySaleTaxCharge;

    private String propertyPurchaseSaleWithholdingTax;

    private String educationFeesTaxableIncome;
    private String educationFeesWithholdingTax;
    private String educationFeesTaxCharge;

    private String airTicketInYearTaxableIncome;
    private String airTicketInYearWithholdingTax;
    private String airTicketInYearTaxCharge;

    private String withdrawalFromPensionFundsTaxableIncome;
    private String withdrawalFromPensionFundsWithholdingTax;
    private String withdrawalFromPensionFundsTaxCharge;

    private String functionsAndGatheringsTaxableIncome;
    private String functionsAndGatheringsWithholdingTax;
    private String functionsAndGatheringsTaxCharge;

    private String taxRefundFromPriorYearsTaxableIncome;
    private String taxRefundFromPriorYearsWithholdingTax;
    private String taxRefundFromPriorYearsTaxCharge;

    private String wealthStatementOpeningWealth;
    private String wealthStatementTaxableIncome;
    private String wealthStatementExpenses;
    private String wealthStatementWithhodingTax;
    private String wealthStatementClosingWealth;

    private String wealthStatement;
    private String wealthStatementPropertyDetail;
    private String wealthStatementBankAccountOrInvestment;
    private String wealthStatementOtherReceivablesOrAssets;
    private String wealthStatementOwnVehicle;
    private String wealthStatementOtherPossessions;
    private String wealthStatementAssetsOutsidePakistan;
    private String wealthStatementOweAnyLoanOrCredit;
    private String wealthStatementDifference;

    private Boolean investmentsInSharesShowCheck;
    private Boolean pensionFundsShowCheck;
    private Boolean healthInsurancePremiumShowCheck;
    private Boolean educationAllowancesShowCheck;

    /*For Lawyer Screen*/

    private Double taxableIncomeForCalculation;
    private Double taxRateForCalculation;

    public TaxformCalculationBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
