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
public class EmTransactionReportBean {

    private String income;
    private String expense;
    private String balance;
    private String currentDate;
    private String fromDate;
    private String toDate;

    private List<EmTransactionBean> transactionBeanList;
}
