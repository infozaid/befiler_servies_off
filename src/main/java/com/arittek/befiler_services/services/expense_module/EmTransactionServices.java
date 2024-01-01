package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.model.user.User;

import java.util.Date;
import java.util.List;

public interface EmTransactionServices {

    EmTransaction saveTransaction(EmTransaction transaction) throws Exception;


    EmTransaction findOneById(Integer transactionId) throws Exception;

    List<EmTransaction> findAllTransactionsByUser(User user) throws Exception;
    List<EmTransaction> findAllIncomeTransactionsByUser(User user) throws Exception;
    List<EmTransaction> findAllExpenseTransactionsByUser(User user) throws Exception;
    List<EmTransaction> findAllTransactionsByUserAndBetweenTwoDates(User user, Date fromDate, Date toDate) throws Exception;
    List<EmTransaction> findAllTransactionsByUserAndBetweenTwoDatesOrderByDate(User user, Date fromDate, Date toDate) throws Exception;

    void deleteTransaction(EmTransaction transaction) throws Exception;
    List<EmTransaction> findAllTransactionsByUserAndTransactionCategory(User user, EmTransactionCategory emTransactionCategory) throws Exception;
    List<EmTransaction> findAllTransactionsByUserAndTransactionCategoryAndTranscationType(User user, EmTransactionCategory emTransactionCategory, EmTransactionType emTransactionType) throws Exception;
    /*List<EmTransaction> findAllTransactionsByUserAndTransactionType(User user) throws Exception;*/
//    List<Date> findAllUniqueDate();
    List<EmTransaction> findAllByDate(Date date);
}
