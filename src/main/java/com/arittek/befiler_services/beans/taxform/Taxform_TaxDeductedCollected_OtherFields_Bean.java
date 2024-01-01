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
public class Taxform_TaxDeductedCollected_OtherFields_Bean implements Serializable {
/*

    private Boolean propertyPurchaseOrSaleTaxDeductedCheck;
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

    /*GETTERS AND SETTERS*/
/*
    public Boolean getPropertyPurchaseOrSaleTaxDeductedCheck() {
        return propertyPurchaseOrSaleTaxDeductedCheck;
    }

    public void setPropertyPurchaseOrSaleTaxDeductedCheck(Boolean propertyPurchaseOrSaleTaxDeductedCheck) {
        this.propertyPurchaseOrSaleTaxDeductedCheck = propertyPurchaseOrSaleTaxDeductedCheck;
    }

    public String getPropertyPurchaseOrSaleTaxDeducted() {
        return propertyPurchaseOrSaleTaxDeducted;
    }

    public void setPropertyPurchaseOrSaleTaxDeducted(String propertyPurchaseOrSaleTaxDeducted) {
        this.propertyPurchaseOrSaleTaxDeducted = propertyPurchaseOrSaleTaxDeducted;
    }*/


}
