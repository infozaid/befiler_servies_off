package com.arittek.befiler_services.services.payment.ibft;

import com.arittek.befiler_services.model.payment.ibft.IBFTRequestDocument;

public interface IBFTRequestDocumentServices {

    IBFTRequestDocument saveOrUpdate(IBFTRequestDocument ibftRequestDocument) throws Exception;
}
