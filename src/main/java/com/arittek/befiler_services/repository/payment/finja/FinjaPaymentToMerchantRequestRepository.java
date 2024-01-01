package com.arittek.befiler_services.repository.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaPaymentToMerchantRequestRepository extends JpaRepository<FinjaPaymentToMerchantRequest, Integer>, RevisionRepository<FinjaPaymentToMerchantRequest, Integer, Integer> {
}
