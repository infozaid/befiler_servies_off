package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.TermsAndConditions;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import lombok.*;
import org.hibernate.envers.Audited;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "taxform")
public class Taxform extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_status_id")
    private Taxform_Status status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "taxform_year_id")
    private TaxformYears taxformYear;

    @Column(name = "tracking_no")
    private String trackingNo;

    @Column(name = "cnic")
    private String cnic;

    @Column(name = "name_as_per_cnic")
    private String nameAsPerCnic;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "country")
    private String country;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "residence_status")
    private String residenceStatus;

    @Column(name = "residence_address")
    private String residenceAddress;

    @Column(name = "stay_in_pakistan_because_of_employment")
    private Boolean stayInPakistanBecauseOfEmployement;

    @Column(name = "stay_in_pakistan_more_than_3_years  ")
    private Boolean stayInPakistanMoreThan3Years;

    @Column(name = "current_screen  ")
    private Integer currentScreen;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_and_conditions_id")
    private TermsAndConditions termsAndConditions;

    @Column(name = "curr_date")
    private Timestamp currentDate;

    @Column(name = "verify_corporate_employee")
    private Integer verifyCorporateEmployee;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizer_id")
    private User authorizer;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Payment> paymentList = new ArrayList<>();
    /*===============TAX FORM DETAIL INFORMATION TABLES===============*/

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_Salary taxformIncomeTaxSalary;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_Property taxformIncomeTaxProperty;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_CapitalGain_OnShare taxformIncomeTaxCapitalGainOnShare;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_CapitalGain_MutualFinds taxformIncomeTaxCapitalGainMutualFinds;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_CapitalGain_Property taxformIncomeTaxCapitalGainProperty;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_IncomeTax_OtherSources taxformIncomeTaxOtherSources;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit> taxformIncomeTaxOtherSourcesProfitOnBankDepositList = new ArrayList<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<TaxForm_IncomeTax_OtherSources_OtherInflow> taxformIncomeTaxOtherSourcesOtherInFlowsList = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_DeductibleAllowanceOrCredit taxformDeductibleAllowanceOrCredit;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_TaxDeductedCollected_BankingTransaction> taxformTaxDeductedCollectedBankingTransactionList = new ArrayList<Taxform_TaxDeductedCollected_BankingTransaction>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_TaxDeductedCollected_WithholdTaxVehicle> taxformTaxDeductedCollectedWithholdTaxVehicleList = new ArrayList<Taxform_TaxDeductedCollected_WithholdTaxVehicle>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_TaxDeductedCollected_Utilities> taxformTaxDeductedCollectedUtilitiesList = new ArrayList<Taxform_TaxDeductedCollected_Utilities>();

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_TaxDeductedCollected_Other taxformTaxDeductedCollectedOther;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_WelthStatement taxformWelthStatement;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "taxform")
    private Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementDetailsOfPersonalExpense;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_PropertyDetail> taxformWelthStatementPropertyDetailList = new ArrayList<Taxform_WelthStatement_PropertyDetail>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_BankAccountsOrInvestments> taxformWelthStatementBankAccountsOrInvestmentsList = new ArrayList<Taxform_WelthStatement_BankAccountsOrInvestments>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_OtherReceivablesOrAssets> taxformWelthStatementOtherReceivablesOrAssetsList = new ArrayList<Taxform_WelthStatement_OtherReceivablesOrAssets>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_OwnVehicle> taxformWelthStatementOwnVehicleList = new ArrayList<Taxform_WelthStatement_OwnVehicle>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_OtherPossessions> taxformWelthStatementOtherPossessionsList = new ArrayList<Taxform_WelthStatement_OtherPossessions>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_AssetsOutSidePakistan> taxformWelthStatementAssetsOutSidePakistanList = new ArrayList<Taxform_WelthStatement_AssetsOutSidePakistan>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<Taxform_WelthStatement_OweAnyLoansOrCredit> taxformWelthStatementOweAnyLoansOrCreditList = new ArrayList<Taxform_WelthStatement_OweAnyLoansOrCredit>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taxform")
    private List<TaxformDocuments> taxformDocumentsList = new ArrayList<TaxformDocuments>();

}
