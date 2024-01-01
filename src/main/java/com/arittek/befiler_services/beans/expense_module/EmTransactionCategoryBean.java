package com.arittek.befiler_services.beans.expense_module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EmTransactionCategoryBean {

    private Integer id;
    private String category;
    private String description;

    private Boolean showTaxDeducted;

    private List<EmTransactionCategoryBean> subTransactionCategoryList;
}
