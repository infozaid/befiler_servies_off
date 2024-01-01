package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform_DeductibleAllowanceOrCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Taxform_DeductibleAllowanceOrCredit_Repository extends JpaRepository<Taxform_DeductibleAllowanceOrCredit, Integer>, RevisionRepository<Taxform_DeductibleAllowanceOrCredit, Integer, Integer> {
}
