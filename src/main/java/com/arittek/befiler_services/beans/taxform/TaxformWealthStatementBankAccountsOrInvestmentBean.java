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
public class TaxformWealthStatementBankAccountsOrInvestmentBean implements Serializable {
    private Integer id;
    private String form;
    private String accountOrInstructionNo;
    private String institutionNameOrInduvidualCnic;
    private String branchName;
    private String cost;


}
