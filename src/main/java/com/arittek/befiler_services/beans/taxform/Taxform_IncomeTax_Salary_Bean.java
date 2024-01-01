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
public class Taxform_IncomeTax_Salary_Bean implements Serializable {
    private String basicSalary;

    private Boolean tadaCheck;
    private String tada;

    private Boolean medicalAllowanceCheck;
    private String medicalAllowance;

    private Boolean providentFundByEmployeerCheck;
    private String providentFundByEmployeer;

    private Boolean otherAllowanceCheck;
    private String otherAllownace;

    private Boolean companyVehcileProvidedCheck;
    private String companyVehicleCost;

    private Boolean companyVehicleReceivedAfterJulyCheck;
    private String companyVehicleReceivedDate;

    private Boolean providentOrGratuityFundReceivedCheck;
    private String providentOrGratuityFundReceived;

    private Boolean salaryTaxBorneByEmployeerCheck;

    private String salaryTaxWithheldByEmployeer;

}