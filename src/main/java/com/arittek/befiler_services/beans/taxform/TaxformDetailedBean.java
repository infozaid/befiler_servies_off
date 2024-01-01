package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformDetailedBean implements Serializable {

    private Integer taxformId;
    private Integer userId;
    private Integer code;
    private Integer currentScreen;
    private String message;

    private String year;

    private String nationality;
    private String residenceStatus;
    private Boolean stayInPakistanBecauseOfEmployement;
    private Boolean stayInPakistanMoreThan3Years;

    /*==================PERSONAL INFORMATION*/
    private Taxform_PersonalInformation_Bean taxformPersonalInformationBean;

    /*==================INCOME TAX -> SALARY*/
    private Boolean salaryCheck;
    private Taxform_IncomeTax_Salary_Bean taxformIncomeTaxSalaryBean;

    /*==================INCOME TAX -> PROPERTY*/
    private Boolean propertyCheck;
    private Taxform_IncomeTax_Property_Bean taxformIncomeTaxPropertyBean;

    /*==================INCOME TAX -> CAPITAL GAIN -> ON SHARE*/
    private Boolean capitalGainOnShareCheck;
    private Taxform_IncomeTax_CapitalGain_OnShare_Bean taxformIncomeTaxCapitalGainOnShareBean;

    /*==================INCOME TAX -> CAPITAL GAIN -> MUTUAL FUNDS*/
    private Boolean capitalGainMutualFundsCheck;
    private Taxform_IncomeTax_CapitalGain_MutualFunds_Bean taxformIncomeTaxCapitalGainMutualFundsBean;

    /*==================INCOME TAX -> CAPITAL GAIN -> PROPERTY*/
    private Boolean capitalGainPropertyCheck;
    private Taxform_IncomeTax_CapitalGain_Property_Bean taxformIncomeTaxCapitalGainPropertyBean;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> AGRICULTURAL INCOME*/
    private Boolean bonusSharesAgriculturalIncomeCheck;
    private Taxform_IncomeTax_OtherSources_AgriculturalIncome_Bean taxformIncomeTaxOtherSourcesAgriculturalIncomeBean;


    /*==================INCOME TAX -> OTHER SOURCES OF INCOME -> OtherInflows --- MULTIPLE VALUES*/
    private Boolean otherInflowsCheck;
    private Taxform_IncomeTax_OtherSources_OtherInflows_Bean taxformIncomeTaxOtherSourcesOtherInflowsBean;
    private List<TaxformIncomeTaxOtherSourcesOtherInFlowBean> taxformIncomeTaxOtherSourcesOtherInFlowBeanList;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> BONUS SHARES*/
    private Boolean bonusSharesCheck;
    private Taxform_IncomeTax_OtherSources_BonusShares_Bean taxformIncomeTaxOtherSourcesBonusSharesBean;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> DIVIDEND*/
    private Boolean dividentCheck;
    private Taxform_IncomeTax_OtherSources_Dividends_Bean taxformIncomeTaxOtherSourcesDividendsBean;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME -> PROFIT ON BANK DEPOSIT --- MULTIPLE VALUES*/

    private Boolean bankDepositCheck;
    private List<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean> taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans;

    /*==================DEDUCTIBLE ALLOWANCE AND TAX CREDIT*/
    private Taxform_DeductibleAllowanceOrCredit_Bean taxformDeductibleAllowanceOrCreditBean;

    /*==================TAX DEDUCTED OR COLLECTED -> BANKING TRANSACTION --- MULTIPLE VALUES*/
    private Boolean bankingTransactionCheck;
    private List<TaxformTaxDeductedCollectedBankingTransactionBean> taxformTaxDeductedCollectedBankingTransactionBeans;

    /*==================TAX DEDUCTED OR COLLECTED -> WITHHOLD TAX VEHICLE --- MULTIPLE VALUES*/
    private Boolean withholdTaxVehicleCheck;
    private List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> taxformTaxDeductedCollectedWithholdVehicleTaxBeans;

    /*==================TAX DEDUCTED OR COLLECTED -> UTILITIES --- MULTIPLE VALUES*/
    private Boolean utilitiesCheck;
    private List<TaxformTaxDeductedCollectedUtilitiesBean> taxformTaxDeductedCollectedUtilitiesBeans;

    /*==================TAX DEDUCTED OR COLLECTED -> OTHER FIELDS*/
    private Boolean taxDeductedCollectedOtherFieldsCheck;
    private Taxform_TaxDeductedCollected_OtherFields_Bean taxformTaxDeductedCollectedOtherFieldsBean;

    /*==================WELTH STATEMENT -> OPENING WEALTH */
    private String openingWealth;
    private String openingWealthSuggestion;

    /*==================WELTH STATEMENT -> PROPERTY DETAIL */
    private Boolean wealthStatementPropertyCheck;
    private List<TaxformWealthStatementPropertyDetailsBean> taxformWealthStatementPropertyDetailsBeans;

    /*==================WELTH STATEMENT -> BANK ACCOUNTS OR INVESTMENTS */
    private Boolean bankAccountOrInvestmentCheck;
    private List<TaxformWealthStatementBankAccountsOrInvestmentBean> taxformWealthStatementBankAccountsOrInvestmentBeans;

    /*==================WELTH STATEMENT -> OTHER RECEIVABLES OR ASSETS*/
    private Boolean otherReceivableAssets;
    private List<TaxformWealthStatementOtherReceivablesOrAssetsBean> taxformWealthStatementOtherReceivablesOrAssetsBeans;

    /*==================WELTH STATEMENT -> OWN VEHICLE */
    private Boolean ownVehicleCheck;
    private List<TaxformWealthStatementOwnVehicleBean> taxformWealthStatementOwnVehicleBeans;

    /*==================WELTH STATEMENT -> OTHER POSSESSIONS */
    private Boolean otherPossessionsCheck;
    private List<TaxformWealthStatementOtherPossessionsBean> taxformWealthStatementOtherPossessionsBeans;

    /*==================WELTH STATEMENT -> ASSETS OUTSIDE PAKISTAN */
    private Boolean assetsOutsidePakistanCheck;
    private List<TaxformWealthStatementAssetsOutsidePakistanBean> taxformWealthStatementAssetsOutsidePakistanBeans;

    /*==================WELTH STATEMENT -> OWE ANY LOANS OR CREDIT */
    private Boolean oweAnyLoanOrCreditCheck;
    private List<TaxformWealthStatementOweAnyLoanOrCreditBean> taxformWealthStatementOweAnyLoanOrCreditBeans;

    /*==================WELTH STATEMENT -> DETAILS OF PERSONAL EXPENSES */
    private Boolean personalExpensesCheck;
    private Taxform_WealthStatement_PersonalExpenses_Bean taxformWealthStatementPersonalExpensesBean;


    /*CONSTRUCTORS*/

    public TaxformDetailedBean(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}
