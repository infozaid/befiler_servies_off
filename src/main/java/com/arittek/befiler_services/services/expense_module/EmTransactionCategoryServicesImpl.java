package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.repository.expense_module.EmTransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmTransactionCategoryServicesImpl implements EmTransactionCategoryServices {

    private EmTransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    public EmTransactionCategoryServicesImpl(EmTransactionCategoryRepository transactionCategoryRepository) {
	this.transactionCategoryRepository = transactionCategoryRepository;
    }

    @Override
    public EmTransactionCategory findOneActiveCategoryById(Integer transactionCategoryId) throws Exception {
        if (transactionCategoryId != null) {
            return transactionCategoryRepository.findOneByIdAndStatus(transactionCategoryId, AppStatus.ACTIVE);
	}
	return null;
    }

    @Override
    public List<EmTransactionCategory> findAllMainActiveIncomeCategories() throws Exception {
	return transactionCategoryRepository.findAllByTransactionTypeAndStatusAndTransactionCategoryIsNull(EmTransactionType.INCOME, AppStatus.ACTIVE);
    }

    @Override
    public List<EmTransactionCategory> findAllSubActiveIncomeCategories(EmTransactionCategory transactionCategory) throws Exception {
        if (transactionCategory != null)
	    return transactionCategoryRepository.findAllByTransactionTypeAndStatusAndTransactionCategory(EmTransactionType.INCOME, AppStatus.ACTIVE, transactionCategory);
	return null;
    }

    @Override
    public List<EmTransactionCategory> findAllMainActiveExpenseCategories() throws Exception {
	return transactionCategoryRepository.findAllByTransactionTypeAndStatusAndTransactionCategoryIsNull(EmTransactionType.EXPENSE, AppStatus.ACTIVE);
    }

    @Override
    public List<EmTransactionCategory> findAllSubActiveExpenseCategories(EmTransactionCategory transactionCategory) throws Exception {
	if (transactionCategory != null)
	    return transactionCategoryRepository.findAllByTransactionTypeAndStatusAndTransactionCategory(EmTransactionType.EXPENSE, AppStatus.ACTIVE, transactionCategory);
	return null;
    }
}
