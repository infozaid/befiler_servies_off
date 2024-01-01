package com.arittek.befiler_services.services.ntn;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface FbrUserAccountInfoServices {

    FbrUserAccountInfo saveOrUpdate(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;

    FbrUserAccountInfo findOneByUser(User user);

    FbrUserAccountInfo updateFbrUserAccountInfoToAccountantStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;
    FbrUserAccountInfo updateFbrUserAccountInfoToLawyerAssignStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;
    FbrUserAccountInfo updateFbrUserAccountInfoToLawyerOpenStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;
    FbrUserAccountInfo updateFbrUserAccountInfoToLawyerCloseStatus(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;

    List<FbrUserAccountInfo> findAllByAccountantStatus() throws Exception;

    List<FbrUserAccountInfo> findAllByUserRoleAndStatus(User user) throws Exception;

}
