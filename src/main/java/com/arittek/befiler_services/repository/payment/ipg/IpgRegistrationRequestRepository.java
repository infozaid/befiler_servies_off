package com.arittek.befiler_services.repository.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IpgRegistrationRequestRepository extends JpaRepository<IpgRegistrationRequest, Integer>, RevisionRepository<IpgRegistrationRequest, Integer, Integer> {

    List<IpgRegistrationRequest> findAllByCreatedBy(Integer createdById);
}
