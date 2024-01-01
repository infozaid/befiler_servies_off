package com.arittek.befiler_services.repository.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpgFinalizationRequestRepository extends JpaRepository<IpgFinalizationRequest, Integer>, RevisionRepository<IpgFinalizationRequest, Integer, Integer> {
}
