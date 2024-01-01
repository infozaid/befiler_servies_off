package com.arittek.befiler_services.repository.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgFinalizationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpgFinalizationResponseRepository extends JpaRepository<IpgFinalizationResponse, Integer>, RevisionRepository<IpgFinalizationResponse, Integer, Integer> {
}
