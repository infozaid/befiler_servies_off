package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.taxform.Taxform_Status;

public interface TaxformStatusServices {
    Taxform_Status findOneByTaxformStatus() throws Exception;
    Taxform_Status findOneByCorporateEmployeeStatus() throws Exception;
    Taxform_Status findOneByFBRStatus() throws Exception;
    Taxform_Status findOneByPaymentStatus() throws Exception;
    Taxform_Status findOneByAccountantStatus() throws Exception;
    Taxform_Status findOneByLawyerNewStatus() throws Exception;
    Taxform_Status findOneByLawyerPendingStatus() throws Exception;
    Taxform_Status findOneByLawyerCompleteStatus() throws Exception;



}
