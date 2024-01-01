package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.taxform.*;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaxformServices {

    Taxform saveOrUpdate(Taxform taxform) throws Exception;
    Taxform createTaxfrom(Taxform taxform)throws Exception;

    Taxform updateTaxform(Taxform taxform)throws Exception;
    Taxform updateTaxformToAccountantStatus(Taxform taxform) throws Exception;

    Taxform findOne(Integer id)throws Exception;
    Taxform findOneByTaxformYearAndUser(TaxformYears taxformYear, User user) throws Exception;

    List<Taxform> findAll() throws Exception;
    List<Taxform> findAllByUserAndActiveTaxformYears(User user) throws Exception;
    List<Taxform> findAllByUserId(Integer userId)throws Exception;

    List<Taxform> findAllByUserRoleAndStatus(User user) throws Exception;
    Page<Taxform> findAllByUserRoleAndStatus(User user, int page, int size) throws Exception;


    List<Taxform> findAllByUserAndFBRStatus(User user) throws Exception;

    List<Taxform> findAllByStatus(Taxform_Status taxform_status);
    List<Taxform> findAllByStatusLessThan(Taxform_Status taxform_status);

    List<Taxform> findAllByStatusIn(List<Taxform_Status> taxform_statusList) throws Exception;
    List<Taxform> findAllByStatusNotIn(List<Taxform_Status> taxform_statusList) throws Exception;

    Taxform_Status findOneByTaxformStatusId(Integer taxformStatusId) throws Exception;


    Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit findOneTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Integer profitOnBankDepositId)throws Exception;
    TaxForm_IncomeTax_OtherSources_OtherInflow findOneTaxformIncomeTaxOtherSourcesOtherInflow(Integer otherInflowsId)throws Exception;

    Taxform_TaxDeductedCollected_BankingTransaction findOneTaxformTaxDeductedCollectedBankingTransaction(Integer id)throws Exception;
    Taxform_TaxDeductedCollected_WithholdTaxVehicle findOneTaxformTaxDeductedCollectedWithholdTaxVehicle(Integer id)throws Exception;
    Taxform_TaxDeductedCollected_Utilities findOneTaxformTaxDeductedCollectedUtilities(Integer id)throws Exception;

    Taxform_WelthStatement_PropertyDetail findOneTaxformWealthStatementPropertyDetail(Integer id)throws Exception;
    Taxform_WelthStatement_BankAccountsOrInvestments findOneTaxformWelthStatementBankAccountsOrInvestments(Integer id)throws Exception;
    Taxform_WelthStatement_OtherReceivablesOrAssets findOneTaxformWelthStatementOtherReceivablesOrAssets(Integer id)throws Exception;
    Taxform_WelthStatement_OwnVehicle findOneTaxformWelthStatementOwnVehicle(Integer id)throws Exception;
    Taxform_WelthStatement_OtherPossessions findOneTaxformWelthStatementOtherPossessions(Integer id)throws Exception;
    Taxform_WelthStatement_AssetsOutSidePakistan findOneTaxformWelthStatementAssetsOutSidePakistan(Integer id)throws Exception;
    Taxform_WelthStatement_OweAnyLoansOrCredit findOneTaxformWelthStatementOweAnyLoansOrCredit(Integer id)throws Exception;


    void deleteTaxformIncomeTaxSalary(Taxform taxform)throws Exception;
    void deleteTaxformIncomeTaxProperty(Taxform taxform)throws Exception;

    void deleteTaxformIncomeTaxCapitalGain(Taxform taxform)throws Exception;
    void deleteTaxformIncomeTaxCapitalGainOnShare(Taxform taxform)throws Exception;
    void deleteTaxformIncomeTaxCapitalGainMutualFunds(Taxform taxform)throws Exception;
    void deleteTaxformIncomeTaxCapitalGainProperty(Taxform taxform)throws Exception;

    void deleteTaxformIncomeTaxOtherSources(Taxform taxform)throws Exception;
    void deleteAllTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Taxform taxform)throws Exception;
    void deleteAllTaxformIncomeTaxOtherSourcesInflow(Taxform taxform)throws Exception;

    void deleteTaxformDeductibleAllowanceOrCredit(Taxform taxform)throws Exception;

    void deleteTaxformTaxDeductedCollectedBankingTransactions(Taxform taxform)throws Exception;
    void deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(Taxform taxform)throws Exception;
    void deleteTaxformTaxDeductedCollectedUtilities(Taxform taxform)throws Exception;
    void deleteTaxformTaxDeductedCollectedOthers(Taxform taxform)throws Exception;

    void deleteWealthStatement(Taxform taxform) throws Exception;
    void deleteWealthStatementPropertyDetail(Taxform taxform)throws Exception;
    void deleteWealthStatementBankAccountsOrInvestments(Taxform taxform)throws Exception;
    void deleteWealthStatementOwnVehicle(Taxform taxform)throws Exception;
    void deleteWealthStatementOtherPossessions(Taxform taxform)throws Exception;
    void deleteWealthStatementAssetsOutSidePakistan(Taxform taxform)throws Exception;
    void deleteWealthStatementOweAnyLoansOrCredit(Taxform taxform)throws Exception;
    void deleteWealthStatementDetailsOfPersonalExpenses(Taxform taxform)throws Exception;
    void deleteWealthStatementOtherRecrivablesOrAsstes(Taxform taxform)throws Exception ;


    Taxform_IncomeTax_Salary createTaxformIncomeTaxSalary(Taxform_IncomeTax_Salary taxformIncomeTaxSalary)throws Exception;
    Taxform_IncomeTax_Property createTaxformIncomeTaxProperty(Taxform_IncomeTax_Property taxformIncomeTaxProperty)throws Exception;

    Taxform_IncomeTax_CapitalGain_MutualFinds createTaxformIncomeTaxCapitalGainMutualFinds(Taxform_IncomeTax_CapitalGain_MutualFinds taxformIncomeTaxCapitalGainMutualFinds)throws Exception;
    Taxform_IncomeTax_CapitalGain_OnShare createTaxformIncomeTaxCapitalGainOnShare(Taxform_IncomeTax_CapitalGain_OnShare taxformIncomeTaxCapitalGainOnShare)throws Exception;
    Taxform_IncomeTax_CapitalGain_Property createTaxformIncomeTaxCapitalGainProperty(Taxform_IncomeTax_CapitalGain_Property taxformIncomeTaxCapitalGainProperty)throws Exception;

    Taxform_DeductibleAllowanceOrCredit createTaxformDeductedAllowanceOrCredit(Taxform_DeductibleAllowanceOrCredit taxformDeductibleAllowanceOrCredit)throws Exception;

    Taxform_IncomeTax_OtherSources createTaxformIncomeTaxOtherSources(Taxform_IncomeTax_OtherSources taxformIncomeTaxOtherSources)throws Exception;
    Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit createTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit)throws Exception;
    TaxForm_IncomeTax_OtherSources_OtherInflow  createTaxformIncomeTaxOtherSourcesOtherInflow(TaxForm_IncomeTax_OtherSources_OtherInflow taxformIncomeTaxOtherSourcesProfitOnBankDeposit)throws Exception;

    Taxform_TaxDeductedCollected_BankingTransaction createTaxformTaxDeductedCollectedBankingTransaction(Taxform_TaxDeductedCollected_BankingTransaction taxformTaxDeductedCollectedBankingTransaction)throws Exception;
    Taxform_TaxDeductedCollected_Utilities createTaxformTaxDeductedCollectedUtilities(Taxform_TaxDeductedCollected_Utilities taxformTaxDeductedCollectedUtilities)throws Exception;
    Taxform_TaxDeductedCollected_WithholdTaxVehicle createTaxformTaxDeductedCollectedWithholdTaxVehicle(Taxform_TaxDeductedCollected_WithholdTaxVehicle taxformTaxDeductedCollectedWithholdTaxVehicle)throws Exception;
    Taxform_TaxDeductedCollected_Other createTaxformTaxDeductedCollectedOther(Taxform_TaxDeductedCollected_Other taxformTaxDeductedCollectedOther)throws Exception;

    Taxform_WelthStatement createTaxformWelthStatement(Taxform_WelthStatement taxformWelthStatement)throws Exception;
    Taxform_WelthStatement_DetailsOfPersonalExpense createTaxformWelthStatementDetailsOfPersonalExpense(Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementQ1)throws Exception;
    Taxform_WelthStatement_PropertyDetail createTaxformWelthStatementPropertyDetail(Taxform_WelthStatement_PropertyDetail taxformWelthStatementQ2)throws Exception;
    Taxform_WelthStatement_BankAccountsOrInvestments createTaxformWelthStatementBankAccountsOrInvestments(Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementQ3)throws Exception;
    Taxform_WelthStatement_OtherReceivablesOrAssets createTaxformWelthStatementOtherReceivablesOrAssets(Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementQ4)throws Exception;
    Taxform_WelthStatement_OwnVehicle createTaxformWelthStatementOwnVehicle(Taxform_WelthStatement_OwnVehicle taxformWelthStatementQ5)throws Exception;
    Taxform_WelthStatement_OtherPossessions createTaxformWelthStatementOtherPossessions(Taxform_WelthStatement_OtherPossessions taxformWelthStatementQ6)throws Exception;
    Taxform_WelthStatement_AssetsOutSidePakistan createTaxformWelthStatementAssetsOutSidePakistan(Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementQ7)throws Exception;
    Taxform_WelthStatement_OweAnyLoansOrCredit createTaxformWelthStatementOweAnyLoansOrCredit(Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementQ8)throws Exception;


    void deleteTaxformIncomeTaxOtherSourcesProfitOnBankDepositByTaxformAndNotIn(Taxform taxform, List bankDepositUpdatedRecords) throws Exception;
    void deleteTaxFormIncomeTaxOtherSourcesOtherInflowByTaxformAndNotIn(Taxform taxform, List<Integer> otherInflowsUpdatedRecords) throws  Exception;

    void deleteTaxformTaxDeductedCollectedBankingTransactionByTaxformAndNotIn(Taxform taxform, List<Integer> deductedCollectedBankingTransactionUpdatedRecord) throws  Exception;
    void deleteTaxformTaxDeductedCollectedWithholdTaxVehicleByTaxformAndNotIn(Taxform taxform, List<Integer> withholdTaxVehicleUpdatedRecord) throws  Exception;
    void deleteTaxformTaxDeductedCollectedUtilitiesByTaxformAndNotIn(Taxform taxform, List<Integer> utilitiesUpdatedRecord) throws  Exception;

    void deleteTaxformWelthStatementPropertyDetailByTaxformAndNotIn(Taxform taxform, List<Integer> propertyDetailUpdatedRecords) throws  Exception;
    void deleteTaxformWelthStatementOtherReceivablesOrAssetsByTaxformAndNotIn(Taxform taxform, List<Integer> otherAssetsUpdatedRecords) throws  Exception;
    void deleteTaxformWelthStatementOwnVehicleByTaxformAndNotIn(Taxform taxform, List<Integer> OwnVehicleUpdatedRecords) throws  Exception;
    void deleteTaxformWelthStatementOtherPossessionsByTaxformAndNotIn(Taxform taxform, List<Integer> otherPossessionsUpdatedRecords) throws  Exception;
    void deleteTaxformWelthStatementAssetsOutSidePakistanByTaxformAndNotIn(Taxform taxform, List<Integer> assetsOutsidePakistanUpdatedRecords) throws  Exception;
    void deleteTaxformWelthStatementOweAnyLoansOrCreditByTaxformAndNotIn(Taxform taxform, List<Integer> oweAnyLoansOrCreditUpdatedRecord) throws  Exception;
    void deleteTaxformWelthStatementBankAccountsOrInvestmentsByTaxformAndNotIn(Taxform taxform, List<Integer> bankAccountsOrInvestmentUpdatedRecord) throws  Exception;
}
