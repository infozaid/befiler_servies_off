package com.arittek.befiler_services.repository.setting.taxform;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDoneeBridge;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovedDoneeBridgeRepository extends JpaRepository<ApprovedDoneeBridge, Integer>, RevisionRepository<ApprovedDoneeBridge, Integer, Integer> {

    @Query("SELECT approvedDoneeBridge FROM ApprovedDoneeBridge approvedDoneeBridge " +
	    "JOIN approvedDoneeBridge.approvedDonee approvedDonee " +
	    "JOIN approvedDoneeBridge.taxformYear taxformYear " +
	    "WHERE taxformYear = :taxformYear " +
	    "AND approvedDoneeBridge.status = :status " +
	    "AND approvedDonee.status >= :status ")
    List<ApprovedDoneeBridge> findAllByTaxformYearAndStatus(@Param("taxformYear") TaxformYears taxformYear, @Param("status") AppStatus status);
}
