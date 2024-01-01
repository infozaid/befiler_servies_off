package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxformYearsRepository extends JpaRepository<TaxformYears, Integer>, RevisionRepository<TaxformYears, Integer, Integer> {

    TaxformYears findOneByYear(Integer year);
    TaxformYears findOneByIdAndStatus(Integer yearId, AppStatus status);
    TaxformYears findOneByIdAndStatusNotIn(Integer yearId, AppStatus status);
    TaxformYears findOneByYearAndStatusNotIn(Integer year, AppStatus status);

    List<TaxformYears> findAllByStatus(AppStatus appStatus);
    List<TaxformYears> findAllByStatusOrderByYearDesc(AppStatus status);
    List<TaxformYears> findAllByStatusNotIn(AppStatus appStatus);
}
