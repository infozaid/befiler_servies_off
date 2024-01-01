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
public class Taxform_DeductibleAllowanceOrCredit_Bean implements Serializable {

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



}
