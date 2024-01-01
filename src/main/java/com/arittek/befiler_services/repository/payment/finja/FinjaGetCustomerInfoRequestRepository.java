package com.arittek.befiler_services.repository.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinjaGetCustomerInfoRequestRepository extends JpaRepository<FinjaGetCustomerInfoRequest, Integer>, RevisionRepository<FinjaGetCustomerInfoRequest, Integer, Integer> {

    List<FinjaGetCustomerInfoRequest> findAllByCreatedBy(Integer createById);
}
