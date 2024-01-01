package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AssignType;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.AssignRepository;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignServicesImpl implements AssignServices {

    private RoleService roleService;
    private AssignRepository assignRepository;
    private TaxformStatusServices taxformStatusServices;

    @Autowired
    public AssignServicesImpl(RoleService roleService, AssignRepository assignRepository, TaxformStatusServices taxformStatusServices) {
        this.roleService = roleService;
        this.assignRepository = assignRepository;
        this.taxformStatusServices = taxformStatusServices;
    }

    @Override
    public Assign findOneByIdAndActiveStatus(Integer assignId) throws Exception {
        if (assignId != null) {
            return assignRepository.findOneByIdAndAppStatus(assignId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public Assign findByLawyerAndTaxformAndAppStatus(User lawyer, Taxform taxform, AppStatus appStatus) throws Exception {
        if (lawyer != null && taxform != null && appStatus != null) {
            return assignRepository.findOneByLawyerAndTaxformAndAppStatus(lawyer, taxform, appStatus);
        }
        return null;
    }

    @Override
    public Assign findOneByLawyerAndFbrUserAccountInfoAndActiveStatus(User lawyer, FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (lawyer != null && fbrUserAccountInfo != null) {
            return assignRepository.findOneByLawyerAndFbrUserAccountInfoAndAppStatus(lawyer, fbrUserAccountInfo, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public Assign findOneByFbrUserAccountInfo(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            return assignRepository.findOneByFbrUserAccountInfo(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public Assign create(Assign assign) throws Exception {
        if (assign != null) {
            return assignRepository.save(assign);
        }
        return null;
    }

    @Override
    public Assign update(Assign assign) throws Exception {
        if (assign != null)
            return assignRepository.save(assign);

        return null;
    }

    @Override
    public List<Assign> findAll() throws Exception {
        return (List<Assign>) assignRepository.findAll();
    }

    /*@Override
    public List<Assign> findAllByTaxform(Taxform taxform) throws Exception {
        if (taxform != null) {
            return (List<Assign>) assignRepository.findAllByTaxform(taxform);
        }
        return null;
    }*/

    @Override
    public List<Assign> findAllByLawyerAndAppStatus(User lawyer, AppStatus appStatus) throws Exception {
        if (lawyer != null && appStatus != null && lawyer.getRoles() != null && lawyer.getRoles().contains(roleService.findOneLawyerRole())) {
            return assignRepository.findAllByLawyerAndAppStatus(lawyer, appStatus);
        }
        return null;
    }

    @Override
    public List<Assign> findAllActiveTaxformsAssignedToLawyer(User lawyer) throws Exception {
        if (lawyer != null && lawyer.getRoles() != null && lawyer.getRoles().contains(roleService.findOneLawyerRole())) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatus(AssignType.TAXFORM, lawyer, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<Assign> findAllActiveNTNAssignedToLawyer(User lawyer) throws Exception {
        if (lawyer != null && lawyer.getRoles() != null && lawyer.getRoles().contains(roleService.findOneLawyerRole())) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatus(AssignType.NTN, lawyer, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<Assign> findAllNtnAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        if (lawyer != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatus(AssignType.NTN, lawyer, AppStatus.ACTIVE);
        }
        return null;
    }

    /*@Override
    public Assign findOneByUserTypeAndTaxformAndAppStatus(UserType userType, Taxform taxform, AppStatus appStatus) throws Exception {
        if (userType != null && taxform != null && appStatus != null) {
            return assignRepository.findByUserTypeAndTaxformAndAppStatus(userType, taxform, appStatus);
        }
        return null;
    }*/

    /*@Override
    public Assign findOneByUserTypeAndFbrUserAccountInfoAndAppStatus(UserType userType, FbrUserAccountInfo fbrUserAccountInfo, AppStatus appStatus) throws Exception {
        if (userType != null && fbrUserAccountInfo != null && appStatus != null) {
            return assignRepository.findByUserTypeAndFbrUserAccountInfoAndAppStatus(userType, fbrUserAccountInfo, appStatus);
        }
        return null;
    }*/

    @Override
    public List<Assign> findAllNewNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        if (lawyer != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndFbrUserAccountInfoStatus(AssignType.NTN, lawyer, AppStatus.ACTIVE, FbrUserAccountInfoStatus.LAWYER_ASSIGN);
        }
        return null;
    }

    @Override
    public List<Assign> findAllOpenNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        if (lawyer != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndFbrUserAccountInfoStatus(AssignType.NTN, lawyer, AppStatus.ACTIVE, FbrUserAccountInfoStatus.LAWYER_OPEN);
        }
        return null;
    }

    @Override
    public List<Assign> findAllCloseNTNAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        if (lawyer != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndFbrUserAccountInfoStatus(AssignType.NTN, lawyer, AppStatus.ACTIVE, FbrUserAccountInfoStatus.LAWYER_CLOSE);
        }
        return null;
    }

    @Override
    public List<Assign> findAllNewTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        Taxform_Status taxformStatus = taxformStatusServices.findOneByLawyerNewStatus();
        if (lawyer != null && taxformStatus != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndTaxformStatus(AssignType.TAXFORM, lawyer, AppStatus.ACTIVE, taxformStatus);
        }
        return null;
    }

    @Override
    public List<Assign> findAllOpenTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        Taxform_Status taxformStatus = taxformStatusServices.findOneByLawyerPendingStatus();
        if (lawyer != null && taxformStatus != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndTaxformStatus(AssignType.TAXFORM, lawyer, AppStatus.ACTIVE, taxformStatus);
        }
        return null;
    }

    @Override
    public List<Assign> findAllCloseTaxformAssignByLawyerAndActiveStatus(User lawyer) throws Exception {
        Taxform_Status taxformStatus = taxformStatusServices.findOneByLawyerCompleteStatus();
        if (lawyer != null && taxformStatus != null) {
            return assignRepository.findAllByAssignTypeAndLawyerAndAppStatusAndTaxformStatus(AssignType.TAXFORM, lawyer, AppStatus.ACTIVE, taxformStatus);
        }
        return null;
    }

    @Override
    public Assign checkIfTaxformIsAssignedToLawyer(Taxform taxform) throws Exception {
        if (taxform != null) {
            List<Assign> assignList = assignRepository.findAllByAssignTypeAndTaxformAndAppStatus(AssignType.TAXFORM, taxform, AppStatus.ACTIVE);
            if (assignList != null && assignList.size() > 0) {
                for (Assign assign : assignList) {
                    if (assign.getLawyer() != null && assign.getLawyer().getRoles() != null && assign.getLawyer().getRoles().contains(roleService.findOneLawyerRole())) {
                        return assign;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Assign checkIfFbrUserAccountInfoIsAssignedToLawyer(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            List<Assign> assignList = assignRepository.findAllByAssignTypeAndFbrUserAccountInfoAndAppStatus(AssignType.NTN, fbrUserAccountInfo, AppStatus.ACTIVE);
            if (assignList != null && assignList.size() > 0) {
                for (Assign assign : assignList) {
                    if (assign.getLawyer() != null && assign.getLawyer().getRoles() != null && assign.getLawyer().getRoles().contains(roleService.findOneLawyerRole())) {
                        return assign;
                    }
                }
            }
        }
        return null;
    }
}

