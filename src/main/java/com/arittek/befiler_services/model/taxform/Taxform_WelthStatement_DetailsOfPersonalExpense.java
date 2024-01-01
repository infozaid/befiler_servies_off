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
@Table(name = "taxform_welth_statement_details_of_personal_expense")
public class Taxform_WelthStatement_DetailsOfPersonalExpense extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "rent")
    private Double rent;

    @Column(name = "rates_taxes_charge_cess")
    private Double ratesTaxesChargeCess;

    @Column(name = "vehicle_running_or_maintenance")
    private Double vehicleRunningOrMaintenance;

    @Column(name = "travelling")
    private Double travelling;

    @Column(name = "electricity")
    private Double electricity;

    @Column(name = "water")
    private Double water;

    @Column(name = "gas")
    private Double gas;

    @Column(name = "telephone")
    private Double telephone;

    @Column(name = "assets_insurance_or_security")
    private Double assetsInsuranceOrSecurity;

    @Column(name = "medical")
    private Double medical;

    @Column(name = "educational")
    private Double educational;

    @Column(name = "club")
    private Double club;

    @Column(name = "functions_or_gathering")
    private Double functionsOrGatherings;

    @Column(name = "donation_zakat_annuity_profit_on_debt_etc")
    private Double donationZakatAnnuityProfitOnDebutEtc;

    @Column(name = "other_personal_or_household_expense")
    private Double otherPersonalOrHouseholdExpense;

    @Column(name = "gift")
    private Double gift;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;
}