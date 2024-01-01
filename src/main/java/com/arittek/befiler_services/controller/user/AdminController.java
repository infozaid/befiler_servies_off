package com.arittek.befiler_services.controller.user;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.*;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UsersServices usersServices;
    private final RoleService roleService;

    @Autowired
    public AdminController(UsersServices usersServices, RoleService roleService) {
        this.usersServices = usersServices;
        this.roleService = roleService;
    }

    /*REGISTER USER*/

    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.POST})
    public @ResponseBody
    ResponseEntity<Status> createUser(@RequestBody UserRegistration userRegistration) throws Exception {
        try {

            if (userRegistration != null && userRegistration.getUserLogin() != null && userRegistration.getUserDetail() != null && userRegistration.getUrl() != null) {


                if (!StringUtils.isNotEmpty(userRegistration.getUserLogin().getEmail())) {
                    return new ResponseEntity<>(new Status(0, "Please enter email address."), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(userRegistration.getUserLogin().getCnic())) {
                    return new ResponseEntity<>(new Status(0, "Please enter cnic no."), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(userRegistration.getUserDetail().getFirstName())) {
                    return new ResponseEntity<>(new Status(0, "Please enter full name."), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(userRegistration.getUserDetail().getMobileNo())) {
                    return new ResponseEntity<>(new Status(0, "Please enter mobile no."), HttpStatus.OK);
                }

                /*CHECK IF AUTHORIZER IS LOGGED IN*/
                User authorizer;
                if (userRegistration.getAuthorizerId() != null) {
                    authorizer = usersServices.findOneByIdAndStatus(userRegistration.getAuthorizerId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                }

                /*USER ROLE CHECK*/
                Role role;
                if (authorizer != null && authorizer.getRoles() != null && authorizer.getRoles().contains(roleService.findOneOperationsRole())) {
                    role = roleService.findOneCustomerRole();
                    if (role == null) {
                        return new ResponseEntity<>(new Status(0, "Customer role is not defined"), HttpStatus.OK);
                    }
                } else {
                    if (userRegistration.getRoleId() != null) {
                        role = roleService.findOne(userRegistration.getRoleId());
                        if (role == null) {
                            return new ResponseEntity<>(new Status(0, "Selected Role is not defined."), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Select User Role."), HttpStatus.OK);
                    }
                }

                /*CHECK FOR DUPLICATE USER*/

                User findFromDataBase = usersServices.findByEmailAddressOrCnicNo(userRegistration.getUserLogin().getEmail(), userRegistration.getUserLogin().getCnic());
                if (findFromDataBase != null) {
                    return new ResponseEntity<>(new Status(0, "User is already registered"), HttpStatus.OK);
                }


                /*GENERATE RANDOM PASSWORD*/
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                String pwd = RandomStringUtils.random(30, characters);
                MyPrint.println(pwd);

                /*REGISTER USER*/
                User user = new User();

                /*if (authorizer != null && authorizer.getRoles() != null && authorizer.getRoles().contains(roleService.findOneOperationsRole())) {
                    user.setCreatedBy(authorizer);
                }*/

                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                user.setStatus(UserStatus.DEACTIVE);

                user.setCnicNo(userRegistration.getUserLogin().getCnic());
                user.setEmailAddress(userRegistration.getUserLogin().getEmail());
                user.setPassword(pwd);

                user.setLoginAttempts(0);

                user.setFullName(userRegistration.getUserDetail().getFirstName());
                user.setMobileNo(userRegistration.getUserDetail().getMobileNo());
                user.setAddress(userRegistration.getUserDetail().getAddress());

                User persistUser = usersServices.create(user);
                if (persistUser == null) {
                    throw new Exception("Can't Create User");
                }

                MyPrint.println("URL::::" + userRegistration.getUrl());

                String url = userRegistration.getUrl() + "?id=" + user.getId() + "&token=" + user.getPassword();
                String emailTemplate = EmailUtil.emailTemaplate(url, user.getFullName());
                EmailSender.sendEmail(emailTemplate, "Welcome To Befiler", user.getEmailAddress());

                return new ResponseEntity<>(new Status(1, "Thank you for joining Befiler, Please check your email"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
        } catch (Exception e) {
            MyPrint.println("___________ Data ROLL Back _____________");
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new Status(0, CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
        }

    }


    /*UPDATE USER*/
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.PUT})
    public ResponseEntity<Status> updateUser(@RequestBody UserRegistration userRegistration) throws Exception {

        if (userRegistration != null &&
                userRegistration.getUserLogin() != null &&
                userRegistration.getUserLogin().getId() != null &&
                userRegistration.getUserDetail() != null &&
                userRegistration.getUserDetail().getId() != null) {
            try {

                /*GET USER AND USER DETAIL BY ID*/
                /*User user = usersServices.findOneByIdAndStatus(userRegistration.getUserLogin().getId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(userRegistration.getUserLogin().getId(), UserStatus.ACTIVE);
                /*UserDetail userDetail = userDetailServices.findOne(userRegistration.getUserDetail().getId());*/

                if (user != null/* && userDetail != null*/) {

                    /*USER TYPE CHECK*/
//                    UserType userType;
//                    if (userRegistration.getUserDetail().getUserType() != null && userRegistration.getUserDetail().getUserType().getId() != null) {
//                        userType = usersServices.findUserTypeById(userRegistration.getUserDetail().getUserType().getId());
//                        if (userType == null) {
//                            return new ResponseEntity<>(new Status(0, "Select User Type."), HttpStatus.OK);
//                        }
//                    } else {
//                        return new ResponseEntity<>(new Status(0, "Select User Type."), HttpStatus.OK);
//                    }

                    /*USER ROLE CHECK*/
                    Role role;
                    if (userRegistration.getRoleId() != null) {
                        role = roleService.findOne(userRegistration.getRoleId());
                        if (role == null) {
                            return new ResponseEntity<>(new Status(0, "Selected Role is not defined."), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Select User Role."), HttpStatus.OK);
                    }

                    /*CHECK IF AUTHORIZER IS LOGGED IN*/
                    User authorizer;
                    if (userRegistration.getAuthorizerId() != null) {
                        /*authorizer = usersServices.findOneByIdAndStatus(userRegistration.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                        authorizer = usersServices.findOneByIdAndStatus(userRegistration.getAuthorizerId(), UserStatus.ACTIVE);
                        if (authorizer == null) {
                            return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                    }

                    /*CHECK FOR DUPLICATE USER*/
                    if (userRegistration.getUserLogin() != null &&
                            userRegistration.getUserLogin().getEmail() != null &&
                            !userRegistration.getUserLogin().getEmail().isEmpty()) {

                        User findFromDataBase = usersServices.findOneByEmailAddressAndIdNotIn(userRegistration.getUserLogin().getEmail(), user.getId());
                        if (findFromDataBase != null) {
                            return new ResponseEntity<>(new Status(0, "A user account with this email account already exists in the system."), HttpStatus.OK);
                        }

                    } else {
                        return new ResponseEntity<>(new Status(0, "Please enter email"), HttpStatus.OK);
                    }

                    if (userRegistration.getUserLogin() != null &&
                            userRegistration.getUserLogin().getCnic() != null &&
                            !userRegistration.getUserLogin().getCnic().isEmpty()) {

                        User findFromDataBase = usersServices.findOneByCnicNoAndIdNotIn(userRegistration.getUserLogin().getCnic(), user.getId());
                        if (findFromDataBase != null) {
                            return new ResponseEntity<>(new Status(0, "A user account with this cnic account already exists in the system."), HttpStatus.OK);
                        }

                    } else {
                        return new ResponseEntity<>(new Status(0, "Please enter CNIC"), HttpStatus.OK);
                    }

//                    user.setUserType(userType);
                    Set<Role> roles = new HashSet<>();
                    roles.add(role);
                    user.setRoles(roles);
                    /*user.setStatus(usersServices.findUserStatusById(1));*/
                    user.setStatus(UserStatus.ACTIVE);

                    user.setCnicNo(userRegistration.getUserLogin().getCnic());
                    user.setEmailAddress(userRegistration.getUserLogin().getEmail());

                    if (userRegistration.getUserDetail().getFirstName() != null && !userRegistration.getUserDetail().getFirstName().isEmpty()) {
                        user.setFullName(userRegistration.getUserDetail().getFirstName());
                    } else {
                        throw new Exception("Please enter first name");
                    }

                    if (userRegistration.getUserDetail().getMobileNo() != null && !userRegistration.getUserDetail().getMobileNo().isEmpty()) {
                        user.setMobileNo(userRegistration.getUserDetail().getMobileNo());
                    } else {
                        throw new Exception("Please enter mobile number");
                    }
                    user.setAddress(userRegistration.getUserDetail().getAddress());


                    User persistUser = usersServices.update(user);
                    if (persistUser == null) {
                        throw new Exception("Can't Create User");
                    }


                    return new ResponseEntity<>(new Status(1, "User updated successfully."), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Status(1, "Please activate user!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                MyPrint.println("___________ Data ROLL Back _____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new Status(0, CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    /*Remove USER*/
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/remove", produces = "application/json", method = {RequestMethod.PUT})
    public ResponseEntity<Status> removeUser(@RequestBody UserRegistration userRegistration) throws Exception {

        if (userRegistration != null &&
                userRegistration.getUserDetail() != null &&
                userRegistration.getUserDetail().getUserId() != null) {
            try {

                /*GET USER AND USER DETAIL BY ID*/
                User user = usersServices.findOneById(userRegistration.getUserDetail().getUserId());

                if (user != null) {

                    /*CHECK IF AUTHORIZER IS LOGGED IN*/
                    User authorizer;
                    if (userRegistration.getAuthorizerId() != null) {
                        authorizer = usersServices.findOneByIdAndStatus(userRegistration.getAuthorizerId(), UserStatus.ACTIVE);
                        if (authorizer == null) {
                            return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Session Expired."), HttpStatus.OK);
                    }

                    if (user.getStatus() != null) {
                        user.setStatus(UserStatus.DELETED);
                        User persistUser = usersServices.update(user);
                        if (persistUser == null) {
                            throw new Exception("Can't Delete User");
                        }
                    }

                    return new ResponseEntity<>(new Status(1, "User Account has been deleted Successfully."), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Status(1, "Please activate user!"), HttpStatus.OK);
                }
            } catch (Exception e) {
                MyPrint.println("__________ Data ROLL Back ____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new Status(0, CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> getUserList(@RequestBody InputBean inputBean) {
        try {
            if (inputBean != null) {

                User authorizer;
                if (inputBean.getAuthorizerId() != null) {
                    /*authorizer = usersServices.findOneByIdAndStatus(inputBean.getAuthorizerId(), usersServices.findUserStatusById(1));*/
                    authorizer = usersServices.findOneByIdAndStatus(inputBean.getAuthorizerId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Session Expired."), HttpStatus.OK);
                }

                List<User> userList = usersServices.findAllByAuthorizerRole(authorizer);

                if (userList != null) {
                    List<DetailedUserBean> userDetailedList = new ArrayList<>();
                    for (User user : userList) {
                        DetailedUserBean userDetailedBean = new DetailedUserBean();
                        userDetailedBean.setUserId(user.getId());
                        userDetailedBean.setCnic(user.getCnicNo());
                        userDetailedBean.setEmail(user.getEmailAddress());
                        userDetailedBean.setCreatedDate(CommonUtil.changeTimestampToString(user.getCreatedDate()));
                        if (user.getRoles() != null) {
                            for (Role role : user.getRoles()) {
                                userDetailedBean.setRoleId(role.getId());
                                userDetailedBean.setRoleName(role.getName());
                                break;
                            }
                        }
                        /*userDetailedBean.setStatus(user.getStatus().getStatus());*/
                        /*userDetailedBean.setStatus(user.getStatus().toString());
                        if (user.getUserDetail() != null) {
                            userDetailedBean.setUserDetailId(user.getUserDetail().getId());
                            userDetailedBean.setFirstName(user.getUserDetail().getFirstName());
                            userDetailedBean.setLastName(user.getUserDetail().getLastName());
                            userDetailedBean.setMobileNo(user.getUserDetail().getMobileNo());
                            userDetailedBean.setAddress(user.getUserDetail().getAddress());
                        }*/

                        userDetailedBean.setFirstName(user.getFullName());
                        userDetailedBean.setMobileNo(user.getMobileNo());
                        userDetailedBean.setAddress(user.getAddress());
                        userDetailedBean.setStatus(user.getStatus().name());

                        userDetailedList.add(userDetailedBean);
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(userDetailedList);
                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }
}
