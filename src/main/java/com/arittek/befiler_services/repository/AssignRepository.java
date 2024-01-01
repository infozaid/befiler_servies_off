package com.arittek.befiler_services.repository;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AssignType;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignRepository extends JpaRepository<Assign, Integer>, RevisionRepository<Assign, Integer, Integer> {

    Assign findOneByIdAndAppStatus(Integer assignId, AppStatus appStatus);

    Assign findOneByLawyerAndTaxformAndAppStatus(User lawyer, Taxform taxform, AppStatus appStatus);
    Assign findOneByLawyerAndFbrUserAccountInfoAndAppStatus(User lawyer, FbrUserAccountInfo fbrUserAccountInfo, AppStatus appStatus);

    Assign findOneByFbrUserAccountInfo(FbrUserAccountInfo fbrUserAccountInfo);

    List<Assign> findAllByLawyerAndAppStatus(User lawyer, AppStatus appStatus);

    List<Assign> findAllByAssignTypeAndLawyerAndAppStatus(AssignType assignType, User lawyer, AppStatus appStatus);

    @Query("SELECT assign FROM Assign assign " +
            "JOIN assign.fbrUserAccountInfo fbrUserAccountInfo " +
            "WHERE assign.assignType = :assignType " +
            "AND assign.lawyer = :lawyer " +
            "AND assign.appStatus = :appStatus " +
            "AND fbrUserAccountInfo.fbrUserAccountInfoStatus = :fbrUserAccountInfoStatus")
    List<Assign> findAllByAssignTypeAndLawyerAndAppStatusAndFbrUserAccountInfoStatus(@Param("assignType") AssignType assignType, @Param("lawyer") User lawyer, @Param("appStatus") AppStatus appStatus, @Param("fbrUserAccountInfoStatus") FbrUserAccountInfoStatus fbrUserAccountInfoStatus);

    @Query("SELECT assign FROM Assign assign " +
            "JOIN assign.taxform taxform " +
            "WHERE assign.assignType = :assignType " +
            "AND assign.lawyer = :lawyer " +
            "AND assign.appStatus = :appStatus " +
            "AND taxform.status = :taxformStatus")
    List<Assign> findAllByAssignTypeAndLawyerAndAppStatusAndTaxformStatus(@Param("assignType") AssignType assignType, @Param("lawyer") User lawyer, @Param("appStatus") AppStatus appStatus, @Param("taxformStatus") Taxform_Status taxformStatus);

    List<Assign> findAllByAssignTypeAndTaxformAndAppStatus(AssignType assignType, Taxform taxform, AppStatus appStatus);
    List<Assign> findAllByAssignTypeAndFbrUserAccountInfoAndAppStatus(AssignType assignType, FbrUserAccountInfo fbrUserAccountInfo, AppStatus appStatus);
}
