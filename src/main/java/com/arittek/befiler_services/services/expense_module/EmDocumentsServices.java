package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.expense_module.EmDocuments;
import com.arittek.befiler_services.model.expense_module.EmTransaction;

import java.util.List;

public interface EmDocumentsServices {

    EmDocuments saveIncomeDocument(EmDocuments document) throws Exception;
    EmDocuments saveExpenseDocument(EmDocuments document) throws Exception;

    EmDocuments findOneActiveRecordById(Integer documentId) throws Exception;

    List<EmDocuments> findAllActiveRecordsByTransaction(EmTransaction transaction) throws Exception;

    void deleteAllByTransactionAndDocumentsNotIn(EmTransaction transaction, List<Integer> documentsList);
}
