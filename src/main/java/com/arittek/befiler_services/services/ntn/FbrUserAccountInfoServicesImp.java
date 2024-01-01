package com.arittek.befiler_services.services.ntn;

import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.ntn.FbrUserAccountInfoRepository;
import com.arittek.befiler_services.services.AssignServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FbrUserAccountInfoServicesImp implements FbrUserAccountInfoServices {

    private FbrUserAccountInfoRepository fbrUserAccountInfoRepository;
    private UsersServices usersServices;
    private AssignServices assignServices;

    @Autowired
    public FbrUserAccountInfoServicesImp(FbrUserAccountInfoRepository fbrUserAccountInfoRepository,UsersServices usersServices, AssignServices assignServices) {
        this.fbrUserAccountInfoRepository = fbrUserAccountInfoRepository;
        this.usersServices = usersServices;
        this.assignServices = assignServices;
    }

    @Override
    public FbrUserAccountInfo saveOrUpdate(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            return fbrUserAccountInfoRepository.save(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public FbrUserAccountInfo findOneByUser(User user) {
        if(user != null){
            return fbrUserAccountInfoRepository.findOneByUser(user);
        }
        return null;
    }

    @Override
    public FbrUserAccountInfo updateFbrUserAccountInfoToAccountantStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.ACCOUNTANT);

            return fbrUserAccountInfoRepository.save(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public FbrUserAccountInfo updateFbrUserAccountInfoToLawyerAssignStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.LAWYER_ASSIGN);

            return fbrUserAccountInfoRepository.save(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public FbrUserAccountInfo updateFbrUserAccountInfoToLawyerOpenStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.LAWYER_OPEN);

            return fbrUserAccountInfoRepository.save(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public FbrUserAccountInfo updateFbrUserAccountInfoToLawyerCloseStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null) {
            fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.LAWYER_CLOSE);

            return fbrUserAccountInfoRepository.save(fbrUserAccountInfo);
        }
        return null;
    }

    @Override
    public List<FbrUserAccountInfo> findAllByAccountantStatus() throws Exception {
        return fbrUserAccountInfoRepository.findAllByFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.ACCOUNTANT);
    }

    @Override
    public List<FbrUserAccountInfo> findAllByUserRoleAndStatus(User user) throws Exception {
        if (user != null) {

            if (usersServices.checkIfUserIsCustomer(user)) {
                return fbrUserAccountInfoRepository.findAllByUser(user);
            } else if (usersServices.checkIfUserIsLawyer(user)) {

                //TODO uncomment this code afte authoassign -- done
                List<Assign> ntnAssignList = assignServices.findAllByLawyerAndAppStatus(user, AppStatus.ACTIVE);
                List<FbrUserAccountInfo> taxformList = new ArrayList<>();
                if (ntnAssignList != null) {
                    for (Assign ntnAssign : ntnAssignList) {
                        if (ntnAssign != null && ntnAssign.getFbrUserAccountInfo() != null) {
                            taxformList.add(ntnAssign.getFbrUserAccountInfo());
                        }
                    }
                }
                return taxformList;
            } else if (usersServices.checkIfUserIsAdmin(user) || usersServices.checkIfUserIsAccountant(user)) {
                return (List<FbrUserAccountInfo>) fbrUserAccountInfoRepository.findAll();
            }
        }
        return null;
    }


}