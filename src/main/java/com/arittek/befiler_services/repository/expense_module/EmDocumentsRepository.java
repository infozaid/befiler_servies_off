package com.arittek.befiler_services.repository.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.expense_module.EmDocuments;
import com.arittek.befiler_services.model.expense_module.EmTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EmDocumentsRepository extends JpaRepository<EmDocuments, Integer>, RevisionRepository<EmDocuments, Integer, Integer> {

    List<EmDocuments> findAllByTransaction(EmTransaction transaction);

    @Transactional
    void deleteAllByTransactionAndIdNotIn(EmTransaction transaction, List<Integer> documentsList);
}
