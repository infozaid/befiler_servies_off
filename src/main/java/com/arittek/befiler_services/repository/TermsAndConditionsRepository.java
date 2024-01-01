package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.TermsAndConditions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermsAndConditionsRepository extends JpaRepository<TermsAndConditions, Integer>, RevisionRepository<TermsAndConditions, Integer, Integer> {
    TermsAndConditions findByTypeAndStatus(Integer type, Integer status);

}
