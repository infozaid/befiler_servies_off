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
public class TaxformWealthStatementOweAnyLoanOrCreditBean implements Serializable {

    private Integer id;
    private String form;
    private String creatorsNtnOrCnic;
    private String creatorsName;
    private String valueAtCost;

}
