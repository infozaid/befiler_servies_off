package com.arittek.befiler_services.services;


import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface AssignServices {

    Assign findOneByIdAndActiveStatus(Integer assignId) throws Exception;

    Assign findByLawyerAndTaxformAndAppStatus(User lawyer, Taxform taxform, AppStatus appStatus) throws Exception;

    Assign findOneByLawyerAndFbrUserAccountInfoAndActiveStatus(User lawyer, FbrUserAccountInfo ntn) throws Exception;

    Assign findOneByFbrUserAccountInfo(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;

    /*Assign findOneByUserTypeAndTaxformAndAppStatus(UserType userType, Taxform taxform, AppStatus appStatus) throws Exception;*/
    /*Assign findOneByUserTypeAndFbrUserAccountInfoAndAppStatus(UserType userType, FbrUserAccountInfo fbrUserAccountInfo, AppStatus appStatus) throws Exception;*/

    Assign create(Assign taxformAssign) throws Exception;
    Assign update(Assign taxformAssign) throws Exception;

    List<Assign> findAll() throws Exception;

    List<Assign> findAllByLawyerAndAppStatus(User lawyer, AppStatus appStatus) throws Exception;

    List<Assign> findAllActiveTaxformsAssignedToLawyer(User lawyer) throws Exception;
    List<Assign> findAllActiveNTNAssignedToLawyer(User lawyer) throws Exception;

    List<Assign> findAllNtnAssignByLawyerAndActiveStatus(User lawyer) throws Exception;

    /*List<Assign> findAllByTaxform(Taxform taxform) throws Exception;*/

    List<Assign> findAllNewNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception;
    List<Assign> findAllOpenNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception;
    List<Assign> findAllCloseNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception;

    List<Assign> findAllNewTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception;
    List<Assign> findAllOpenTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception;
    List<Assign> findAllCloseTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception;

    Assign checkIfTaxformIsAssignedToLawyer(Taxform taxform) throws Exception;
    Assign checkIfFbrUserAccountInfoIsAssignedToLawyer(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;
}
