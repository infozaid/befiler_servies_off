package com.arittek.befiler_services.repository.payment.keenu;

import com.arittek.befiler_services.model.payment.keenu.KeenuResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeenuResponseRepository extends JpaRepository<KeenuResponse, Integer>, RevisionRepository<KeenuResponse, Integer, Integer> {
}
