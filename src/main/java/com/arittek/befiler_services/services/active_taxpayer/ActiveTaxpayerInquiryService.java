package com.arittek.befiler_services.services.active_taxpayer;

import com.arittek.befiler_services.model.active_taxpayer.ActiveTaxpayerInquiry;

import java.util.List;

public interface ActiveTaxpayerInquiryService {


    ActiveTaxpayerInquiry create(ActiveTaxpayerInquiry activeTaxpayerInquiry) throws Exception;
    ActiveTaxpayerInquiry update(ActiveTaxpayerInquiry activeTaxpayerInquiry) throws Exception;

    Boolean registrationNoIsExist(String registrationNo) throws Exception;

    List<ActiveTaxpayerInquiry> findAll() throws Exception;

}
