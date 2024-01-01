package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.email.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfigRepository extends JpaRepository<EmailConfig,Integer>, RevisionRepository<EmailConfig, Integer, Integer> {
     EmailConfig findByStatus(Integer status);

}
