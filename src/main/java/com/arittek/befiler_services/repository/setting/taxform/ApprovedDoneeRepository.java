package com.arittek.befiler_services.repository.setting.taxform;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedDoneeRepository extends JpaRepository<ApprovedDonee, Integer>, RevisionRepository<ApprovedDonee, Integer, Integer> {

    ApprovedDonee findOneByIdAndStatus(Integer approvedDoneeId, AppStatus status);

    List<ApprovedDonee> findAllByStatus(AppStatus appStatus);

}
