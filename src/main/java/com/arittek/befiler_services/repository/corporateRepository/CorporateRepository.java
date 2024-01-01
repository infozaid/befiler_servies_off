package com.arittek.befiler_services.repository.corporateRepository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporateRepository extends JpaRepository<Corporate, Integer>, RevisionRepository<Corporate, Integer, Integer> {

    Corporate findOneByIdAndStatus(Integer corporateId, AppStatus status);

    List<Corporate> findAllByStatus(AppStatus status);

}
