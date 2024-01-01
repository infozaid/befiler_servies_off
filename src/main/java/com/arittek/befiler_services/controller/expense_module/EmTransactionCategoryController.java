package com.arittek.befiler_services.controller.expense_module;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.expense_module.EmTransactionCategoryBean;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.services.expense_module.EmTransactionCategoryServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/em/transaction/category")
public class EmTransactionCategoryController {

    private EmTransactionCategoryServices transactionCategoryServices;

    @Autowired
    public EmTransactionCategoryController(EmTransactionCategoryServices transactionCategoryServices) {
	this.transactionCategoryServices = transactionCategoryServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllTransactionCategories() {
	try {

	    Map<String, List<EmTransactionCategoryBean>> responseMap = new HashMap<>();

	    List<EmTransactionCategory> mainIncomeCategoryList = transactionCategoryServices.findAllMainActiveIncomeCategories();

	    List<EmTransactionCategoryBean> mainIncomeCategoryBeanList = new ArrayList<>();

	    if (mainIncomeCategoryList != null && mainIncomeCategoryList.size() > 0) {
		for (EmTransactionCategory incomeCategory : mainIncomeCategoryList) {

		    EmTransactionCategoryBean incomeCategoryBean = new EmTransactionCategoryBean();
			incomeCategoryBean.setId(incomeCategory.getId());
		    incomeCategoryBean.setCategory(incomeCategory.getCategory());
		    incomeCategoryBean.setDescription(incomeCategory.getDescription());
		    incomeCategoryBean.setShowTaxDeducted(incomeCategory.getShowTaxDeducted());

		    List<EmTransactionCategory> subIncomeCategoryList = transactionCategoryServices.findAllSubActiveIncomeCategories(incomeCategory);

		     if (subIncomeCategoryList != null && subIncomeCategoryList.size() > 0) {
			List<EmTransactionCategoryBean> subIncomeCategoryBeanList = new ArrayList<>();
			for (EmTransactionCategory subIncomeCategory : subIncomeCategoryList) {
			    EmTransactionCategoryBean subIncomeCategoryBean = new EmTransactionCategoryBean();

			    subIncomeCategoryBean.setId(subIncomeCategory.getId());
			    subIncomeCategoryBean.setCategory(subIncomeCategory.getCategory());
			    subIncomeCategoryBean.setDescription(subIncomeCategory.getDescription());

			    subIncomeCategoryBean.setShowTaxDeducted(subIncomeCategory.getShowTaxDeducted());

			    subIncomeCategoryBeanList.add(subIncomeCategoryBean);
			}

			incomeCategoryBean.setSubTransactionCategoryList(subIncomeCategoryBeanList);
		    }
		    mainIncomeCategoryBeanList.add(incomeCategoryBean);
		}
	    }
	    responseMap.put("INCOME", mainIncomeCategoryBeanList);

	    List<EmTransactionCategory> mainExpenseCategoryList = transactionCategoryServices.findAllMainActiveExpenseCategories();
	    List<EmTransactionCategoryBean> mainExpenseCategoryBeanList = new ArrayList<>();
	    if (mainExpenseCategoryList != null && mainExpenseCategoryList.size() > 0) {
		for (EmTransactionCategory expenseCategory : mainExpenseCategoryList) {
		    EmTransactionCategoryBean expenseCategoryBean = new EmTransactionCategoryBean();

		    expenseCategoryBean.setId(expenseCategory.getId());
		    expenseCategoryBean.setCategory(expenseCategory.getCategory());
		    expenseCategoryBean.setDescription(expenseCategory.getDescription());
		    expenseCategoryBean.setShowTaxDeducted(expenseCategory.getShowTaxDeducted());

		    List<EmTransactionCategory> subExpenseCategoryList = transactionCategoryServices.findAllSubActiveExpenseCategories(expenseCategory);
		    if (subExpenseCategoryList != null && subExpenseCategoryList.size() > 0) {
			List<EmTransactionCategoryBean> subExpenseCategoryBeanList = new ArrayList<>();
			for (EmTransactionCategory subExpenseCategory : subExpenseCategoryList) {
			    EmTransactionCategoryBean subExpenseCategoryBean = new EmTransactionCategoryBean();

			    subExpenseCategoryBean.setId(subExpenseCategory.getId());
			    subExpenseCategoryBean.setCategory(subExpenseCategory.getCategory());
			    subExpenseCategoryBean.setDescription(subExpenseCategory.getDescription());
			    subExpenseCategoryBean.setShowTaxDeducted(subExpenseCategory.getShowTaxDeducted());

			    subExpenseCategoryBeanList.add(subExpenseCategoryBean);
			}
			expenseCategoryBean.setSubTransactionCategoryList(subExpenseCategoryBeanList);
		    }

		    mainExpenseCategoryBeanList.add(expenseCategoryBean);
		}
	    }
	    responseMap.put("EXPENSE", mainExpenseCategoryBeanList);

	    StatusBean statusBean = new StatusBean(1, "Successfull");
	    statusBean.setResponseMap(responseMap);
	    return new ResponseEntity<>(statusBean, HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : " , e);
	    return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
	}
    }
}
