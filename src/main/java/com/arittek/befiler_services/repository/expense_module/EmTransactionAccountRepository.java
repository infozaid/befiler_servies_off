package com.arittek.befiler_services.repository.expense_module;

import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AssignType;
import com.arittek.befiler_services.model.expense_module.EmTransactionAccount;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmTransactionAccountRepository extends JpaRepository<EmTransactionAccount, Integer>, RevisionRepository<EmTransactionAccount, Integer, Integer> {

    EmTransactionAccount findOneByIdAndStatus(Integer transactionPaymentTypeId, AppStatus status);

    List<EmTransactionAccount> findAllByStatusAndAccountIsNull(AppStatus status);

    @Query("SELECT account FROM EmTransactionAccount account " +
            "WHERE account.status = :status " +
            "AND account.account = :account " +
            "AND account.user = :user " +
            "OR account.user IS NULL ")
    List<EmTransactionAccount> findAllByStatusAndTransactionAccountAndUser(@Param("status") AppStatus status, @Param("account") EmTransactionAccount account, @Param("user") User user);

   //List<EmTransactionAccount> findAllByStatusAndAccount(AppStatus appStatus, EmTransactionAccount emTransactionAccount);
//
}
