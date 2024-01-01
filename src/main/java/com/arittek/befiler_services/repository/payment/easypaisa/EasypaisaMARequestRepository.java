package com.arittek.befiler_services.repository.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMARequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EasypaisaMARequestRepository extends JpaRepository<EasypaisaMARequest, Integer>, RevisionRepository<EasypaisaMARequest, Integer, Integer> {
}
