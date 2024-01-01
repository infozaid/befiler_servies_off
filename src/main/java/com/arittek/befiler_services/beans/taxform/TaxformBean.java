package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformBean implements Serializable {

    private Integer taxformId;
    private Integer userId;

    private Integer termsAndConditionsId;

    private Integer yearId;
    private String year;

    private Boolean verifyCorporateEmpoyee;
    private String currentScreen;
    private int statusId;
    private Timestamp currentDate;
    private long dateDifference;
     /*====================ipg ==============by nadeem*/

    private Double amount;
    private String transactionId;

    /*==================PERSONAL INFORMATION*/
    private String cnic;
    private String nameAsPerCnic;
    private String email;
    private String mobileNo;
    private String occupation;
    private String dateOfBirth;
    private String country;
    private String nationality;
    private String residenceStatus;
    private String residenceAddress;
    private Boolean stayInPakistanBecauseOfEmployement;
    private Boolean stayInPakistanMoreThan3Years;

    /*==================INCOME TAX -> SALARY*/
    private Boolean salaryCheck;

    private String basicSalary;

    private Boolean tadaCheck;
    private String tada;

    private Boolean medicalAllowanceCheck;
    private String medicalAllowance;

    private Boolean providentFundByEmployeerCheck;
    private String providentFundByEmployeer;

    private Boolean otherAllowanceCheck;
    private String otherAllownace;

    private Boolean companyVehcileProvidedCheck;
    private String companyVehicleCost;

    private Boolean companyVehicleReceivedAfterJulyCheck;
    private String companyVehicleReceivedDate;

    private Boolean providentOrGratuityFundReceivedCheck;
    private String providentOrGratuityFundReceived;

    private Boolean salaryTaxBorneByEmployeerCheck;
    private String salaryTaxWithheldByEmployeer;

    /*==================INCOME TAX -> PROPERTY*/
    private Boolean propertyCheck;
    private String rentReceivedFromYourProperty;
    private Boolean doYouDeductAnyTax;
    private String propertyTax;

    /*==================INCOME TAX -> CAPITAL GAIN*/
    private Boolean capitalGainCheck;

    /*==================INCOME TAX -> CAPITAL GAIN -> ON SHARE*/
    private Boolean capitalGainOnShareCheck;

    private Boolean onShareLessThan12MonthsCheck;
    private String onShareLessThan12MonthsFieldsCapitalGain;
    private String onShareLessThan12MonthsTaxDeducted;

    private Boolean onShareMoreThan12ButLessThan24MonthsCheck;
    private String onShareMoreThan12ButLessThan24MonthsFieldsCapitalGain;
    private String onShareMoreThan12ButLessThan24MonthsTaxDeducted;

    private Boolean onShareMoreThan24MonthsButAquiredAfter1July2012Check;
    private String onShareMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain;
    private String onShareMoreThan24MonthsButAquiredAfter1July2012TaxDeducted;

    private Boolean onShareAquiredBefore1July2012Check;
    private String onShareAquiredBefore1July2012FieldsCapitalGain;
    private String onShareAquiredBefore1July2012TaxDeducted;

    private Boolean onShareAcquiredOnOrAfter1JulyCheck;
    private String onShareAcquiredOnOrAfter1JulyCapitalGain;
    private String onShareAcquiredOnOrAfter1JulyTaxDeducted;

    /*==================INCOME TAX -> CAPITAL GAIN -> MUTUAL FUNDS*/
    private Boolean capitalGainMutualFundsCheck;

    private Boolean mutualFundsLessThan12MonthsCheck;
    private String mutualFundsLessThan12MonthsFieldsCapitalGain;
    private String mutualFundsLessThan12MonthsTaxDeducted;

    private Boolean mutualFundsMoreThan12ButLessThan24MonthsCheck;
    private String mutualFundsMoreThan12ButLessThan24MonthsFieldsCapitalGain;
    private String mutualFundsMoreThan12ButLessThan24MonthsTaxDeducted;

    private Boolean mutualFundsMoreThan24MonthsButAquiredAfter1July2012Check;
    private String mutualFundsMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain;
    private String mutualFundsMoreThan24MonthsButAquiredAfter1July2012TaxDeducted;

    private Boolean mutualFundsAquiredBefore1July2012Check;
    private String mutualFundsAquiredBefore1July2012FieldsCapitalGain;
    private String mutualFundsAquiredBefore1July2012TaxDeducted;

    private Boolean mutualFundsAcquiredOnOrAfter1JulyCheck;
    private String mutualFundsAcquiredOnOrAfter1JulyCapitalGain;
    private String mutualFundsAcquiredOnOrAfter1JulyTaxDeducted;

    /*==================INCOME TAX -> CAPITAL GAIN -> PROPERTY*/
    private Boolean capitalGainPropertyCheck;

    private Boolean propertyBefore1JulyCheck;

    private Boolean propertyBefore1JulyAndUpto3YearsCheck;
    private String propertyBefore1JulyAndUpto3YearsPurchaseCost;
    private String propertyBefore1JulyAndUpto3YearsSaleCost;
    private String propertyBefore1JulyAndUpto3YearsLocation;

    private Boolean propertyBefore1JulyAndMoreThan3YearsCheck;
    private String propertyBefore1JulyAndMoreThan3YearsPurchaseCost;
    private String propertyBefore1JulyAndMoreThan3YearsSaleCost;
    private String propertyBefore1JulyAndMoreThan3YearsLocation;

    private Boolean propertyAfter1JulyCheck;

    /*FOR TAX YEAR 2017*/
    private String propertyAfter1JulyPurchaseCost;
    private String propertyAfter1JulySaleCost;
    private String propertyAfter1JulyLocation;

    /*FOR TAX YEAR 2018-2019*/
    private Boolean propertyAfter1JulyUpto1YearCheck;
    private String propertyAfter1JulyUpto1YearPurchaseCost;
    private String propertyAfter1JulyUpto1YearSaleCost;
    private String propertyAfter1JulyUpto1YearLocation;

    private Boolean propertyAfter1July1To2YearsCheck;
    private String propertyAfter1July1To2YearsPurchaseCost;
    private String propertyAfter1July1To2YearsSaleCost;
    private String propertyAfter1July1To2YearsLocation;

    private Boolean propertyAfter1July2To3YearsCheck;
    private String propertyAfter1July2To3YearsPurchaseCost;
    private String propertyAfter1July2To3YearsSaleCost;
    private String propertyAfter1July2To3YearsLocation;

    private Boolean propertyAfter1JulyAndMoreThan3YearsCheck;
    private String propertyAfter1JulyAndMoreThan3YearsPurchaseCost;
    private String propertyAfter1JulyAndMoreThan3YearsSaleCost;
    private String propertyAfter1JulyAndMoreThan3YearsLocation;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME*/
    private Boolean incomeTaxOtherSourcesCheck;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> Agricultural*/
    private Boolean bonusSharesAgriculturalIncomeCheck;
    private String bonusSharesAgriculturalIncome;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> BONUS SHARES*/
    private Boolean bonusSharesCheck;
    private String bonusShares;
    private String bonusSharesTaxDeducted;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> DIVIDEND*/
    private Boolean dividentCheck;
    private String dividentByPowerCompanies;
    private String dividentByPowerCompaniesTaxDeducted;
    private String dividentByOtherCompaniesStockFund;
    private String dividentByOtherCompaniesStockFundTaxDeducted;
    private String dividentByMutualFunds;
    private String dividentByMutualFundsTaxDeducted;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME -> PROFIT ON BANK DEPOSIT --- MULTIPLE VALUES*/

    private Boolean bankDepositCheck;
    private List<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean> taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans;

    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> OTHER INFLOWS*/
    private Boolean otherInflowsCheck;
    private String otherInflows;
    private List<TaxformIncomeTaxOtherSourcesOtherInFlowBean> taxformIncomeTaxOtherSourcesOtherInFlowBeanList;

    /*==================DEDUCTIBLE ALLOWANCE AND TAX CREDIT*/
    private Boolean donationsToCharityCheck;
    private Boolean donationsUnderSection61Check;
    private String donationsUnderSection61;
    private Boolean donationsUnderClause61Check;
    private Integer donationsUnderClause61ApprovedDoneeId;
    private String donationsUnderClause61;

    private Boolean investmentInSharesMutualFundsAndLifeInsuranceCheck;
    private String investmentInSharesMutualFundsAndLifeInsurance;

    private Boolean investmentInApprovedPensionFundCheck;
    private String investmentInApprovedPensionFund;

    private Boolean interestOrRateOnHouseHoldsCheck;
    private String interestOrRateOnHouseHolds;

    private Boolean helthInsurancePremiumCheck;
    private String helthInsurancePremium;

    private Boolean educationAllowanceTutionFeesCheck;
    private String educationAllowanceTutionFees;

    private Boolean educationAllowanceNoOfChildrensCheck;
    private String educationAllowanceNoOfChildrens;


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

    /*private Boolean propertyPurchaseOrSaleTaxDeductedCheck;
    private String propertyPurchaseOrSaleTaxDeducted;
*/

    private Boolean propertyPurchaseTaxDeductedCheck;
    private String propertyPurchaseTaxDeducted;

    private Boolean propertySaleTaxDeductedCheck;
    private String propertySaleTaxDeducted;

    private Boolean educationFeeTaxDeductedCheck;
    private String educationFeeTaxDeducted;

    private Boolean airTicketInYearTaxDeductedCheck;
    private String airTicketInYearTaxDeducted;

    private Boolean withdrawalFromPensionFundCheck;
    private String withdrawalFromPensionFund;

    private Boolean withdrawalFromPensionFundTaxDeductedCheck;
    private String withdrawalFromPensionFundTaxDeducted;

    private Boolean taxDeductedCollectedFunctionsAndGatheringsCheck;
    private String taxDeductedCollectedFunctionsAndGatherings;

    private Boolean taxRefundOfPriorYearCheck;
    private String taxRefundOfPriorYear;

    /*==================WELTH STATEMENT -> Openning Wealth */
    private String openingWealth;

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


    /*==================WELTH STATEMENT -> WELTH AT THE BEGINNING OF YEAR */
    private String value;

    /*==================WELTH STATEMENT -> DETAILS OF PERSONAL EXPENSES */
    private Boolean personalExpensesCheck;

    private String rent;
    private String ratesTaxesChargeCess;
    private String vehicleRunningOrMaintenance;
    private String travelling;
    private String electricity;
    private String water;
    private String gas;
    private String telephone;
    private String assetsInsuranceOrSecurity;
    private String medical;
    private String educational;
    private String club;
    private String functionsOrGatherings;
    private String donationZakatAnnuityProfitOnDebutEtc;
    private String otherPersonalOrHouseholdExpense;
    private String gift;


}