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
public class TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean implements Serializable {
    private Integer id;
    private String accountType;
    private String bankName;
    private String branch;
    private String accountNo;
    private String currency;
    private String profitAmount;
    private String taxDeducted;
}
