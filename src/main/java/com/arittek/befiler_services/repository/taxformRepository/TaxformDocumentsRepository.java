package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.TaxformDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxformDocumentsRepository extends JpaRepository<TaxformDocuments, Integer>, RevisionRepository<TaxformDocuments, Integer, Integer> {

    TaxformDocuments findOneByIdAndStatus(Integer taxformDocumentId, AppStatus appStatus);

    List<TaxformDocuments> findAllByTaxformAndStatus(Taxform taxform, AppStatus status);
    List<TaxformDocuments> findAllByTaxformAndDocumentTypeAndStatus(Taxform taxform, String documentType, AppStatus status);
    List<TaxformDocuments> findAllByTaxformAndIdNotIn(Taxform taxform, List<Integer> activeRecordsList);
}
