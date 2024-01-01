package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform_IncomeTax_CapitalGain_Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Taxform_IncomeTax_CapitalGain_Property_Repository extends JpaRepository<Taxform_IncomeTax_CapitalGain_Property, Integer>, RevisionRepository<Taxform_IncomeTax_CapitalGain_Property, Integer, Integer> {
}
