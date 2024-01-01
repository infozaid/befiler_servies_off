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
public class Taxform_WealthStatement_PersonalExpenses_Bean implements Serializable {
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
