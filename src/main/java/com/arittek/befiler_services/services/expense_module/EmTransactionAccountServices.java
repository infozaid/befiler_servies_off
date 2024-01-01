package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.expense_module.EmTransactionAccount;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface EmTransactionAccountServices {

    EmTransactionAccount save(EmTransactionAccount transactionAccount) throws Exception;

    EmTransactionAccount findOneActiveAccountById(Integer accountId) throws Exception;

    List<EmTransactionAccount> findAllParentActiveAccounts() throws Exception;
    List<EmTransactionAccount> findAllSubActiveAccountsByUser(User user, EmTransactionAccount transactionAccount) throws Exception;

}
