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
public class Taxform_IncomeTax_CapitalGain_Property_Bean implements Serializable {
    private Boolean before1JulyCheck;

    private Boolean before1JulyAndUpto3YearsCheck;
    private String before1JulyAndUpto3YearsPurchaseCost;
    private String before1JulyAndUpto3YearsSaleCost;
    private String before1JulyAndUpto3YearsLocation;

    private Boolean before1JulyAndMoreThan3YearsCheck;
    private String before1JulyAndMoreThan3YearsPurchaseCost;
    private String before1JulyAndMoreThan3YearsSaleCost;
    private String before1JulyAndMoreThan3YearsLocation;

    private Boolean after1JulyCheck;

    /*FOR TAX YEAR 2017*/
    private String after1JulyPurchaseCost;
    private String after1JulySaleCost;
    private String after1JulyLocation;

    /*FOR TAX YEAR 2018-2019*/
    private Boolean after1JulyUpto1YearCheck;
    private String after1JulyUpto1YearPurchaseCost;
    private String after1JulyUpto1YearSaleCost;
    private String after1JulyUpto1YearLocation;

    private Boolean after1July1To2YearsCheck;
    private String after1July1To2YearsPurchaseCost;
    private String after1July1To2YearsSaleCost;
    private String after1July1To2YearsLocation;

    private Boolean after1July2To3YearsCheck;
    private String after1July2To3YearsPurchaseCost;
    private String after1July2To3YearsSaleCost;
    private String after1July2To3YearsLocation;

    private Boolean after1JulyAndMoreThan3YearsCheck;
    private String after1JulyAndMoreThan3YearsPurchaseCost;
    private String after1JulyAndMoreThan3YearsSaleCost;
    private String after1JulyAndMoreThan3YearsLocation;

}
