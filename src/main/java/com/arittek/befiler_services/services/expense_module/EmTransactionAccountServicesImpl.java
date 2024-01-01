package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.expense_module.EmTransactionAccount;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.expense_module.EmTransactionAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmTransactionAccountServicesImpl implements EmTransactionAccountServices {

    private EmTransactionAccountRepository transactionAccountRepository;

    @Autowired
    public EmTransactionAccountServicesImpl(EmTransactionAccountRepository transactionAccountRepository) {
	this.transactionAccountRepository = transactionAccountRepository;
    }

    @Override
    public EmTransactionAccount save(EmTransactionAccount transactionAccount) throws Exception {
        if (transactionAccount != null)
            return transactionAccountRepository.save(transactionAccount);
	return null;
    }

    @Override
    public EmTransactionAccount findOneActiveAccountById(Integer accountId) throws Exception {
        if (accountId != null) {
	    return transactionAccountRepository.findOneByIdAndStatus(accountId, AppStatus.ACTIVE);
	}
	return null;
    }

    @Override
    public List<EmTransactionAccount> findAllParentActiveAccounts() throws Exception {
	return transactionAccountRepository.findAllByStatusAndAccountIsNull(AppStatus.ACTIVE);
    }

    @Override
    public List<EmTransactionAccount> findAllSubActiveAccountsByUser(User user, EmTransactionAccount transactionAccount) throws Exception {
        if (user != null && transactionAccount != null)
            return transactionAccountRepository.findAllByStatusAndTransactionAccountAndUser(AppStatus.ACTIVE,transactionAccount,user);
	return null;
    }
}
