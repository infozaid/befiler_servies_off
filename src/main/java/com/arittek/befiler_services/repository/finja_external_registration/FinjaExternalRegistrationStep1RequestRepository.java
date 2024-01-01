package com.arittek.befiler_services.repository.finja_external_registration;

import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep1Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinjaExternalRegistrationStep1RequestRepository extends JpaRepository<FinjaExternalRegistrationStep1Request, Integer>, RevisionRepository<FinjaExternalRegistrationStep1Request, Integer, Integer> {
}
