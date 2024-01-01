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
public class Taxform_IncomeTax_CapitalGain_OnShare_Bean implements Serializable {

    private Boolean lessThan12MonthsCheck;
    private String lessThan12MonthsFieldsCapitalGain;
    private String lessThan12MonthsTaxDeducted;

    private Boolean moreThan12ButLessThan24MonthsCheck;
    private String moreThan12ButLessThan24MonthsFieldsCapitalGain;
    private String moreThan12ButLessThan24MonthsTaxDeducted;

    private Boolean moreThan24MonthsButAquiredAfter1July2012Check;
    private String moreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain;
    private String moreThan24MonthsButAquiredAfter1July2012TaxDeducted;

    private Boolean aquiredBefore1July2012Check;
    private String aquiredBefore1July2012FieldsCapitalGain;
    private String aquiredBefore1July2012TaxDeducted;

    private Boolean acquiredOnOrAfter1JulyCheck;
    private String acquiredOnOrAfter1JulyCapitalGain;
    private String acquiredOnOrAfter1JulyTaxDeducted;
}
