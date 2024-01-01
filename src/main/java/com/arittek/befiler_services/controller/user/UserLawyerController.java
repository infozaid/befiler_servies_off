package com.arittek.befiler_services.controller.user;

import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.UserDetailBean;
import com.arittek.befiler_services.beans.UserRegistration;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.UserLawyerServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.EncoderDecoder;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MyPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.*;

@RestController
@RequestMapping("/users/lawyer")
public class UserLawyerController {

    private final RoleService roleService;
    private final UserLawyerServices userLawyerServices;
    private final UsersServices usersServices;

    @Autowired
    public UserLawyerController(RoleService roleService, UserLawyerServices userLawyerServices, UsersServices usersServices) {
        this.roleService = roleService;
        this.userLawyerServices = userLawyerServices;
        this.usersServices = usersServices;
    }

    //get Lawyers...
    @RequestMapping( value = "/getLawyers", produces = "application/json",  method = RequestMethod.GET )
    public ResponseEntity<StatusBean> getAllLawyers() {
        try {
            List<User> userList = userLawyerServices.getAllActiveLawyers();
            List<UserDetailBean> userDetailBeanList = new ArrayList<>();
            for(User user : userList){
                UserDetailBean userDetailBean = new UserDetailBean();
                userDetailBean.setStatus(user.getStatus().name());
                userDetailBean.setCnic(user.getCnicNo());
                userDetailBean.setEmail(user.getEmailAddress());
                userDetailBean.setCurrDate(CommonUtil.changeTimestampToString(user.getCreatedDate()));

                userDetailBean.setId(user.getId());
                userDetailBean.setMobileNo(user.getMobileNo());
                userDetailBean.setAddress(user.getAddress());
                userDetailBean.setFirstName(user.getFullName());
                userDetailBeanList.add(userDetailBean);
            }
            StatusBean statusBean = new StatusBean(1,"Success");
            statusBean.setResponse(userDetailBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    //crete Lawyer...
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/lawyerRegisteration", produces = "application/json", method = {RequestMethod.POST})
    public Status createLawyer(@RequestBody UserRegistration userRegistration) throws Exception {
        if (userRegistration != null && userRegistration.getUserLogin() != null && userRegistration.getUserDetail() != null) {

            User user = new User();
            try {
                Role role = roleService.findOneLawyerRole();
                if (role == null) {
                    return new Status(0, "Lawyer role is not defined");
                }

                User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getCnic());
                if (userByCnic != null) {
                    return new Status(0, " A user account with this CNIC number already exists in the system.");
                }

                User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getEmail());
                if (userByEmail != null) {
                    return new Status(0, "A user account with this email account already exists in the system.");
                }

                MyPrint.println("password" + userRegistration.getUserLogin().getPassword());

                boolean hasUppercase = !userRegistration.getUserLogin().getPassword().equals(userRegistration.getUserLogin().getPassword().toLowerCase());
                boolean hasLowercase = !userRegistration.getUserLogin().getPassword().equals(userRegistration.getUserLogin().getPassword().toUpperCase());
                boolean hasNumber = userRegistration.getUserLogin().getPassword().matches(".*\\d.*");
                boolean noSpecialChar = userRegistration.getUserLogin().getPassword().matches("[a-zA-Z0-9 ]*");

                if (userRegistration.getUserLogin().getPassword().length() < 11) {
                    return new Status(0, "Password is too short. Needs to have 11 characters ");
                }

                if (!hasUppercase) {
                    return new Status(0, "Password needs an upper case");
                }

                if (!hasLowercase) {
                    return new Status(0, "Password needs a lowercase ");

                }

                if (!hasNumber) {
                    return new Status(0, "Password needs a number");
                }
                String encodedPassword = EncoderDecoder.getEncryptedSHA1Password(userRegistration.getUserLogin().getPassword());

                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                user.setCnicNo(userRegistration.getUserLogin().getCnic());
                user.setEmailAddress(userRegistration.getUserLogin().getEmail());
                user.setStatus(UserStatus.ACTIVE);
                user.setPassword(encodedPassword);

                user.setFullName(userRegistration.getUserDetail().getFirstName());
                user.setAddress(userRegistration.getUserDetail().getAddress());
                user.setMobileNo(userRegistration.getUserDetail().getMobileNo());

                userLawyerServices.save(user);

                return new Status(1, "Befiler Lawyer's Account has been created.");
            } catch (Exception e) {
                MyPrint.println("__________ Data ROLL Back ____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new Status(0, CommonUtil.getRootCause(e).getMessage());
            }
        } else {
            return new Status(0, "Incomplete Data");
        }
    }

    //update Lawyer...
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/lawyerUpdate", produces = "application/json", method = {RequestMethod.PUT})
    public Status updateLawyer(@RequestBody UserRegistration userRegistration) throws Exception {
        if (userRegistration != null && userRegistration.getUserLogin() != null && userRegistration.getUserDetail() != null) {

            User user = userLawyerServices.findOne(userRegistration.getUserDetail().getId());
            try {

                Role role = roleService.findOneLawyerRole();
                if (role == null) {
                    return new Status(0, "Lawyer role is not defined");
                }

                if(!userRegistration.getUserLogin().getCnic().equals(user.getCnicNo())  && !userRegistration.getUserLogin().getEmail().equals(user.getEmailAddress())) {
                    User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getCnic());
                    if (userByCnic != null) {
                        return new Status(0, " A user account with this CNIC number already exists in the system.");
                    }

                    User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getEmail());
                    if (userByEmail != null) {
                        return new Status(0, "A user account with this email account already exists in the system.");
                    }
                }

                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                user.setCnicNo(userRegistration.getUserLogin().getCnic());
                user.setEmailAddress(userRegistration.getUserLogin().getEmail());
                user.setStatus(UserStatus.ACTIVE);
//                user.setPassword(encodedPassword);

                user.setFullName(userRegistration.getUserDetail().getFirstName());
                user.setAddress(userRegistration.getUserDetail().getAddress());
                user.setMobileNo(userRegistration.getUserDetail().getMobileNo());

                userLawyerServices.update(user);

                return new Status(1, "Befiler Lawyer's Account has been updated.");
            } catch (Exception e) {
                MyPrint.println("__________ Data ROLL Back ____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new Status(0, CommonUtil.getRootCause(e).getMessage());
            }
        } else {
            return new Status(0, "Incomplete Data");
        }
    }

    //remove Lawyer...
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/lawyerRemove", produces = "application/json", method = {RequestMethod.PUT})
    public Status removeLawyer(@RequestBody UserDetailBean userDetailBean) throws Exception {
        if (userDetailBean != null && userDetailBean.getId() != null) {

            User user = userLawyerServices.findOne(userDetailBean.getId());
            try {
                user.setStatus(UserStatus.DELETED);
                userLawyerServices.update(user);

                return new Status(1, "Befiler Lawyer's Account has been deleted.");
            } catch (Exception e) {
                MyPrint.println("__________ Data ROLL Back ____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new Status(0, CommonUtil.getRootCause(e).getMessage());
            }
        } else {
            return new Status(0, "Incomplete Data");
        }
    }
}
