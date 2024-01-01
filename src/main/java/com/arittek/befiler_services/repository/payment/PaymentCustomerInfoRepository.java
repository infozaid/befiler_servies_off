package com.arittek.befiler_services.repository.payment;

import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCustomerInfoRepository extends JpaRepository<PaymentCustomerInfo, Integer>, RevisionRepository<PaymentCustomerInfo, Integer, Integer> {

    PaymentCustomerInfo findOneByOrderId(String orderNo);
}
