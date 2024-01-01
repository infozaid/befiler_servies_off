package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform_WelthStatement_DetailsOfPersonalExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Taxform_WelthStatement_DetailsOfPersonalExpense_Repository extends JpaRepository<Taxform_WelthStatement_DetailsOfPersonalExpense, Integer>, RevisionRepository<Taxform_WelthStatement_DetailsOfPersonalExpense, Integer, Integer> {
}
