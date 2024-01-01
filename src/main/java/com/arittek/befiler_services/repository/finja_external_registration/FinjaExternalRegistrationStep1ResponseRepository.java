package com.arittek.befiler_services.repository.finja_external_registration;

import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep1Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaExternalRegistrationStep1ResponseRepository extends JpaRepository<FinjaExternalRegistrationStep1Response, Integer>, RevisionRepository<FinjaExternalRegistrationStep1Response, Integer, Integer> {
}
