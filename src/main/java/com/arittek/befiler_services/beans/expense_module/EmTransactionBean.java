package com.arittek.befiler_services.beans.expense_module;

import com.arittek.befiler_services.beans.DocumentsBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class EmTransactionBean {

    private Integer id;

    private String amount;
    private String taxDeducted;
    private String date;
    private String description;

    private Integer transactionTypeId;
    private String transactionType;

    private Integer transactionCategoryId;
    private String transactionCategory;

    private Integer transactionAccountId;
    private String transactionAccount;

    private List<EmTransactionBean> emSubListByCategory;

    private List<DocumentsBean> documentsBeanList;


}
