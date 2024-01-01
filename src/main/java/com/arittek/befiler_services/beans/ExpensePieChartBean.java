package com.arittek.befiler_services.beans;

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
public class ExpensePieChartBean implements Serializable {

    private Integer code;
    private String message;

    private Double rentPercentage;
    private Double electricityPercentage;
    private Double waterPercentage;
    private Double gasPercentage;
    private Double telephonePercentage;
    private Double medicalPercentage;
    private Double educationalPercentage;
    private Double otherExpensesPercentage;

    public ExpensePieChartBean(Integer code, String message){
        this.code  = code;
        this.message = message;
    }


    /*CONSTRUCTORS*/

}
