package com.arittek.befiler_services.controller.expense_module;

import com.arittek.befiler_services.beans.DocumentsBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.expense_module.EmTransactionAccountBean;
import com.arittek.befiler_services.beans.expense_module.EmTransactionBean;
import com.arittek.befiler_services.model.expense_module.EmDocuments;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.model.expense_module.EmTransactionAccount;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.expense_module.EmTransactionAccountServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/em/transaction/account")
public class EmTransactionAccountController {

    private UsersServices usersServices;
    private EmTransactionAccountServices transactionAccountServices;

    @Autowired
    public EmTransactionAccountController(UsersServices usersServices, EmTransactionAccountServices transactionAccountServices) {
        this.usersServices = usersServices;
	this.transactionAccountServices = transactionAccountServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getTransactionAccounts() {
	try {
	    User user = usersServices.getUserFromToken();

	    List<EmTransactionAccount> mainAccountList = transactionAccountServices.findAllParentActiveAccounts();
	    if (mainAccountList != null && mainAccountList.size() > 0) {
		List<EmTransactionAccountBean> mainAccountBeanList = new ArrayList<>();
		for (EmTransactionAccount mainAccount : mainAccountList) {
		    EmTransactionAccountBean mainAccountBean = new EmTransactionAccountBean();

		    mainAccountBean.setId(mainAccount.getId());
		    mainAccountBean.setType(mainAccount.getType());
		    mainAccountBean.setDescription(mainAccount.getDescription());
		    mainAccountBean.setCanAddAccount(mainAccount.getCanAddAccount());

		    List<EmTransactionAccount> subAccountList = transactionAccountServices.findAllSubActiveAccountsByUser(user, mainAccount);
		    if (subAccountList != null && subAccountList.size() > 0) {
			List<EmTransactionAccountBean> subAccountBeanList = new ArrayList<>();
			for (EmTransactionAccount subAccount : subAccountList) {
			    EmTransactionAccountBean subAccountBean = new EmTransactionAccountBean();

			    subAccountBean.setId(subAccount.getId());
			    subAccountBean.setType(subAccount.getType());
			    subAccountBean.setDescription(subAccount.getDescription());

			    subAccountBeanList.add(subAccountBean);
			}
			mainAccountBean.setSubAccountList(subAccountBeanList);
		    }

		    mainAccountBeanList.add(mainAccountBean);
		}
		StatusBean statusBean = new StatusBean(1, "Successfull");
		statusBean.setResponse(mainAccountBeanList);
		return new ResponseEntity<>(statusBean, HttpStatus.OK);
	    }
	    return new ResponseEntity<>(new StatusBean(1, "No record found"), HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : " , e);
	    return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
	}
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> saveTransactionAccount(@RequestBody EmTransactionAccountBean transactionAccountBean) {
	try {
	    User user = usersServices.getUserFromToken();
	    if (user == null) {
		return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
	    }

	    if (!StringUtils.isNotEmpty(transactionAccountBean.getType()))
		return new ResponseEntity<>(new StatusBean(0, "Please input account title"), HttpStatus.OK);

	    EmTransactionAccount parentTransactionAccount;
	    if (transactionAccountBean.getParentTransactionAccountId() != null) {
		parentTransactionAccount = transactionAccountServices.findOneActiveAccountById(transactionAccountBean.getParentTransactionAccountId());
		if (parentTransactionAccount == null) {
		    return new ResponseEntity<>(new StatusBean(0, "Please select parent account"), HttpStatus.OK);
		}
		if (!parentTransactionAccount.getCanAddAccount())
		    return new ResponseEntity<>(new StatusBean(0, "You don't have permission to create account"), HttpStatus.OK);
	    } else
		return new ResponseEntity<>(new StatusBean(0, "Please select income category"), HttpStatus.OK);

	    EmTransactionAccount transactionAccount;
	    if (transactionAccountBean.getId() != null) {
		transactionAccount = transactionAccountServices.findOneActiveAccountById(transactionAccountBean.getId());
		if (transactionAccount == null) {
		    transactionAccount = new EmTransactionAccount();
		}
	    } else {
		transactionAccount = new EmTransactionAccount();
	    }

	    transactionAccount.setUser(user);
	    transactionAccount.setAccount(parentTransactionAccount);

	    transactionAccount.setType(transactionAccountBean.getType());
	    transactionAccount.setDescription(transactionAccountBean.getDescription());

	    transactionAccountServices.save(transactionAccount);

	    return new ResponseEntity<>(new StatusBean(1, "Record added successfully"), HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : " , e);
	    return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
	}
    }
}
