package com.arittek.befiler_services.services.payment.ibft;

import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IBFTRequestServices {

    IBFTRequest save(IBFTRequest ibftRequest) throws Exception;

    IBFTRequest findOneActiveRecordById(Integer ibftRequestId) throws Exception;

    List<IBFTRequest> findAllActiveRecords() throws Exception;
    List<IBFTRequest> findAllActiveRecordsOrderByIdDesc() throws Exception;
}
