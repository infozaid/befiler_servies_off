package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.taxform.*;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.taxformRepository.*;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxformServicesImpl implements TaxformServices {

    private UsersServices usersServices;
    private TaxformStatusServices taxformStatusServices;
    private AssignServices assignServices;
    private Taxform_Repository taxformRepository;
    private TaxformYearsServices taxformYearsServices;
    private Taxform_Status_Repository taxformStatusRepository;
    private Taxform_DeductibleAllowanceOrCredit_Repository taxformDeductibleAllowanceOrCreditRepository;
    private Taxform_IncomeTax_CapitalGain_MutualFinds_Repository taxformIncomeTaxCapitalGainMutualFindsRepository;
    private Taxform_IncomeTax_CapitalGain_OnShare_Repository taxformIncomeTaxCapitalGainOnShareRepository;
    private Taxform_IncomeTax_CapitalGain_Property_Repository taxformIncomeTaxCapitalGainPropertyRepository;
    private Taxform_IncomeTax_OtherSources_Repository taxformIncomeTaxOtherSourcesRepository;
    private Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit_Repository taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository;
    private Taxform_IncomeTax_Property_Repository taxformIncomeTaxPropertyRepository;
    private Taxform_IncomeTax_Salary_Repository taxformIncomeTaxSalaryRepository;
    private Taxform_TaxDeductedCollected_BankingTransaction_Repository taxformTaxDeductedCollectedBankingTransactionRepository;
    private Taxform_IncomeTax_OtherSources_Other_InFlow_Repository taxformIncomeTaxOtherSourcesOtherInFlowRepository;
    private Taxform_TaxDeductedCollected_Other_Repository taxformTaxDeductedCollectedOtherRepository;
    private Taxform_TaxDeductedCollected_Utilities_Repository taxformTaxDeductedCollectedUtilitiesRepository;
    private Taxform_TaxDeductedCollected_WithholdTaxVehicle_Repository taxformTaxDeductedCollectedWithholdTaxVehicleRepository;
    private Taxform_WelthStatement_Repository taxformWelthStatementRepository;
    private Taxform_WelthStatement_DetailsOfPersonalExpense_Repository taxformWelthStatementDetailsOfPersonalExpenseRepository;
    private Taxform_WelthStatement_PropertyDetail_Repository taxformWelthStatementPropertyDetailRepository;
    private Taxform_WelthStatement_BankAccountsOrInvestments_Repository taxformWelthStatementBankAccountsOrInvestmentsRepository;
    private Taxform_WelthStatement_OtherReceivablesOrAssets_Repository taxformWelthStatementOtherReceivablesOrAssetsRepository;
    private Taxform_WelthStatement_OwnVehicle_Repository taxformWelthStatementOwnVehicleRepository;
    private Taxform_WelthStatement_OtherPossessions_Repository taxformWelthStatementOtherPossessionsRepository;
    private Taxform_WelthStatement_AssetsOutSidePakistan_Repository taxformWelthStatementAssetsOutSidePakistanRepository;
    private Taxform_WelthStatement_OweAnyLoansOrCredit_Repository taxformWelthStatementOweAnyLoansOrCreditRepository;

    @Autowired
    public TaxformServicesImpl(UsersServices usersServices, TaxformStatusServices taxformStatusServices, AssignServices assignServices, Taxform_Repository taxformRepository, TaxformYearsServices taxformYearsServices, Taxform_Status_Repository taxformStatusRepository, Taxform_DeductibleAllowanceOrCredit_Repository taxformDeductibleAllowanceOrCreditRepository, Taxform_IncomeTax_CapitalGain_MutualFinds_Repository taxformIncomeTaxCapitalGainMutualFindsRepository, Taxform_IncomeTax_CapitalGain_OnShare_Repository taxformIncomeTaxCapitalGainOnShareRepository, Taxform_IncomeTax_CapitalGain_Property_Repository taxformIncomeTaxCapitalGainPropertyRepository, Taxform_IncomeTax_OtherSources_Repository taxformIncomeTaxOtherSourcesRepository, Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit_Repository taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository, Taxform_IncomeTax_Property_Repository taxformIncomeTaxPropertyRepository, Taxform_IncomeTax_Salary_Repository taxformIncomeTaxSalaryRepository, Taxform_TaxDeductedCollected_BankingTransaction_Repository taxformTaxDeductedCollectedBankingTransactionRepository, Taxform_IncomeTax_OtherSources_Other_InFlow_Repository taxformIncomeTaxOtherSourcesOtherInFlowRepository, Taxform_TaxDeductedCollected_Other_Repository taxformTaxDeductedCollectedOtherRepository, Taxform_TaxDeductedCollected_Utilities_Repository taxformTaxDeductedCollectedUtilitiesRepository, Taxform_TaxDeductedCollected_WithholdTaxVehicle_Repository taxformTaxDeductedCollectedWithholdTaxVehicleRepository, Taxform_WelthStatement_Repository taxformWelthStatementRepository, Taxform_WelthStatement_DetailsOfPersonalExpense_Repository taxformWelthStatementDetailsOfPersonalExpenseRepository, Taxform_WelthStatement_PropertyDetail_Repository taxformWelthStatementPropertyDetailRepository, Taxform_WelthStatement_BankAccountsOrInvestments_Repository taxformWelthStatementBankAccountsOrInvestmentsRepository, Taxform_WelthStatement_OtherReceivablesOrAssets_Repository taxformWelthStatementOtherReceivablesOrAssetsRepository, Taxform_WelthStatement_OwnVehicle_Repository taxformWelthStatementOwnVehicleRepository, Taxform_WelthStatement_OtherPossessions_Repository taxformWelthStatementOtherPossessionsRepository, Taxform_WelthStatement_AssetsOutSidePakistan_Repository taxformWelthStatementAssetsOutSidePakistanRepository, Taxform_WelthStatement_OweAnyLoansOrCredit_Repository taxformWelthStatementOweAnyLoansOrCreditRepository) {
        this.usersServices = usersServices;
        this.taxformStatusServices = taxformStatusServices;
        this.assignServices = assignServices;
        this.taxformRepository = taxformRepository;
        this.taxformYearsServices = taxformYearsServices;
        this.taxformStatusRepository = taxformStatusRepository;
        this.taxformDeductibleAllowanceOrCreditRepository = taxformDeductibleAllowanceOrCreditRepository;
        this.taxformIncomeTaxCapitalGainMutualFindsRepository = taxformIncomeTaxCapitalGainMutualFindsRepository;
        this.taxformIncomeTaxCapitalGainOnShareRepository = taxformIncomeTaxCapitalGainOnShareRepository;
        this.taxformIncomeTaxCapitalGainPropertyRepository = taxformIncomeTaxCapitalGainPropertyRepository;
        this.taxformIncomeTaxOtherSourcesRepository = taxformIncomeTaxOtherSourcesRepository;
        this.taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository = taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository;
        this.taxformIncomeTaxPropertyRepository = taxformIncomeTaxPropertyRepository;
        this.taxformIncomeTaxSalaryRepository = taxformIncomeTaxSalaryRepository;
        this.taxformTaxDeductedCollectedBankingTransactionRepository = taxformTaxDeductedCollectedBankingTransactionRepository;
        this.taxformIncomeTaxOtherSourcesOtherInFlowRepository = taxformIncomeTaxOtherSourcesOtherInFlowRepository;
        this.taxformTaxDeductedCollectedOtherRepository = taxformTaxDeductedCollectedOtherRepository;
        this.taxformTaxDeductedCollectedUtilitiesRepository = taxformTaxDeductedCollectedUtilitiesRepository;
        this.taxformTaxDeductedCollectedWithholdTaxVehicleRepository = taxformTaxDeductedCollectedWithholdTaxVehicleRepository;
        this.taxformWelthStatementRepository = taxformWelthStatementRepository;
        this.taxformWelthStatementDetailsOfPersonalExpenseRepository = taxformWelthStatementDetailsOfPersonalExpenseRepository;
        this.taxformWelthStatementPropertyDetailRepository = taxformWelthStatementPropertyDetailRepository;
        this.taxformWelthStatementBankAccountsOrInvestmentsRepository = taxformWelthStatementBankAccountsOrInvestmentsRepository;
        this.taxformWelthStatementOtherReceivablesOrAssetsRepository = taxformWelthStatementOtherReceivablesOrAssetsRepository;
        this.taxformWelthStatementOwnVehicleRepository = taxformWelthStatementOwnVehicleRepository;
        this.taxformWelthStatementOtherPossessionsRepository = taxformWelthStatementOtherPossessionsRepository;
        this.taxformWelthStatementAssetsOutSidePakistanRepository = taxformWelthStatementAssetsOutSidePakistanRepository;
        this.taxformWelthStatementOweAnyLoansOrCreditRepository = taxformWelthStatementOweAnyLoansOrCreditRepository;
    }

    /*OVERRIDED METHODS*/

    @Override
    public Taxform saveOrUpdate(Taxform taxform) throws Exception {
        if (taxform != null) {
            taxformRepository.save(taxform);
        }
        return null;
    }

    @Override
    public Taxform createTaxfrom(Taxform taxform) throws Exception {
        if (taxform.getId() != null) {
            return null;
        }
        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
        Taxform savedTaxform = taxformRepository.save(taxform);
        return savedTaxform;
    }

    @Override
    public Taxform updateTaxform(Taxform taxform) throws Exception {
        if (taxform.getId() != null) {
            Taxform taxformPersisted = findOne(taxform.getId());
            if (taxformPersisted == null) {
                return null;
            }
            taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
            return taxformRepository.save(taxform);
        }
        return null;
    }

    @Override
    public Taxform updateTaxformToAccountantStatus(Taxform taxform) throws Exception {
        if (taxform != null) {
            taxform.setStatus(taxformStatusServices.findOneByAccountantStatus());
            taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

            return taxformRepository.save(taxform);
        }
        return null;
    }

    @Override
    public Taxform findOne(Integer id) throws Exception {
        Taxform taxform = taxformRepository.findById(id).orElse(null);
        return taxform;
    }

    @Override
    public Taxform findOneByTaxformYearAndUser(TaxformYears taxformYear, User user) throws Exception {
        if (taxformYear != null && user != null) {
            return taxformRepository.findOneByTaxformYearAndUser(taxformYear, user);
        }
        return null;
    }

    @Override
    public List<Taxform> findAll() throws Exception {
        List<Taxform> taxformsList = (ArrayList<Taxform>) taxformRepository.findAll();
        return taxformsList;
    }

    @Override
    public List<Taxform> findAllByUserAndActiveTaxformYears(User user) throws Exception {
        List<TaxformYears> taxformYearsList = taxformYearsServices.findAllActiveYears();
        if (user != null && taxformYearsList != null) {
            return taxformRepository.findAllByUserAndTaxformYearIn(user, taxformYearsList);
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByUserId(Integer userId) throws Exception {
        if (userId != null) {
            /*User user = usersServices.findOneByIdAndStatus(userId, usersServices.findUserStatusById(1));*/
            User user = usersServices.findOneByIdAndStatus(userId, UserStatus.ACTIVE);
            if (user != null) {
                List<Taxform> taxformsList = (ArrayList<Taxform>) taxformRepository.findAllByUser(user);
                return taxformsList;
            }
        }
        return new ArrayList<Taxform>();

    }

    @Override
    public Taxform_Status findOneByTaxformStatusId(Integer taxformStatusId) throws Exception {
        if (taxformStatusId != null) {
            Taxform_Status taxformStatus = taxformStatusRepository.findById(taxformStatusId).orElse(null);
            return taxformStatus;
        }
        return null;
    }

    @Override
    public void deleteTaxformIncomeTaxSalary(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformIncomeTaxSalary() != null) {
                taxformIncomeTaxSalaryRepository.delete(taxform.getTaxformIncomeTaxSalary());
            }
        }
    }

    @Override
    public void deleteTaxformIncomeTaxProperty(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformIncomeTaxProperty() != null) {
                taxformIncomeTaxPropertyRepository.delete(taxform.getTaxformIncomeTaxProperty());
            }
        }
    }

    @Override
    public void deleteTaxformIncomeTaxCapitalGain(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null) {
                taxformIncomeTaxCapitalGainOnShareRepository.delete(taxform.getTaxformIncomeTaxCapitalGainOnShare());
            }
            if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null) {
                taxformIncomeTaxCapitalGainMutualFindsRepository.delete(taxform.getTaxformIncomeTaxCapitalGainMutualFinds());
            }

            if (taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {
                taxformIncomeTaxCapitalGainPropertyRepository.delete(taxform.getTaxformIncomeTaxCapitalGainProperty());
            }
        }
    }

    @Override
    public void deleteTaxformIncomeTaxCapitalGainOnShare(Taxform taxform) throws Exception {
        if (taxform != null && taxform.getTaxformIncomeTaxCapitalGainOnShare() != null) {
            taxformIncomeTaxCapitalGainOnShareRepository.delete(taxform.getTaxformIncomeTaxCapitalGainOnShare());
        }
    }

    @Override
    public void deleteTaxformIncomeTaxCapitalGainMutualFunds(Taxform taxform) throws Exception {
        if (taxform != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null) {
            taxformIncomeTaxCapitalGainMutualFindsRepository.delete(taxform.getTaxformIncomeTaxCapitalGainMutualFinds());
        }
    }

    @Override
    public void deleteTaxformIncomeTaxCapitalGainProperty(Taxform taxform) throws Exception {
        if (taxform != null && taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {
            if (taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {
                taxformIncomeTaxCapitalGainPropertyRepository.delete(taxform.getTaxformIncomeTaxCapitalGainProperty());
            }
        }
    }

    @Override
    public Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit findOneTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Integer profitOnBankDepositId) throws Exception {
        Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit = taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository.findById(profitOnBankDepositId).orElse(null);
        return taxformIncomeTaxOtherSourcesProfitOnBankDeposit;
    }

    @Override
    public TaxForm_IncomeTax_OtherSources_OtherInflow findOneTaxformIncomeTaxOtherSourcesOtherInflow(Integer otherInflowsId) throws Exception {
        TaxForm_IncomeTax_OtherSources_OtherInflow taxformIncomeTaxOtherSourcesOtherInflow = taxformIncomeTaxOtherSourcesOtherInFlowRepository.findById(otherInflowsId).orElse(null);
        return taxformIncomeTaxOtherSourcesOtherInflow;
    }

    @Override
    public void deleteTaxformIncomeTaxOtherSources(Taxform taxform) throws Exception {
        if (taxform != null) {
            Taxform_IncomeTax_OtherSources taxform_incomeTax_otherSources = taxform.getTaxformIncomeTaxOtherSources();
            if (taxform_incomeTax_otherSources != null) {
                taxformIncomeTaxOtherSourcesRepository.delete(taxform_incomeTax_otherSources);
            }

            if (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0) {
                taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository.deleteAll(taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList());
            }

            if (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) {
                taxformIncomeTaxOtherSourcesOtherInFlowRepository.deleteAll(taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList());
            }
        }
    }

    @Override
    public void deleteAllTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0) {
                taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository.deleteAll(taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList());
            }
        }
    }

    @Override
    public void deleteAllTaxformIncomeTaxOtherSourcesInflow(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) {
                taxformIncomeTaxOtherSourcesOtherInFlowRepository.deleteAll(taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList());
            }
        }
    }

    @Override
    public Taxform_TaxDeductedCollected_BankingTransaction findOneTaxformTaxDeductedCollectedBankingTransaction(Integer id) throws Exception {
        Taxform_TaxDeductedCollected_BankingTransaction taxformTaxDeductedCollectedBankingTransaction = taxformTaxDeductedCollectedBankingTransactionRepository.findById(id).orElse(null);
        return taxformTaxDeductedCollectedBankingTransaction;
    }

    @Override
    public Taxform_TaxDeductedCollected_WithholdTaxVehicle findOneTaxformTaxDeductedCollectedWithholdTaxVehicle(Integer id) throws Exception {
        Taxform_TaxDeductedCollected_WithholdTaxVehicle taxformTaxDeductedCollectedWithholdTaxVehicle = taxformTaxDeductedCollectedWithholdTaxVehicleRepository.findById(id).orElse(null);
        return taxformTaxDeductedCollectedWithholdTaxVehicle;
    }

    @Override
    public Taxform_TaxDeductedCollected_Utilities findOneTaxformTaxDeductedCollectedUtilities(Integer id) throws Exception {
        Taxform_TaxDeductedCollected_Utilities taxformTaxDeductedCollectedUtilities = taxformTaxDeductedCollectedUtilitiesRepository.findById(id).orElse(null);
        return taxformTaxDeductedCollectedUtilities;
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedBankingTransactions(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) {
                taxformTaxDeductedCollectedBankingTransactionRepository.deleteAll(taxform.getTaxformTaxDeductedCollectedBankingTransactionList());
            }
        }
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) {
                taxformTaxDeductedCollectedWithholdTaxVehicleRepository.deleteAll(taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList());
            }
        }
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedUtilities(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) {
                taxformTaxDeductedCollectedUtilitiesRepository.deleteAll(taxform.getTaxformTaxDeductedCollectedUtilitiesList());
            }
        }
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedOthers(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformTaxDeductedCollectedOther() != null) {
                taxformTaxDeductedCollectedOtherRepository.delete(taxform.getTaxformTaxDeductedCollectedOther());
            }
        }
    }

    @Override
    public void deleteTaxformDeductibleAllowanceOrCredit(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                taxformDeductibleAllowanceOrCreditRepository.delete(taxform.getTaxformDeductibleAllowanceOrCredit());
            }
        }
    }

    @Override
    public Taxform_WelthStatement_PropertyDetail findOneTaxformWealthStatementPropertyDetail(Integer id) throws Exception {
        Taxform_WelthStatement_PropertyDetail taxformWelthStatementPropertyDetail = taxformWelthStatementPropertyDetailRepository.findById(id).orElse(null);
        return taxformWelthStatementPropertyDetail;
    }

    @Override
    public Taxform_WelthStatement_BankAccountsOrInvestments findOneTaxformWelthStatementBankAccountsOrInvestments(Integer id) throws Exception {
        Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementBankAccountsOrInvestments = taxformWelthStatementBankAccountsOrInvestmentsRepository.findById(id).orElse(null);
        return taxformWelthStatementBankAccountsOrInvestments;
    }

    @Override
    public Taxform_WelthStatement_OtherReceivablesOrAssets findOneTaxformWelthStatementOtherReceivablesOrAssets(Integer id) throws Exception {
        Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementOtherReceivablesOrAssets = taxformWelthStatementOtherReceivablesOrAssetsRepository.findById(id).orElse(null);
        return taxformWelthStatementOtherReceivablesOrAssets;
    }

    @Override
    public Taxform_WelthStatement_OwnVehicle findOneTaxformWelthStatementOwnVehicle(Integer id) throws Exception {
        Taxform_WelthStatement_OwnVehicle taxformWelthStatementOwnVehicle = taxformWelthStatementOwnVehicleRepository.findById(id).orElse(null);
        return taxformWelthStatementOwnVehicle;
    }

    @Override
    public Taxform_WelthStatement_OtherPossessions findOneTaxformWelthStatementOtherPossessions(Integer id) throws Exception {
        Taxform_WelthStatement_OtherPossessions taxformWelthStatementOtherPossessions = taxformWelthStatementOtherPossessionsRepository.findById(id).orElse(null);
        return taxformWelthStatementOtherPossessions;
    }

    @Override
    public Taxform_WelthStatement_AssetsOutSidePakistan findOneTaxformWelthStatementAssetsOutSidePakistan(Integer id) throws Exception {
        Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementAssetsOutSidePakistan = taxformWelthStatementAssetsOutSidePakistanRepository.findById(id).orElse(null);
        return taxformWelthStatementAssetsOutSidePakistan;
    }

    @Override
    public Taxform_WelthStatement_OweAnyLoansOrCredit findOneTaxformWelthStatementOweAnyLoansOrCredit(Integer id) throws Exception {
        Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementOweAnyLoansOrCredit = taxformWelthStatementOweAnyLoansOrCreditRepository.findById(id).orElse(null);
        return taxformWelthStatementOweAnyLoansOrCredit;
    }

    @Override
    public void deleteWealthStatement(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatement() != null) {
                taxformWelthStatementRepository.delete(taxform.getTaxformWelthStatement());
            }
            if (taxform.getTaxformWelthStatementPropertyDetailList() != null && taxform.getTaxformWelthStatementPropertyDetailList().size() > 0) {
                taxformWelthStatementPropertyDetailRepository.deleteAll(taxform.getTaxformWelthStatementPropertyDetailList());
            }
            if (taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList() != null && taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList().size() > 0) {
                taxformWelthStatementBankAccountsOrInvestmentsRepository.deleteAll(taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList());
            }
            if (taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList() != null && taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList().size() > 0) {
                taxformWelthStatementOtherReceivablesOrAssetsRepository.deleteAll(taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList());
            }
            if (taxform.getTaxformWelthStatementOwnVehicleList() != null && taxform.getTaxformWelthStatementOwnVehicleList().size() > 0) {
                taxformWelthStatementOwnVehicleRepository.deleteAll(taxform.getTaxformWelthStatementOwnVehicleList());
            }
            if (taxform.getTaxformWelthStatementOtherPossessionsList() != null && taxform.getTaxformWelthStatementOtherPossessionsList().size() > 0) {
                taxformWelthStatementOtherPossessionsRepository.deleteAll(taxform.getTaxformWelthStatementOtherPossessionsList());
            }
            if (taxform.getTaxformWelthStatementAssetsOutSidePakistanList() != null && taxform.getTaxformWelthStatementAssetsOutSidePakistanList().size() > 0) {
                taxformWelthStatementAssetsOutSidePakistanRepository.deleteAll(taxform.getTaxformWelthStatementAssetsOutSidePakistanList());
            }
            if (taxform.getTaxformWelthStatementOweAnyLoansOrCreditList() != null && taxform.getTaxformWelthStatementOweAnyLoansOrCreditList().size() > 0) {
                taxformWelthStatementOweAnyLoansOrCreditRepository.deleteAll(taxform.getTaxformWelthStatementOweAnyLoansOrCreditList());
            }
            if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense() != null) {
                taxformWelthStatementDetailsOfPersonalExpenseRepository.delete(taxform.getTaxformWelthStatementDetailsOfPersonalExpense());
            }
        }
    }

    @Override
    public void deleteWealthStatementPropertyDetail(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementPropertyDetailList() != null && taxform.getTaxformWelthStatementPropertyDetailList().size() > 0) {
                taxformWelthStatementPropertyDetailRepository.deleteAll(taxform.getTaxformWelthStatementPropertyDetailList());
            }
        }
    }

    @Override
    public void deleteWealthStatementBankAccountsOrInvestments(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList() != null && taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList().size() > 0) {
                taxformWelthStatementBankAccountsOrInvestmentsRepository.deleteAll(taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList());
            }
        }
    }

    @Override
    public void deleteWealthStatementOtherRecrivablesOrAsstes(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList() != null && taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList().size() > 0) {
                taxformWelthStatementOtherReceivablesOrAssetsRepository.deleteAll(taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList());
            }
        }
    }

    @Override
    public void deleteWealthStatementOwnVehicle(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementOwnVehicleList() != null && taxform.getTaxformWelthStatementOwnVehicleList().size() > 0) {
                taxformWelthStatementOwnVehicleRepository.deleteAll(taxform.getTaxformWelthStatementOwnVehicleList());
            }
        }
    }

    @Override
    public void deleteWealthStatementOtherPossessions(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementOtherPossessionsList() != null && taxform.getTaxformWelthStatementOtherPossessionsList().size() > 0) {
                taxformWelthStatementOtherPossessionsRepository.deleteAll(taxform.getTaxformWelthStatementOtherPossessionsList());
            }
        }
    }

    @Override
    public void deleteWealthStatementAssetsOutSidePakistan(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementAssetsOutSidePakistanList() != null && taxform.getTaxformWelthStatementAssetsOutSidePakistanList().size() > 0) {
                taxformWelthStatementAssetsOutSidePakistanRepository.deleteAll(taxform.getTaxformWelthStatementAssetsOutSidePakistanList());
            }
        }
    }

    @Override
    public void deleteWealthStatementOweAnyLoansOrCredit(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementOweAnyLoansOrCreditList() != null && taxform.getTaxformWelthStatementOweAnyLoansOrCreditList().size() > 0) {
                taxformWelthStatementOweAnyLoansOrCreditRepository.deleteAll(taxform.getTaxformWelthStatementOweAnyLoansOrCreditList());
            }
        }
    }

    @Override
    public void deleteWealthStatementDetailsOfPersonalExpenses(Taxform taxform) throws Exception {
        if (taxform != null) {
            if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense() != null) {
                taxformWelthStatementDetailsOfPersonalExpenseRepository.delete(taxform.getTaxformWelthStatementDetailsOfPersonalExpense());
            }
        }
    }

    @Override
    public Taxform_DeductibleAllowanceOrCredit createTaxformDeductedAllowanceOrCredit(Taxform_DeductibleAllowanceOrCredit taxformDeductibleAllowanceOrCredit) throws Exception {
        return taxformDeductibleAllowanceOrCreditRepository.save(taxformDeductibleAllowanceOrCredit);
    }

    @Override
    public Taxform_IncomeTax_CapitalGain_MutualFinds createTaxformIncomeTaxCapitalGainMutualFinds(Taxform_IncomeTax_CapitalGain_MutualFinds taxformIncomeTaxCapitalGainMutualFinds) throws Exception {
        return taxformIncomeTaxCapitalGainMutualFindsRepository.save(taxformIncomeTaxCapitalGainMutualFinds);
    }

    @Override
    public Taxform_IncomeTax_CapitalGain_OnShare createTaxformIncomeTaxCapitalGainOnShare(Taxform_IncomeTax_CapitalGain_OnShare taxformIncomeTaxCapitalGainOnShare) throws Exception {
        return taxformIncomeTaxCapitalGainOnShareRepository.save(taxformIncomeTaxCapitalGainOnShare);
    }

    @Override
    public Taxform_IncomeTax_CapitalGain_Property createTaxformIncomeTaxCapitalGainProperty(Taxform_IncomeTax_CapitalGain_Property taxformIncomeTaxCapitalGainProperty) throws Exception {
        return taxformIncomeTaxCapitalGainPropertyRepository.save(taxformIncomeTaxCapitalGainProperty);
    }

    @Override
    public Taxform_IncomeTax_OtherSources createTaxformIncomeTaxOtherSources(Taxform_IncomeTax_OtherSources taxformIncomeTaxOtherSources) throws Exception {
        return taxformIncomeTaxOtherSourcesRepository.save(taxformIncomeTaxOtherSources);
    }

    @Override
    public Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit createTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit) throws Exception {
        return taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository.save(taxformIncomeTaxOtherSourcesProfitOnBankDeposit);
    }

    @Override
    public TaxForm_IncomeTax_OtherSources_OtherInflow createTaxformIncomeTaxOtherSourcesOtherInflow(TaxForm_IncomeTax_OtherSources_OtherInflow taxformIncomeTaxOtherSourcesOtherInflow) throws Exception {
        return taxformIncomeTaxOtherSourcesOtherInFlowRepository.save(taxformIncomeTaxOtherSourcesOtherInflow);
    }

    @Override
    public Taxform_IncomeTax_Property createTaxformIncomeTaxProperty(Taxform_IncomeTax_Property taxformIncomeTaxProperty) throws Exception {
        return taxformIncomeTaxPropertyRepository.save(taxformIncomeTaxProperty);
    }

    @Override
    public Taxform_IncomeTax_Salary createTaxformIncomeTaxSalary(Taxform_IncomeTax_Salary taxformIncomeTaxSalary) throws Exception {
        return taxformIncomeTaxSalaryRepository.save(taxformIncomeTaxSalary);
    }

    @Override
    public Taxform_TaxDeductedCollected_BankingTransaction createTaxformTaxDeductedCollectedBankingTransaction(Taxform_TaxDeductedCollected_BankingTransaction taxformTaxDeductedCollectedBankingTransaction) throws Exception {
        return taxformTaxDeductedCollectedBankingTransactionRepository.save(taxformTaxDeductedCollectedBankingTransaction);
    }

    @Override
    public Taxform_TaxDeductedCollected_Other createTaxformTaxDeductedCollectedOther(Taxform_TaxDeductedCollected_Other taxformTaxDeductedCollectedOther) throws Exception {
        return taxformTaxDeductedCollectedOtherRepository.save(taxformTaxDeductedCollectedOther);
    }

    @Override
    public Taxform_TaxDeductedCollected_Utilities createTaxformTaxDeductedCollectedUtilities(Taxform_TaxDeductedCollected_Utilities taxformTaxDeductedCollectedUtilities) throws Exception {
        return taxformTaxDeductedCollectedUtilitiesRepository.save(taxformTaxDeductedCollectedUtilities);
    }

    @Override
    public Taxform_TaxDeductedCollected_WithholdTaxVehicle createTaxformTaxDeductedCollectedWithholdTaxVehicle(Taxform_TaxDeductedCollected_WithholdTaxVehicle taxformTaxDeductedCollectedWithholdTaxVehicle) throws Exception {
        return taxformTaxDeductedCollectedWithholdTaxVehicleRepository.save(taxformTaxDeductedCollectedWithholdTaxVehicle);
    }

    @Override
    public Taxform_WelthStatement createTaxformWelthStatement(Taxform_WelthStatement taxformWelthStatement) throws Exception {
        return taxformWelthStatementRepository.save(taxformWelthStatement);
    }

    @Override
    public Taxform_WelthStatement_DetailsOfPersonalExpense createTaxformWelthStatementDetailsOfPersonalExpense(Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementQ1) throws Exception {
        return taxformWelthStatementDetailsOfPersonalExpenseRepository.save(taxformWelthStatementQ1);
    }

    @Override
    public Taxform_WelthStatement_PropertyDetail createTaxformWelthStatementPropertyDetail(Taxform_WelthStatement_PropertyDetail taxformWelthStatementQ2) throws Exception {
        return taxformWelthStatementPropertyDetailRepository.save(taxformWelthStatementQ2);
    }

    @Override
    public Taxform_WelthStatement_BankAccountsOrInvestments createTaxformWelthStatementBankAccountsOrInvestments(Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementQ3) throws Exception {
        return taxformWelthStatementBankAccountsOrInvestmentsRepository.save(taxformWelthStatementQ3);
    }

    @Override
    public Taxform_WelthStatement_OtherReceivablesOrAssets createTaxformWelthStatementOtherReceivablesOrAssets(Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementQ4) throws Exception {
        return taxformWelthStatementOtherReceivablesOrAssetsRepository.save(taxformWelthStatementQ4);
    }

    @Override
    public Taxform_WelthStatement_OwnVehicle createTaxformWelthStatementOwnVehicle(Taxform_WelthStatement_OwnVehicle taxformWelthStatementQ5) throws Exception {
        return taxformWelthStatementOwnVehicleRepository.save(taxformWelthStatementQ5);
    }

    @Override
    public Taxform_WelthStatement_OtherPossessions createTaxformWelthStatementOtherPossessions(Taxform_WelthStatement_OtherPossessions taxformWelthStatementQ6) throws Exception {
        return taxformWelthStatementOtherPossessionsRepository.save(taxformWelthStatementQ6);
    }

    @Override
    public Taxform_WelthStatement_AssetsOutSidePakistan createTaxformWelthStatementAssetsOutSidePakistan(Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementQ7) throws Exception {
        return taxformWelthStatementAssetsOutSidePakistanRepository.save(taxformWelthStatementQ7);
    }

    @Override
    public Taxform_WelthStatement_OweAnyLoansOrCredit createTaxformWelthStatementOweAnyLoansOrCredit(Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementQ8) throws Exception {
        return taxformWelthStatementOweAnyLoansOrCreditRepository.save(taxformWelthStatementQ8);
    }

    @Override
    public List<Taxform> findAllByStatus(Taxform_Status taxform_status) {
        if (taxform_status != null) {
            return taxformRepository.findAllByStatus(taxform_status);
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByStatusLessThan(Taxform_Status taxform_status) {
        if (taxform_status != null) {
            return taxformRepository.findAllByStatusLessThan(taxform_status);
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByStatusIn(List<Taxform_Status> taxform_statusList) throws Exception {
        if (taxform_statusList != null) {
            return taxformRepository.findAllByStatusIn(taxform_statusList);
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByStatusNotIn(List<Taxform_Status> taxform_statusList) throws Exception {
        if (taxform_statusList != null) {
            return taxformRepository.findAllByStatusNotIn(taxform_statusList);
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByUserRoleAndStatus(User user) throws Exception {

        if (user != null) {

            if (usersServices.checkIfUserIsCustomer(user)) {
                return taxformRepository.findAllByUser(user);
            } else if (usersServices.checkIfUserIsLawyer(user)) {

                /*List<Taxform_Status> taxformStatusList = new ArrayList<>();
                taxformStatusList.add(taxformStatusRepository.findOne(5));
                taxformStatusList.add(taxformStatusRepository.findOne(6));
                taxformStatusList.add(taxformStatusRepository.findOne(7));

                List<Taxform> taxformList = findAllByStatusIn(taxformStatusList);*/

                //TODO uncomment this code afte authoassign -- done
                List<Assign> taxformAssignList = assignServices.findAllByLawyerAndAppStatus(user, AppStatus.ACTIVE);
                List<Taxform> taxformList = new ArrayList<>();
                if (taxformAssignList != null) {
                    for (Assign taxformAssign : taxformAssignList) {
                        if (taxformAssign != null && taxformAssign.getTaxform() != null) {
                            taxformList.add(taxformAssign.getTaxform());
                        }
                    }
                }
                return taxformList;
            } else if (usersServices.checkIfUserIsAdmin(user) || usersServices.checkIfUserIsAccountant(user)) {
                return (List<Taxform>) taxformRepository.findAll();
            }
        }
        return null;
    }

    @Override
    public Page<Taxform> findAllByUserRoleAndStatus(User user, int page, int size) throws Exception {

        if (user != null) {

            if (usersServices.checkIfUserIsAdmin(user) || usersServices.checkIfUserIsAccountant(user)) {

                Pageable pageable = PageRequest.of(page, size);
                Page<Taxform> taxforms = taxformRepository.findAll(pageable);

                return taxforms;
            }
        }
        return null;
    }

    @Override
    public List<Taxform> findAllByUserAndFBRStatus(User user) throws Exception {
        Taxform_Status status = taxformStatusServices.findOneByFBRStatus();
        if (user != null && status != null) {
            taxformRepository.findAllByUserAndStatus(user, status);
        }
        return null;
    }

    @Override
    public void deleteTaxformIncomeTaxOtherSourcesProfitOnBankDepositByTaxformAndNotIn(Taxform taxform, List bankDepositUpdatedRecords) throws Exception {
        taxformIncomeTaxOtherSourcesProfitOnBankDepositRepository.deleteAllByTaxformAndIdNotIn(taxform, bankDepositUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementPropertyDetailByTaxformAndNotIn(Taxform taxform, List<Integer> propertyDetailUpdatedRecords) throws Exception {
        taxformWelthStatementPropertyDetailRepository.deleteAllByTaxformAndIdNotIn(taxform, propertyDetailUpdatedRecords);
    }

    @Override
    public void deleteTaxFormIncomeTaxOtherSourcesOtherInflowByTaxformAndNotIn(Taxform taxform, List<Integer> otherInflowsUpdatedRecords) throws Exception {
        taxformIncomeTaxOtherSourcesOtherInFlowRepository.deleteAllByTaxformAndIdNotIn(taxform, otherInflowsUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementOtherReceivablesOrAssetsByTaxformAndNotIn(Taxform taxform, List<Integer> otherAssetsUpdatedRecords) throws Exception {
        taxformWelthStatementOtherReceivablesOrAssetsRepository.deleteAllByTaxformAndIdNotIn(taxform, otherAssetsUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementOwnVehicleByTaxformAndNotIn(Taxform taxform, List<Integer> OwnVehicleUpdatedRecords) throws Exception {
        taxformWelthStatementOwnVehicleRepository.deleteAllByTaxformAndIdNotIn(taxform, OwnVehicleUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementOtherPossessionsByTaxformAndNotIn(Taxform taxform, List<Integer> otherPossessionsUpdatedRecords) throws Exception {
        taxformWelthStatementOtherPossessionsRepository.deleteAllByTaxformAndIdNotIn(taxform, otherPossessionsUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementAssetsOutSidePakistanByTaxformAndNotIn(Taxform taxform, List<Integer> assetsOutsidePakistanUpdatedRecords) throws Exception {
        taxformWelthStatementAssetsOutSidePakistanRepository.deleteAllByTaxformAndIdNotIn(taxform, assetsOutsidePakistanUpdatedRecords);
    }

    @Override
    public void deleteTaxformWelthStatementOweAnyLoansOrCreditByTaxformAndNotIn(Taxform taxform, List<Integer> oweAnyLoansOrCreditUpdatedRecord) throws Exception {
        taxformWelthStatementOweAnyLoansOrCreditRepository.deleteAllByTaxformAndIdNotIn(taxform, oweAnyLoansOrCreditUpdatedRecord);
    }

    @Override
    public void deleteTaxformWelthStatementBankAccountsOrInvestmentsByTaxformAndNotIn(Taxform taxform, List<Integer> bankAccountsOrInvestmentUpdatedRecord) throws Exception {
        taxformWelthStatementBankAccountsOrInvestmentsRepository.deleteAllByTaxformAndIdNotIn(taxform, bankAccountsOrInvestmentUpdatedRecord);
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedBankingTransactionByTaxformAndNotIn(Taxform taxform, List<Integer> deductedCollectedBankingTransactionUpdatedRecord) throws Exception {
        taxformTaxDeductedCollectedBankingTransactionRepository.deleteAllByTaxformAndIdNotIn(taxform, deductedCollectedBankingTransactionUpdatedRecord);
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedWithholdTaxVehicleByTaxformAndNotIn(Taxform taxform, List<Integer> withholdTaxVehicleUpdatedRecord) throws Exception {
        taxformTaxDeductedCollectedWithholdTaxVehicleRepository.deleteAllByTaxformAndIdNotIn(taxform, withholdTaxVehicleUpdatedRecord);
    }

    @Override
    public void deleteTaxformTaxDeductedCollectedUtilitiesByTaxformAndNotIn(Taxform taxform, List<Integer> utilitiesUpdatedRecord) throws Exception {
        taxformTaxDeductedCollectedUtilitiesRepository.deleteAllByTaxformAndIdNotIn(taxform, utilitiesUpdatedRecord);
    }
}
