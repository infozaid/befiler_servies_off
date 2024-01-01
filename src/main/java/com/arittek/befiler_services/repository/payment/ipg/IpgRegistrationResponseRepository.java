package com.arittek.befiler_services.repository.payment.ipg;

import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpgRegistrationResponseRepository extends JpaRepository<IpgRegistrationResponse, Integer>, RevisionRepository<IpgRegistrationResponse, Integer, Integer> {
}
