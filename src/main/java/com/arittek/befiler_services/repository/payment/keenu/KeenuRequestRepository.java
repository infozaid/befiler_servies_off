package com.arittek.befiler_services.repository.payment.keenu;

import com.arittek.befiler_services.model.payment.keenu.KeenuRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeenuRequestRepository extends JpaRepository<KeenuRequest, Integer>, RevisionRepository<KeenuRequest, Integer, Integer> {
}
