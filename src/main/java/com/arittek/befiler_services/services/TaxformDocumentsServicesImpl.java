package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.taxform.TaxformDocuments;
import com.arittek.befiler_services.repository.taxformRepository.TaxformDocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxformDocumentsServicesImpl implements TaxformDocumentsServices {

    private TaxformDocumentsRepository taxformDocumentsRepository;

    @Autowired
    public TaxformDocumentsServicesImpl(TaxformDocumentsRepository taxformDocumentsRepository) {
        this.taxformDocumentsRepository = taxformDocumentsRepository;
    }

    @Override
    public TaxformDocuments save(TaxformDocuments taxformDocuments) throws Exception {
        if (taxformDocuments != null) {
            return taxformDocumentsRepository.save(taxformDocuments);
        }
        return null;
    }
}
