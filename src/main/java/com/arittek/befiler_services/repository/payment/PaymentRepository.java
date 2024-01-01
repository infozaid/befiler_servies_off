package com.arittek.befiler_services.repository.payment;

import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>, RevisionRepository<Payment, Integer, Integer> {
    List<Payment> findAllByOrderByIdDesc();
    List<Payment> findAllByUser(User user);
}
