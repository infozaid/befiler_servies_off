package com.arittek.befiler_services.repository.payment.easypaisa;

import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMAResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EasypaisaMAResponseRepository extends JpaRepository<EasypaisaMAResponse, Integer>, RevisionRepository<EasypaisaMAResponse, Integer, Integer> {
}
