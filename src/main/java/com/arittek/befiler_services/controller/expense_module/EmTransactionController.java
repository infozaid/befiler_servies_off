package com.arittek.befiler_services.controller.expense_module;

import com.arittek.befiler_services.beans.DocumentsBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.expense_module.EmTransactionBean;
import com.arittek.befiler_services.beans.expense_module.EmTransactionReportBean;
import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmDocuments;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.model.expense_module.EmTransactionAccount;
import com.arittek.befiler_services.model.expense_module.EmTransactionCategory;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.expense_module.EmDocumentsServices;
import com.arittek.befiler_services.services.expense_module.EmTransactionAccountServices;
import com.arittek.befiler_services.services.expense_module.EmTransactionCategoryServices;
import com.arittek.befiler_services.services.expense_module.EmTransactionServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Constants;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MyPrint;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.*;

@RestController
@RequestMapping(value = "/em/transaction")
public class EmTransactionController {

    private UsersServices usersServices;
    private EmDocumentsServices documentsServices;
    private EmTransactionServices transactionServices;
    private EmTransactionAccountServices transactionAccountServices;
    private EmTransactionCategoryServices transactionCategoryServices;

    @Value("${befiler.url}")
    private String serverUrl;

    @Value("${static.content.path}")
    private String staticContentPath;

    @Value("${static.content.em.transaction}")
    private String staticContentTransactionPath;

    @Autowired
    public EmTransactionController(UsersServices usersServices, EmDocumentsServices documentsServices, EmTransactionServices transactionServices, EmTransactionAccountServices transactionAccountServices, EmTransactionCategoryServices transactionCategoryServices) {
        this.usersServices = usersServices;
        this.documentsServices = documentsServices;
        this.transactionServices = transactionServices;
        this.transactionAccountServices = transactionAccountServices;
        this.transactionCategoryServices = transactionCategoryServices;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> saveTransaction(@RequestBody EmTransactionBean transactionBean) {
        try {
            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
            }

            if (!StringUtils.isNotEmpty(transactionBean.getAmount()))
                return new ResponseEntity<>(new StatusBean(0, "Please input amount"), HttpStatus.OK);
            if (!StringUtils.isNotEmpty(transactionBean.getDate()))
                return new ResponseEntity<>(new StatusBean(0, "Please input date"), HttpStatus.OK);

            EmTransactionCategory transactionCategory;
            if (transactionBean.getTransactionCategoryId() != null) {
                transactionCategory = transactionCategoryServices.findOneActiveCategoryById(transactionBean.getTransactionCategoryId());
                if (transactionCategory == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Please select income category"), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(new StatusBean(0, "Please select income category"), HttpStatus.OK);

            if (transactionCategory.getShowTaxDeducted()) {
                if (!StringUtils.isNotEmpty(transactionBean.getTaxDeducted()))
                    return new ResponseEntity<>(new StatusBean(0, "Please input tax deducted"), HttpStatus.OK);
            }

            EmTransactionAccount transactionAccount;
            if (transactionBean.getTransactionAccountId() != null) {
                transactionAccount = transactionAccountServices.findOneActiveAccountById(transactionBean.getTransactionAccountId());
                if (transactionAccount == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Please select account"), HttpStatus.OK);
                }
            } else
                return new ResponseEntity<>(new StatusBean(0, "Please account"), HttpStatus.OK);

            EmTransaction transaction;
            if (transactionBean.getId() != null) {
                transaction = transactionServices.findOneById(transactionBean.getId());
                if (transaction == null) {
                    transaction = new EmTransaction();
                }
            } else {
                transaction = new EmTransaction();
            }

            transaction.setUser(user);

            transaction.setAmount(Double.parseDouble(transactionBean.getAmount()));
            if (StringUtils.isNotEmpty(transactionBean.getTaxDeducted()))
                transaction.setTaxDeducted(Double.parseDouble(transactionBean.getTaxDeducted()));
            transaction.setDate(CommonUtil.changeDateStringToDate(transactionBean.getDate()));
            transaction.setDescription(transactionBean.getDescription());

            transaction.setTransactionCategory(transactionCategory);
            transaction.setTransactionAccount(transactionAccount);

            transaction.setTransactionType(transactionCategory.getTransactionType());

            transactionServices.saveTransaction(transaction);

            List<Integer> documentsUpdatedList = new ArrayList<>();

            if (transactionBean.getDocumentsBeanList() != null && transactionBean.getDocumentsBeanList().size() > 0) {

                for (DocumentsBean documentsBean : transactionBean.getDocumentsBeanList()) {

                    EmDocuments document = null;
                    if (documentsBean.getId() != null) {
                        document = documentsServices.findOneActiveRecordById(documentsBean.getId());
                        if (document != null) {
                            documentsUpdatedList.add(document.getId());
                        }
                    }

                    if (document == null) {
                        if (documentsBean.getDocumentName() != null && documentsBean.getDocumentBase64() != null && documentsBean.getDocumentFormat() != null) {
                            document = new EmDocuments();

                            document.setTransaction(transaction);

                            document.setDocumentFormat(documentsBean.getDocumentFormat());
                            document.setDocumentDescription(documentsBean.getDocumentName());
                            documentsServices.saveIncomeDocument(document);

                            if (document != null && document.getId() != null) {

                                String fileFormat = FilenameUtils.getExtension(documentsBean.getDocumentName());
                                String fileName = transaction.getId() + "_" + document.getId() + "." + fileFormat;
                                String fileUrl = staticContentTransactionPath + fileName;
                                byte[] imageByte = Base64.decodeBase64(documentsBean.getDocumentBase64());
                                BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                                imageOutFile.write(imageByte);
                                imageOutFile.close();
                                document.setDocumentName("transaction/" + fileName);

                                documentsServices.saveIncomeDocument(document);
                            }
                            documentsUpdatedList.add(document.getId());
                        }
                    }
                }
            }
            if (documentsUpdatedList != null) {
                documentsServices.deleteAllByTransactionAndDocumentsNotIn(transaction, documentsUpdatedList);
            }

            return new ResponseEntity<>(new StatusBean(1, "Record added successfully"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
    }

//    @SuppressWarnings("unchecked")
//    @RequestMapping(value = "/getAllEmTransctionDateWise", produces = "application/json", method = RequestMethod.POST)
//    public ResponseEntity<StatusBean> getAllTransactionsByUser(@RequestBody EmTransactionReportBean transactionReportBean, Device device) {
//        try {
//
//            double mergeAmountOfSameCategory = 0.0;
//            double inflow = 0.0;
//            double outflow = 0.0;
//            User user = usersServices.getUserFromToken();
//            if (user == null) {
//                return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
//            }
//
//            Date fromDate;
//            Date toDate;
//            if (StringUtils.isNotEmpty(transactionReportBean.getFromDate()) && StringUtils.isNotEmpty(transactionReportBean.getToDate())) {
//                fromDate = CommonUtil.changeDateStringToDate(transactionReportBean.getFromDate());
//                toDate = CommonUtil.changeDateStringToDate(transactionReportBean.getToDate());
//
//            } else {
//                fromDate = CommonUtil.getStartDateOfCurrentMonth();
//                toDate = CommonUtil.getLastDateOfCurrentMonth();
//            }
//
//            List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUserAndBetweenTwoDates(user, fromDate, toDate);
//            if (transactionList != null && transactionList.size() > 0) {
//
//                List<EmTransactionBean> transactionBeanList = new ArrayList<>();
//                outerloop:
//                for (EmTransaction transaction : transactionList) {
//
//                    EmTransactionBean transactionBean = new EmTransactionBean();
//                    transactionBean.setId(transaction.getId());
//                    transactionBean.setTaxDeducted(transaction.getTaxDeducted().toString());
//                    transactionBean.setDate(CommonUtil.changeDateToString(transaction.getDate()));
//                    transactionBean.setDescription(transaction.getDescription());
//                    transactionBean.setTransactionTypeId(transaction.getTransactionType().getId());
//                    transactionBean.setTransactionType(transaction.getTransactionType().name());
//                    mergeAmountOfSameCategory = 0;
//
//                    if (transaction.getTransactionCategory() != null) {
//                        transactionBean.setTransactionCategoryId(transaction.getTransactionCategory().getId());
//                        transactionBean.setTransactionCategory(transaction.getTransactionCategory().getCategory());
//                        List<EmTransaction> transaction1 = transactionServices.findAllTransactionsByUserAndTransactionCategory(user, transaction.getTransactionCategory());
//                        List<EmTransactionBean> getListByCategoryAndUserId = new ArrayList<>();
//
//                        for (EmTransaction transaction2 : transaction1) {
//                            EmTransactionBean transactionBean1 = new EmTransactionBean();
//                            transactionBean1.setTaxDeducted("" + transaction2.getTaxDeducted());
//                            transactionBean1.setId(transaction2.getId());
//                            transactionBean1.setAmount(transaction2.getAmount().toString());
//                            transactionBean1.setDescription(transaction2.getDescription());
//                            transactionBean1.setDate(CommonUtil.changeDateToString(transaction2.getDate()));
//                            transactionBean1.setTransactionTypeId(transaction2.getTransactionType().getId());
//                            transactionBean1.setTransactionType(transaction2.getTransactionType().name());
//                            transactionBean1.setTransactionCategoryId(transaction2.getTransactionCategory().getId());
//                            transactionBean1.setTransactionCategory(transaction2.getTransactionCategory().getCategory());
//                            if (transaction.getTransactionAccount() != null) {
//                                transactionBean1.setTransactionAccountId(transaction2.getTransactionAccount().getId());
//                                transactionBean1.setTransactionAccount(transaction2.getTransactionAccount().getType());
//                            }
//                            List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction2);
//
//                            if (documentsList != null && documentsList.size() >= 0) {
//                                List<DocumentsBean> documentsBeanList = new ArrayList<>();
//                                for (EmDocuments documents : documentsList) {
//                                    DocumentsBean documentsBean = new DocumentsBean();
//                                    documentsBean.setId(documents.getId());
//                                    documentsBean.setDocumentName(documents.getDocumentName());
//                                    documentsBean.setDocumentDescription(documents.getDocumentDescription());
//                                    documentsBean.setDocumentFormat(documents.getDocumentFormat());
//
//                                    documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
//
//                                    documentsBeanList.add(documentsBean);
//                                }
//                                transactionBean1.setDocumentsBeanList(documentsBeanList);
//                            }
//
//                            getListByCategoryAndUserId.add(transactionBean1);
//                            transactionBean.setEmSubListByCategory(getListByCategoryAndUserId);
//                            mergeAmountOfSameCategory += transaction2.getAmount();
//                        }
//
//                    }
//                    transactionBean.setAmount("" + mergeAmountOfSameCategory);
//
//                    if (transaction.getTransactionAccount() != null) {
//                        transactionBean.setTransactionAccountId(transaction.getTransactionAccount().getId());
//                        transactionBean.setTransactionAccount(transaction.getTransactionAccount().getType());
//                    }
//
//
//                    if (!transactionBeanList.isEmpty()) {
//                        for (EmTransactionBean emTransactionBean : transactionBeanList) {
//                            if (emTransactionBean.getTransactionCategory().equals(transaction.getTransactionCategory().getCategory())) {
//                                continue outerloop;
//                            }
//                        }
//                    }
//                    transactionBeanList.add(transactionBean);
//                }
//
//                List<EmTransaction> transactionListOfIcomeByUser = transactionServices.findAllIncomeTransactionsByUser(user, fromDate, toDate);
//                for (EmTransaction emTransaction : transactionListOfIcomeByUser) {
//                    //System.out.println(emTransaction.getAmount());
//                    inflow += emTransaction.getAmount();
//                    //System.out.println("================income============="+inflow);
//                }
//                List<EmTransaction> transactionListOfExpensesByUser = transactionServices.findAllExpenseTransactionsByUser(user, fromDate, toDate);
//                for (EmTransaction emTransaction : transactionListOfExpensesByUser) {
//                    //System.out.println(emTransaction.getAmount());
//                    outflow += emTransaction.getAmount();
//                    //System.out.println("================expense============="+outflow);
//                }
//
//                List<EmTransactionReportBean> emTransactionReportBeanList = new ArrayList<>();
//                EmTransactionReportBean emTransactionReportBean = new EmTransactionReportBean();
//                emTransactionReportBean.setExpense("" + outflow);
//                emTransactionReportBean.setIncome("" + inflow);
//                emTransactionReportBean.setTransactionBeanList(transactionBeanList);
//                emTransactionReportBeanList.add(emTransactionReportBean);
//                StatusBean statusBean = new StatusBean(1, "Successfull");
//                statusBean.setResponse(emTransactionReportBeanList);
//                return new ResponseEntity<>(statusBean, HttpStatus.OK);
//            }
//            return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
//        } catch (
//                Exception e)
//
//        {
//            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
//            Logger4j.getLogger().error("Exception : ", e);
//            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
//        }
//
//    }

    //    <==by Maqsood==>
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAllEmTransctionDateWise", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllTransactionsByUser(@RequestBody EmTransactionReportBean transactionReportBean, Device device) {
        try {
            double mergeAmountOfSameCategory = 0.0;
            double inflow = 0.0;
            double outflow = 0.0;
            Date currentDate = CommonUtil.getCurrentTimestamp();

            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
            }

            /*Date fromDate;
            Date toDate;
            if (StringUtils.isNotEmpty(transactionReportBean.getFromDate()) && StringUtils.isNotEmpty(transactionReportBean.getToDate())) {
                fromDate = CommonUtil.changeDateStringToDate(transactionReportBean.getFromDate());
                toDate = CommonUtil.changeDateStringToDate(transactionReportBean.getToDate());

            } else {
                fromDate = CommonUtil.getStartDateOfCurrentMonth();
                toDate = CommonUtil.getLastDateOfCurrentMonth();
            }*/
            List<String> isDate = new ArrayList<String>();
            //List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUserAndBetweenTwoDatesOrderByDate(user, fromDate, toDate);
            List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUser(user);
            if (transactionList != null && transactionList.size() > 0) {

                List<EmTransactionBean> transactionBeanList = new ArrayList<>();
                outerloop:
                for (EmTransaction transaction : transactionList) {

                    EmTransactionBean transactionBean = new EmTransactionBean();
                    transactionBean.setId(transaction.getId());
                    transactionBean.setTaxDeducted(transaction.getTaxDeducted().toString());
                    transactionBean.setDate(CommonUtil.changeDateToString(transaction.getDate()));
                    transactionBean.setDescription(transaction.getDescription());
                    transactionBean.setTransactionTypeId(transaction.getTransactionType().getId());
                    transactionBean.setTransactionType(transaction.getTransactionType().name());
                    mergeAmountOfSameCategory = 0;

                    if (!isDate.contains(CommonUtil.changeDateToString(transaction.getDate()))) {
                        if (transaction.getTransactionCategory() != null) {
                            transactionBean.setTransactionCategoryId(transaction.getTransactionCategory().getId());
                            transactionBean.setTransactionCategory(transaction.getTransactionCategory().getCategory());
//                        List<EmTransaction> transaction1 = transactionServices.findAllTransactionsByUserAndTransactionCategory(user, transaction.getTransactionCategory());
                            List<EmTransaction> transactionByDate = transactionServices.findAllByDate(transaction.getDate());   //<==       ==>
                            List<EmTransactionBean> getListByCategoryAndUserId = new ArrayList<>();

                            for (EmTransaction transaction2 : transactionByDate) {
                                EmTransactionBean transactionBean1 = new EmTransactionBean();
                                transactionBean1.setTaxDeducted("" + transaction2.getTaxDeducted());
                                transactionBean1.setId(transaction2.getId());
                                transactionBean1.setAmount(transaction2.getAmount().toString());
                                transactionBean1.setDescription(transaction2.getDescription());
                                transactionBean1.setDate(CommonUtil.changeDateToString(transaction2.getDate()));
                                transactionBean1.setTransactionTypeId(transaction2.getTransactionType().getId());
                                transactionBean1.setTransactionType(transaction2.getTransactionType().name());
                                transactionBean1.setTransactionCategoryId(transaction2.getTransactionCategory().getId());
                                transactionBean1.setTransactionCategory(transaction2.getTransactionCategory().getCategory());
                                if (transaction.getTransactionAccount() != null) {
                                    transactionBean1.setTransactionAccountId(transaction2.getTransactionAccount().getId());
                                    transactionBean1.setTransactionAccount(transaction2.getTransactionAccount().getType());
                                }
                                List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction2);

                                if (documentsList != null && documentsList.size() >= 0) {
                                    List<DocumentsBean> documentsBeanList = new ArrayList<>();
                                    for (EmDocuments documents : documentsList) {
                                        DocumentsBean documentsBean = new DocumentsBean();
                                        documentsBean.setId(documents.getId());
                                        documentsBean.setDocumentName(documents.getDocumentName());
                                        documentsBean.setDocumentDescription(documents.getDocumentDescription());
                                        documentsBean.setDocumentFormat(documents.getDocumentFormat());

                                        documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());

                                        documentsBeanList.add(documentsBean);
                                    }
                                    transactionBean1.setDocumentsBeanList(documentsBeanList);
                                }

                                getListByCategoryAndUserId.add(transactionBean1);
                                transactionBean.setEmSubListByCategory(getListByCategoryAndUserId);
                                if (transaction2.getTransactionType() == EmTransactionType.INCOME || transaction2.getTransactionType().equals(EmTransactionType.INCOME)) {
                                    mergeAmountOfSameCategory = mergeAmountOfSameCategory + transaction2.getAmount();
                                } else {
                                    mergeAmountOfSameCategory = mergeAmountOfSameCategory - transaction2.getAmount();
                                }
                            }

                        }
                        transactionBean.setAmount("" + mergeAmountOfSameCategory);
                        if (transaction.getTransactionAccount() != null) {
                            transactionBean.setTransactionAccountId(transaction.getTransactionAccount().getId());
                            transactionBean.setTransactionAccount(transaction.getTransactionAccount().getType());
                        }

                        if (!transactionBeanList.isEmpty()) {
                            for (EmTransactionBean emTransactionBean : transactionBeanList) {
                                if (emTransactionBean.getTransactionCategory().equals(transaction.getTransactionCategory().getCategory())) {
                                    continue outerloop;
                                }
                            }
                        }
                        transactionBeanList.add(transactionBean);
                        isDate.add(CommonUtil.changeDateToString(transaction.getDate()));
                    }
                }

                //List<EmTransaction> transactionListOfIcomeByUser = transactionServices.findAllIncomeTransactionsByUser(user, fromDate, toDate);
                List<EmTransaction> transactionListOfIcomeByUser = transactionServices.findAllIncomeTransactionsByUser(user);
                for (EmTransaction emTransaction : transactionListOfIcomeByUser) {
                    //System.out.println(emTransaction.getAmount());
                    inflow += emTransaction.getAmount();
                    //System.out.println("================income============="+inflow);
                }
                //List<EmTransaction> transactionListOfExpensesByUser = transactionServices.findAllExpenseTransactionsByUser(user, fromDate, toDate);
                List<EmTransaction> transactionListOfExpensesByUser = transactionServices.findAllExpenseTransactionsByUser(user);
                for (EmTransaction emTransaction : transactionListOfExpensesByUser) {
                    //System.out.println(emTransaction.getAmount());
                    outflow += emTransaction.getAmount();
                    //System.out.println("================expense============="+outflow);
                }

                List<EmTransactionReportBean> emTransactionReportBeanList = new ArrayList<>();
                EmTransactionReportBean emTransactionReportBean = new EmTransactionReportBean();
                emTransactionReportBean.setCurrentDate("" + CommonUtil.changeDateToString(currentDate));
//                emTransactionReportBean.setCurrentDate("" + currentDate);
                emTransactionReportBean.setExpense("" + outflow);
                emTransactionReportBean.setIncome("" + inflow);
                emTransactionReportBean.setTransactionBeanList(transactionBeanList);
                emTransactionReportBeanList.add(emTransactionReportBean);
                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(emTransactionReportBeanList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
            return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
        } catch (Exception e)

        {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }

    }
//    <==by Maqsood==>


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/report", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllTransactionsByUserAndBetweenTwoDates(@RequestBody EmTransactionReportBean transactionReportBean, Device device) {
        try {
            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
            }

            Date fromDate;
            Date toDate;
            if (StringUtils.isNotEmpty(transactionReportBean.getFromDate()) && StringUtils.isNotEmpty(transactionReportBean.getToDate())) {
                fromDate = CommonUtil.changeDateStringToDate(transactionReportBean.getFromDate());
                toDate = CommonUtil.changeDateStringToDate(transactionReportBean.getToDate());

            } else {
                fromDate = CommonUtil.getStartDateOfCurrentMonth();
                toDate = CommonUtil.getLastDateOfCurrentMonth();
            }

            List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUserAndBetweenTwoDates(user, fromDate, toDate);
            if (transactionList != null && transactionList.size() > 0) {

                Double income = 0.0;
                Double expense = 0.0;
                List<EmTransactionBean> transactionBeanList = new ArrayList<>();

                for (EmTransaction transaction : transactionList) {

                    if (transaction.getTransactionType() != null && transaction.getAmount() != null) {
                        if (transaction.getTransactionType() == EmTransactionType.INCOME)
                            income += transaction.getAmount();
                        if (transaction.getTransactionType() == EmTransactionType.EXPENSE)
                            expense += transaction.getAmount();
                    }

                    EmTransactionBean transactionBean = new EmTransactionBean();

                    transactionBean.setId(transaction.getId());
                    transactionBean.setAmount(transaction.getAmount().toString());
                    transactionBean.setTaxDeducted(transaction.getTaxDeducted().toString());
                    transactionBean.setDate(CommonUtil.changeDateToString(transaction.getDate()));
                    transactionBean.setDescription(transaction.getDescription());

                    transactionBean.setTransactionTypeId(transaction.getTransactionType().getId());
                    transactionBean.setTransactionType(transaction.getTransactionType().name());

                    if (transaction.getTransactionCategory() != null) {
                        transactionBean.setTransactionCategoryId(transaction.getTransactionCategory().getId());
                        transactionBean.setTransactionCategory(transaction.getTransactionCategory().getCategory());
                    }

                    if (transaction.getTransactionAccount() != null) {
                        transactionBean.setTransactionAccountId(transaction.getTransactionAccount().getId());
                        transactionBean.setTransactionAccount(transaction.getTransactionAccount().getType());
                    }

                    List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction);
                    if (documentsList != null && documentsList.size() >= 0) {
                        List<DocumentsBean> documentsBeanList = new ArrayList<>();
                        for (EmDocuments documents : documentsList) {

                            DocumentsBean documentsBean = new DocumentsBean();

                            documentsBean.setId(documents.getId());
                            documentsBean.setDocumentName(documents.getDocumentName());
                            documentsBean.setDocumentDescription(documents.getDocumentDescription());
                            documentsBean.setDocumentFormat(documents.getDocumentFormat());

                            /*if (device.isTablet() || device.isMobile()) {*/
                            documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
                            /*} else {
                                documentsBean.setDocumentBase64(Base64.encodeBase64String(Base64Util.readBytesFromFile(staticContentPath + documents.getDocumentName())));
                            }*/
                            documentsBeanList.add(documentsBean);
                        }
                        transactionBean.setDocumentsBeanList(documentsBeanList);
                    }
                    transactionBeanList.add(transactionBean);
                }

                EmTransactionReportBean transactionReportBean1 = new EmTransactionReportBean();
                transactionReportBean1.setIncome(income + "");
                transactionReportBean1.setExpense(expense + "");
                transactionReportBean1.setBalance((income - expense) + "");

                transactionReportBean1.setFromDate(CommonUtil.changeDateToString(fromDate));
                transactionReportBean1.setToDate(CommonUtil.changeDateToString(toDate));

                transactionReportBean1.setTransactionBeanList(transactionBeanList);

                List<EmTransactionReportBean> transactionReportBeanList = new ArrayList<>();
                transactionReportBeanList.add(transactionReportBean1);

                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(transactionReportBeanList);

                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
            return new ResponseEntity<>(new StatusBean(1, "No record found"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
    }

    /*@SuppressWarnings("unchecked")
    @RequestMapping(value = "/piechart", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> pieChart(Device device) {
	try {
	    User user = usersServices.getUserFromToken();
	    if (user == null) {
		return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
	    }

	    List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUserAndBetweenStartAndEndDate(user, CommonUtil.getStartDateOfCurrentMonth(), CommonUtil.getLastDateOfCurrentMonth());
	    if (transactionList != null && transactionList.size() > 0) {

	        Double income = 0.0;
	        Double expense = 0.0;

		for (EmTransaction transaction : transactionList) {
		    if (transaction.getTransactionType() != null && transaction.getAmount() != null) {
		        if (transaction.getTransactionType() == EmTransactionAccount.INCOME)
		            income += transaction.getAmount();
		        if (transaction.getTransactionType() == EmTransactionAccount.EXPENSE)
		            expense += transaction.getAmount();
		    }
		}

		EmTransactionDashboardBean transactionDashboardBean = new EmTransactionDashboardBean();
		transactionDashboardBean.setIncome(income + "");
		transactionDashboardBean.setExpense(expense + "");
		transactionDashboardBean.setBalance((income - expense) + "");

		List<EmTransactionDashboardBean> transactionDashboardBeanList = new ArrayList<>();
		transactionDashboardBeanList.add(transactionDashboardBean);

		StatusBean statusBean = new StatusBean(1, "Successfull");
		statusBean.setResponse(transactionDashboardBeanList);

		return new ResponseEntity<>(statusBean, HttpStatus.OK);
	    }
	    return new ResponseEntity<>(new StatusBean(1, "No record found"), HttpStatus.OK);
	} catch (Exception e) {
	    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
	    Logger4j.getLogger().error("Exception : " , e);
	    return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
	}
    }*/

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.DELETE)
    public ResponseEntity<Status> deleteTransaction(@PathVariable("id") Integer transactionId) {
        try {
            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
            }

            if (transactionId == null) {
                return new ResponseEntity<>(new Status(0, "Please select record"), HttpStatus.OK);
            }

            EmTransaction transaction = transactionServices.findOneById(transactionId);
            if (transaction != null) {
                documentsServices.deleteAllByTransactionAndDocumentsNotIn(transaction, new ArrayList<>());
                transactionServices.deleteTransaction(transaction);

                return new ResponseEntity<>(new Status(1, "Record deleted successfully"), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(new Status(0, "Selected record not found"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new Status(0, "Something went wrong"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/emTranctionListSeparatedByIncomeAndExpense", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllTransactionsByIncomeAndExpenses(@RequestBody EmTransactionReportBean transactionReportBean, Device device) {
        try {
            Map<String, List<EmTransactionBean>> responseMap = new HashMap<>();
            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);
            }
            /*Date fromDate;
            Date toDate;
            if (StringUtils.isNotEmpty(transactionReportBean.getFromDate()) && StringUtils.isNotEmpty(transactionReportBean.getToDate())) {
                fromDate = CommonUtil.changeDateStringToDate(transactionReportBean.getFromDate());
                toDate = CommonUtil.changeDateStringToDate(transactionReportBean.getToDate());

            } else {
                fromDate = CommonUtil.getStartDateOfCurrentMonth();
                toDate = CommonUtil.getLastDateOfCurrentMonth();
            }*/
            //List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUserAndBetweenTwoDates(user, fromDate, toDate);
            List<EmTransaction> transactionList = transactionServices.findAllTransactionsByUser(user);
            List<EmTransactionBean> transactionBeanList = new ArrayList<>();
            double mergeAmountOfSameCategory = 0.0;
            if (transactionList != null && transactionList.size() > 0) {
                outerloop:
                for (EmTransaction transaction : transactionList) {
                    if (transaction.getTransactionType() == EmTransactionType.INCOME) {
                        EmTransactionBean transactionBean = new EmTransactionBean();
                        transactionBean.setId(transaction.getId());
                        transactionBean.setTaxDeducted(transaction.getTaxDeducted().toString());
                        transactionBean.setDate(CommonUtil.changeDateToString(transaction.getDate()));
                        transactionBean.setDescription(transaction.getDescription());
                        transactionBean.setTransactionTypeId(transaction.getTransactionType().getId());
                        transactionBean.setTransactionType(transaction.getTransactionType().name());

                        if (transaction.getTransactionCategory() != null) {
                            transactionBean.setTransactionCategoryId(transaction.getTransactionCategory().getId());
                            transactionBean.setTransactionCategory(transaction.getTransactionCategory().getCategory());
                            List<EmTransaction> transaction1 = transactionServices.findAllTransactionsByUserAndTransactionCategoryAndTranscationType(user, transaction.getTransactionCategory(), transaction.getTransactionType());
                            List<EmTransactionBean> getListByCategoryAndUserId = new ArrayList<>();
                            for (EmTransaction transaction2 : transaction1) {
                                EmTransactionBean transactionBean1 = new EmTransactionBean();
                                transactionBean1.setAmount(transaction2.getAmount().toString());
                                transactionBean1.setDescription(transaction2.getDescription());
                                transactionBean1.setDate(CommonUtil.changeDateToString(transaction2.getDate()));
                                transactionBean1.setTransactionTypeId(transaction2.getTransactionType().getId());
                                transactionBean1.setTransactionType(transaction2.getTransactionType().name());
                                transactionBean1.setTransactionCategoryId(transaction2.getTransactionCategory().getId());
                                transactionBean1.setTransactionCategory(transaction2.getTransactionCategory().getCategory());
                                transactionBean1.setTaxDeducted("" + transaction2.getTaxDeducted());
                                transactionBean1.setId(transaction2.getId());
                                if (transaction.getTransactionAccount() != null) {
                                    transactionBean1.setTransactionAccountId(transaction2.getTransactionAccount().getId());
                                    transactionBean1.setTransactionAccount(transaction2.getTransactionAccount().getType());
                                }
                                // <===by Maqsood
                                List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction2);
                                if (documentsList != null && documentsList.size() >= 0) {
                                    List<DocumentsBean> documentsBeanList = new ArrayList<>();
                                    for (EmDocuments documents : documentsList) {
                                        DocumentsBean documentsBean = new DocumentsBean();
                                        documentsBean.setId(documents.getId());
                                        documentsBean.setDocumentName(documents.getDocumentName());
                                        documentsBean.setDocumentDescription(documents.getDocumentDescription());
                                        documentsBean.setDocumentFormat(documents.getDocumentFormat());
                                        documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
                                        documentsBeanList.add(documentsBean);

                                    }
                                    transactionBean1.setDocumentsBeanList(documentsBeanList);
                                }    // by Maqsood===>

                                getListByCategoryAndUserId.add(transactionBean1);
                                transactionBean.setEmSubListByCategory(getListByCategoryAndUserId);

//								System.out.println("=======before======== "+ mergeAmountOfSameCategory);
                                mergeAmountOfSameCategory += transaction2.getAmount();
//								System.out.println("===========after========== "+ mergeAmountOfSameCategory);
                            }

                        }
                        transactionBean.setAmount("" + mergeAmountOfSameCategory);
                        mergeAmountOfSameCategory = 0.0;        // <==by Maqsood==>
                        if (transaction.getTransactionAccount() != null) {
                            transactionBean.setTransactionAccountId(transaction.getTransactionAccount().getId());
                            transactionBean.setTransactionAccount(transaction.getTransactionAccount().getType());
                        }

//						List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction);
//						if (documentsList != null && documentsList.size() >= 0) {
//							List<DocumentsBean> documentsBeanList = new ArrayList<>();
//							for (EmDocuments documents : documentsList) {
//								DocumentsBean documentsBean = new DocumentsBean();
//								documentsBean.setId(documents.getId());
//								documentsBean.setDocumentName(documents.getDocumentName());
//								documentsBean.setDocumentDescription(documents.getDocumentDescription());
//								documentsBean.setDocumentFormat(documents.getDocumentFormat());
//								documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
//								documentsBeanList.add(documentsBean);
//							}
//							transactionBean.setDocumentsBeanList(documentsBeanList);
//						}
                        if (!transactionBeanList.isEmpty()) {
                            for (EmTransactionBean emTransactionBean : transactionBeanList) {
                                if (emTransactionBean.getTransactionCategory().equals(transaction.getTransactionCategory().getCategory())) {
                                    continue outerloop;
                                }
                            }
                        }

                        transactionBeanList.add(transactionBean);
                    }
                }
                responseMap.put("INCOME", transactionBeanList);
            }
            //List<EmTransaction> transactionListExpenses = transactionServices.findAllTransactionsByUserAndBetweenTwoDates(user, fromDate, toDate);
            List<EmTransaction> transactionListExpenses = transactionServices.findAllTransactionsByUser(user);
            List<EmTransactionBean> transactionBeanListExpenses = new ArrayList<>();
            double mergeAmountOfSameCategoryExpenses = 0.0;
            if (transactionListExpenses != null && transactionListExpenses.size() > 0) {
                outerloop:
                for (EmTransaction transaction : transactionListExpenses) {
                    if (transaction.getTransactionType() == EmTransactionType.EXPENSE) {
                        EmTransactionBean transactionBean = new EmTransactionBean();
                        transactionBean.setId(transaction.getId());
                        transactionBean.setTaxDeducted(transaction.getTaxDeducted().toString());
                        transactionBean.setDate(CommonUtil.changeDateToString(transaction.getDate()));
                        transactionBean.setDescription(transaction.getDescription());
                        transactionBean.setTransactionTypeId(transaction.getTransactionType().getId());
                        transactionBean.setTransactionType(transaction.getTransactionType().name());

                        if (transaction.getTransactionCategory() != null) {
                            transactionBean.setTransactionCategoryId(transaction.getTransactionCategory().getId());
                            transactionBean.setTransactionCategory(transaction.getTransactionCategory().getCategory());
                            List<EmTransaction> transaction1 = transactionServices.findAllTransactionsByUserAndTransactionCategoryAndTranscationType(user, transaction.getTransactionCategory(), transaction.getTransactionType());
                            List<EmTransactionBean> getListByCategoryAndUserId = new ArrayList<>();
                            for (EmTransaction transaction2 : transaction1) {

                                EmTransactionBean transactionBean1 = new EmTransactionBean();
                                transactionBean1.setTaxDeducted("" + transaction2.getTaxDeducted());
                                transactionBean1.setId(transaction2.getId());
                                transactionBean1.setAmount(transaction2.getAmount().toString());
                                transactionBean1.setDescription(transaction2.getDescription());
                                transactionBean1.setDate(CommonUtil.changeDateToString(transaction2.getDate()));
                                transactionBean1.setTransactionTypeId(transaction2.getTransactionType().getId());
                                transactionBean1.setTransactionType(transaction2.getTransactionType().name());
                                transactionBean1.setTransactionCategoryId(transaction2.getTransactionCategory().getId());
                                transactionBean1.setTransactionCategory(transaction2.getTransactionCategory().getCategory());

                                if (transaction.getTransactionAccount() != null) {
                                    transactionBean1.setTransactionAccountId(transaction2.getTransactionAccount().getId());
                                    transactionBean1.setTransactionAccount(transaction2.getTransactionAccount().getType());
                                }

                                // <===by Maqsood
                                List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction2);
                                if (documentsList != null && documentsList.size() >= 0) {
                                    List<DocumentsBean> documentsBeanList = new ArrayList<>();
                                    for (EmDocuments documents : documentsList) {
                                        DocumentsBean documentsBean = new DocumentsBean();
                                        documentsBean.setId(documents.getId());
                                        documentsBean.setDocumentName(documents.getDocumentName());
                                        documentsBean.setDocumentDescription(documents.getDocumentDescription());
                                        documentsBean.setDocumentFormat(documents.getDocumentFormat());
                                        documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
                                        documentsBeanList.add(documentsBean);
                                    }
                                    transactionBean1.setDocumentsBeanList(documentsBeanList);
                                }   // by Maqsood===>
                                getListByCategoryAndUserId.add(transactionBean1);
                                transactionBean.setEmSubListByCategory(getListByCategoryAndUserId);
                                mergeAmountOfSameCategoryExpenses += transaction2.getAmount();
                            }

                        }
                        transactionBean.setAmount("" + mergeAmountOfSameCategoryExpenses);
                        mergeAmountOfSameCategoryExpenses = 0.0;   // <==by Maqsood==>

                        if (transaction.getTransactionAccount() != null) {
                            transactionBean.setTransactionAccountId(transaction.getTransactionAccount().getId());
                            transactionBean.setTransactionAccount(transaction.getTransactionAccount().getType());
                        }

//							List<EmDocuments> documentsList = documentsServices.findAllActiveRecordsByTransaction(transaction);
//							if (documentsList != null && documentsList.size() >= 0) {
//								List<DocumentsBean> documentsBeanList = new ArrayList<>();
//								for (EmDocuments documents : documentsList) {
//									DocumentsBean documentsBean = new DocumentsBean();
//									documentsBean.setId(documents.getId());
//									documentsBean.setDocumentName(documents.getDocumentName());
//									documentsBean.setDocumentDescription(documents.getDocumentDescription());
//									documentsBean.setDocumentFormat(documents.getDocumentFormat());
//									documentsBean.setDocumentURL(serverUrl + documents.getDocumentName());
//									documentsBeanList.add(documentsBean);
//								}
//								transactionBean.setDocumentsBeanList(documentsBeanList);
//							}
                        if (!transactionBeanListExpenses.isEmpty()) {
                            for (EmTransactionBean emTransactionBean : transactionBeanListExpenses) {
                                if (emTransactionBean.getTransactionCategory().equals(transaction.getTransactionCategory().getCategory())) {
                                    continue outerloop;
                                }
                            }
                        }
                        transactionBeanListExpenses.add(transactionBean);
                    }

                }
            }

            responseMap.put("EXPENSE", transactionBeanListExpenses);
            StatusBean statusBean = new StatusBean(1, "Successfull");
            statusBean.setResponseMap(responseMap);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
    }
}
