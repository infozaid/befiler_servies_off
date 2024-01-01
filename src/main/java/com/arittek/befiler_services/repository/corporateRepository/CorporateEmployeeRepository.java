package com.arittek.befiler_services.repository.corporateRepository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporateEmployeeRepository extends JpaRepository<CorporateEmployee, Integer>, RevisionRepository<CorporateEmployee, Integer, Integer> {

    CorporateEmployee findCorporateEmployeeByCnicNo(String cnic);

    CorporateEmployee findByIdAndStatus(Integer id, AppStatus appStatus);

    List<CorporateEmployee> findAllByUser(User user);
    List<CorporateEmployee> findAllByCorporate(Corporate corporate);

    List<CorporateEmployee> findAllByCorporateAndStatus(Corporate corporate, AppStatus status);

}
