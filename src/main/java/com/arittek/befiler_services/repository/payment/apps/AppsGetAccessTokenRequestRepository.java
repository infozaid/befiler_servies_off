package com.arittek.befiler_services.repository.payment.apps;

import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppsGetAccessTokenRequestRepository extends JpaRepository<AppsGetAccessTokenRequest, Integer>, RevisionRepository<AppsGetAccessTokenRequest, Integer, Integer> {
}
