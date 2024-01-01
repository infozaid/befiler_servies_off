package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.repository.taxformRepository.Taxform_Status_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxformStatusServicesImp implements TaxformStatusServices  {

    private Taxform_Status_Repository taxformStatusRepository;

    @Autowired
    TaxformStatusServicesImp(Taxform_Status_Repository taxformStatusRepository){
        this.taxformStatusRepository=taxformStatusRepository;
    }

    @Override
    public Taxform_Status findOneByTaxformStatus() throws Exception{
        return taxformStatusRepository.findById(0).orElse(null);
    }
    @Override
    public Taxform_Status findOneByCorporateEmployeeStatus() throws Exception {
        return taxformStatusRepository.findById(1).orElse(null);
   }
    @Override
    public Taxform_Status  findOneByFBRStatus() throws Exception {
        return taxformStatusRepository.findById(2).orElse(null);
    }
    @Override
    public Taxform_Status findOneByPaymentStatus() throws Exception {
        return taxformStatusRepository.findById(3).orElse(null);
    }
    @Override
    public Taxform_Status findOneByAccountantStatus() throws Exception {
        return taxformStatusRepository.findById(4).orElse(null);
    }
    @Override
    public Taxform_Status findOneByLawyerNewStatus() throws Exception {
        return taxformStatusRepository.findById(5).orElse(null);
    }
    @Override
    public Taxform_Status findOneByLawyerPendingStatus() throws Exception {
        return taxformStatusRepository.findById(6).orElse(null);
    }
    @Override
    public Taxform_Status findOneByLawyerCompleteStatus() throws Exception {
        return taxformStatusRepository.findById(7).orElse(null);
    }



}
