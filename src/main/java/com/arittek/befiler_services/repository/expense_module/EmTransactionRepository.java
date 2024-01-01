package com.arittek.befiler_services.repository.expense_module;

import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface EmTransactionRepository extends JpaRepository<EmTransaction, Integer>, RevisionRepository<EmTransaction, Integer, Integer> {

    List<EmTransaction> findAllByUserOrderByIdDesc(User user);
    List<EmTransaction> findAllByUserAndTransactionCategoryOrderByIdDesc(User user, EmTransactionCategory emTransactionCategory);

    List<EmTransaction> findAllByUserAndTransactionTypeOrderByIdDesc(User user, EmTransactionType transactionType);
    List<EmTransaction>findAllByUserAndTransactionCategoryAndTransactionType(User user,EmTransactionCategory emTransactionCategory,EmTransactionType emTransactionType);

    //List<EmTransaction>findAllByUserAndTransactionCategoryAndTransactionType(User user,EmTransactionCategory emTransactionCategory,EmTransactionType emTransactionType);

    @Query("SELECT transaction FROM EmTransaction transaction " +
            "WHERE transaction.user = :user AND transaction.date BETWEEN :startDate AND :endDate " +
            "ORDER BY transaction.id DESC")

    List<EmTransaction> findAllTransactionsByUserBetweenFromAndToDateAndOrderByIdDesc(@Param("user") User user, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
//    <==By Maqsood==>
    @Query("SELECT transaction FROM EmTransaction transaction " +
            "WHERE transaction.user = :user AND transaction.date BETWEEN :startDate AND :endDate " +
            "ORDER BY transaction.date DESC")
    List<EmTransaction> findAllTransactionsByUserBetweenFromAndToDateAndOrderByDateDesc(@Param("user") User user, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
//    <==By Maqsood==>

   // List<EmTransaction> findAllTransactionsByUserAndTransactionTypeBetweenFromAndToDateAndOrderByIdDesc(@Param("user") @Param("") User user,EmTransactionType emTransactionType,@Param("startDate") Date startDate, @Param("endDate")Date endDate);

//    @Query("SELECT DISTINCT(DATE) FROM EmTransaction")
//    public List<Date> findAllUniqueDates();   //<==By Maqsood==>

    public List<EmTransaction> findAllByDate(Date date); //<==By Maqsood==>
}