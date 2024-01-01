package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.email.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Integer>, RevisionRepository<EmailLog, Integer, Integer> {
}
