package com.arittek.befiler_services.repository.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaPaymentToMerchantResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaPaymentToMerchantResponseRepository extends JpaRepository<FinjaPaymentToMerchantResponse, Integer>, RevisionRepository<FinjaPaymentToMerchantResponse, Integer, Integer> {
}
