package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class TaxformCalculationValuesBean {

    /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> EDUCATION ALLOWANCE========================*/
    private Double educationAllowanceShowCheckAmount;
    private Double educationAllowanceTaxableIncomePercent;

    /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> HEALTH INSURANCE PREMIUM========================*/
    private Double healthInsurancePremiumStaticAmount;

    /*========================INCOME TAX -> OTHER SOURCES -> Dividends========================*/
    List<Double> dividendPercentages;


}
