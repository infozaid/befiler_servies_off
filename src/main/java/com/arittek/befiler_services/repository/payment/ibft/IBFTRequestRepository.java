package com.arittek.befiler_services.repository.payment.ibft;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBFTRequestRepository extends JpaRepository<IBFTRequest,Integer>, RevisionRepository<IBFTRequest, Integer, Integer> {

    IBFTRequest findOneByIdAndStatus(Integer ibftRequestId, AppStatus status);

    List<IBFTRequest> findAllByStatus(AppStatus status);
    List<IBFTRequest> findAllByStatusOrderByIdDesc(AppStatus status);
}
