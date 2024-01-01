package com.arittek.befiler_services.services.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.expense_module.EmDocuments;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import com.arittek.befiler_services.repository.expense_module.EmDocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmDocumentsServicesImpl implements EmDocumentsServices {

    private EmDocumentsRepository documentsRepository;

    @Autowired
    public EmDocumentsServicesImpl(EmDocumentsRepository documentsRepository) {
	this.documentsRepository = documentsRepository;
    }

    @Override
    public EmDocuments saveIncomeDocument(EmDocuments document) throws Exception {
        if (document != null) {
            document.setTransactionType(EmTransactionType.INCOME);

            return documentsRepository.save(document);
	}
	return null;
    }

    @Override
    public EmDocuments saveExpenseDocument(EmDocuments document) throws Exception {
	if (document != null) {
	    document.setTransactionType(EmTransactionType.EXPENSE);

	    return documentsRepository.save(document);
	}
	return null;
    }

    @Override
    public EmDocuments findOneActiveRecordById(Integer documentId) throws Exception {
        if (documentId != null) {
            return documentsRepository.findById(documentId).orElse(null);
	}
	return null;
    }

    @Override
    public List<EmDocuments> findAllActiveRecordsByTransaction(EmTransaction transaction) throws Exception {
        if (transaction != null) {
            return documentsRepository.findAllByTransaction(transaction);
	}
	return null;
    }

    @Override
    public void deleteAllByTransactionAndDocumentsNotIn(EmTransaction transaction, List<Integer> documentsList) {
	documentsRepository.deleteAllByTransactionAndIdNotIn(transaction, documentsList);
    }
}
