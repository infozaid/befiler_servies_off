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
public class EmTransactionAccountBean {
    private Integer id;
    private String type;
    private String description;

    private Boolean canAddAccount;

    private Integer parentTransactionAccountId;

    private List<EmTransactionAccountBean> subAccountList;
}
