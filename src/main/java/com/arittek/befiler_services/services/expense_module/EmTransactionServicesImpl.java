package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.expense_module.EmTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EmTransactionServicesImpl implements EmTransactionServices {

    private EmTransactionRepository transactionRepository;

    @Autowired
    public EmTransactionServicesImpl(EmTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public EmTransaction saveTransaction(EmTransaction transaction) throws Exception {
        if (transaction != null)
            return transactionRepository.save(transaction);
        return null;
    }

    @Override
    public EmTransaction findOneById(Integer transactionId) throws Exception {
        if (transactionId != null) {
            return transactionRepository.findById(transactionId).orElse(null);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllTransactionsByUser(User user) throws Exception {
        if (user != null) {
            return transactionRepository.findAllByUserOrderByIdDesc(user);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllTransactionsByUserAndTransactionCategory(User user, EmTransactionCategory emTransactionCategory) throws Exception {
        if (user != null && emTransactionCategory != null) {
            return transactionRepository.findAllByUserAndTransactionCategoryOrderByIdDesc(user, emTransactionCategory);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllTransactionsByUserAndTransactionCategoryAndTranscationType(User user, EmTransactionCategory emTransactionCategory, EmTransactionType emTransactionType) throws Exception {
        if (user != null) {
            return transactionRepository.findAllByUserAndTransactionCategoryAndTransactionType(user, emTransactionCategory, emTransactionType);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllIncomeTransactionsByUser(User user) throws Exception {
        if (user != null) {
            return transactionRepository.findAllByUserAndTransactionTypeOrderByIdDesc(user, EmTransactionType.INCOME);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllExpenseTransactionsByUser(User user) throws Exception {
        if (user != null) {
            return transactionRepository.findAllByUserAndTransactionTypeOrderByIdDesc(user, EmTransactionType.EXPENSE);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllTransactionsByUserAndBetweenTwoDates(User user, Date fromDate, Date toDate) throws Exception {
        if (user != null && fromDate != null && toDate != null) {
            return transactionRepository.findAllTransactionsByUserBetweenFromAndToDateAndOrderByIdDesc(user, fromDate, toDate);
        }
        return null;
    }

    @Override
    public List<EmTransaction> findAllTransactionsByUserAndBetweenTwoDatesOrderByDate(User user, Date fromDate, Date toDate) throws Exception {
        if (user != null && fromDate != null && toDate != null) {
            return transactionRepository.findAllTransactionsByUserBetweenFromAndToDateAndOrderByDateDesc(user, fromDate, toDate);
        }
        return null;
    }


    @Override
    public void deleteTransaction(EmTransaction transaction) throws Exception {
        if (transaction != null)
            transactionRepository.deleteById(transaction.getId());
    }

//    @Override
//    public List<Date> findAllUniqueDate(){
//        return transactionRepository.findAllUniqueDates();
//    }

    @Override
    public List<EmTransaction> findAllByDate(Date date) {
        return transactionRepository.findAllByDate(date);
    }
}
