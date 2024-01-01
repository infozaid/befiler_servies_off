package com.arittek.befiler_services.repository.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaGetCustomerInfoResponseRepository extends JpaRepository<FinjaGetCustomerInfoResponse, Integer>, RevisionRepository<FinjaGetCustomerInfoResponse, Integer, Integer> {
}
