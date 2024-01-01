package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform_TaxDeductedCollected_Other;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Taxform_TaxDeductedCollected_Other_Repository extends JpaRepository<Taxform_TaxDeductedCollected_Other, Integer>, RevisionRepository<Taxform_TaxDeductedCollected_Other, Integer, Integer> {
}
