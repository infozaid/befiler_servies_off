package com.arittek.befiler_services.repository.finja_external_registration;

import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep2Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaExternalRegistrationStep2RequestRepository extends JpaRepository<FinjaExternalRegistrationStep2Request, Integer>, RevisionRepository<FinjaExternalRegistrationStep2Request, Integer, Integer> {
}
