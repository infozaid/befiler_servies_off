package com.arittek.befiler_services.repository.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EasypaisaOTCRequestRepository extends JpaRepository<EasypaisaOTCRequest, Integer>, RevisionRepository<EasypaisaOTCRequest, Integer, Integer> {
}
