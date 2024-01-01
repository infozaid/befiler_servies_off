package com.arittek.befiler_services.repository.finja_external_registration;

import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep2Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaExternalRegistrationStep2ResponseRepository extends JpaRepository<FinjaExternalRegistrationStep2Response, Integer>, RevisionRepository<FinjaExternalRegistrationStep2Response, Integer, Integer> {
}
