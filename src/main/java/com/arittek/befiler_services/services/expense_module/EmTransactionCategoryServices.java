package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;

import java.util.List;

public interface EmTransactionCategoryServices {

    EmTransactionCategory findOneActiveCategoryById(Integer transactionCategoryId) throws Exception;

    List<EmTransactionCategory> findAllMainActiveIncomeCategories() throws Exception;
    List<EmTransactionCategory> findAllSubActiveIncomeCategories(EmTransactionCategory transactionCategory) throws Exception;

    List<EmTransactionCategory> findAllMainActiveExpenseCategories() throws Exception;
    List<EmTransactionCategory> findAllSubActiveExpenseCategories(EmTransactionCategory transactionCategory) throws Exception;
}
