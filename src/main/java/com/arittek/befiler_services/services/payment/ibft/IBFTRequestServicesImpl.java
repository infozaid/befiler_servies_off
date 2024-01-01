package com.arittek.befiler_services.services.payment.ibft;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import com.arittek.befiler_services.repository.payment.ibft.IBFTRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IBFTRequestServicesImpl implements IBFTRequestServices {

    private IBFTRequestRepository ibftRequestRepository;

    @Autowired
    public IBFTRequestServicesImpl(IBFTRequestRepository ibftRequestRepository) {
        this.ibftRequestRepository = ibftRequestRepository;
    }

    @Override
    public IBFTRequest save(IBFTRequest ibftRequest) throws Exception {
        if (ibftRequest != null)
            return ibftRequestRepository.save(ibftRequest);
        return null;
    }

    @Override
    public IBFTRequest findOneActiveRecordById(Integer ibftRequestId) throws Exception {
        if (ibftRequestId != null) {
            return ibftRequestRepository.findOneByIdAndStatus(ibftRequestId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<IBFTRequest> findAllActiveRecords() throws Exception {
        return ibftRequestRepository.findAllByStatus(AppStatus.ACTIVE);
    }

    @Override
    public List<IBFTRequest> findAllActiveRecordsOrderByIdDesc() throws Exception {
        return ibftRequestRepository.findAllByStatusOrderByIdDesc(AppStatus.ACTIVE);
    }
}
