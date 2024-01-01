package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.SalarySlab;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalarySlabRepository extends CrudRepository<SalarySlab,Integer> {

    SalarySlab findOneByIdAndStatus(Integer salarySlabId, AppStatus status);

    List<SalarySlab> findAllByTaxformYearAndStatus(TaxformYears taxformYears, AppStatus status);
}
