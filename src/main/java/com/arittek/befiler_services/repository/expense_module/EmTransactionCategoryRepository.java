package com.arittek.befiler_services.repository.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmTransactionCategoryRepository extends JpaRepository<EmTransactionCategory, Integer>, RevisionRepository<EmTransactionCategory, Integer, Integer> {

    EmTransactionCategory findOneByIdAndStatus(Integer transactionCategoryId, AppStatus appStatus);
    List<EmTransactionCategory> findAllByTransactionTypeAndStatusAndTransactionCategoryIsNull(EmTransactionType transactionType, AppStatus appStatus);
    List<EmTransactionCategory> findAllByTransactionTypeAndStatusAndTransactionCategory(EmTransactionType transactionType, AppStatus appStatus, EmTransactionCategory transactionCategory);

}
