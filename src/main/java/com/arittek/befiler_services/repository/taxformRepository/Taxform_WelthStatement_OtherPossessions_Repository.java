package com.arittek.befiler_services.repository.taxformRepository;

import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_WelthStatement_OtherPossessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Taxform_WelthStatement_OtherPossessions_Repository extends JpaRepository<Taxform_WelthStatement_OtherPossessions, Integer>, RevisionRepository<Taxform_WelthStatement_OtherPossessions, Integer, Integer> {
    List<Taxform_WelthStatement_OtherPossessions> findAllByTaxform(Taxform taxform);

    void deleteAllByTaxformAndIdNotIn(Taxform taxform, List otherPossessionsUpdatedRecords);
}
