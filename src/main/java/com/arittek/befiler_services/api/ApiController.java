package com.arittek.befiler_services.api;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final RoleService roleService;
    private final UsersServices usersServices;

    @Autowired
    public ApiController(RoleService roleService, UsersServices usersServices) {
        this.roleService = roleService;
        this.usersServices = usersServices;
    }


    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/customer/registration", produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<UserRegistrationResponseBean> createUser(@RequestBody UserRegistrationRequestBean userRegistrationRequestBean) throws Exception {

        Logger4j.getLogger().info("Inside");

        if (!StringUtils.isNotEmpty(userRegistrationRequestBean.getFullName())) {return new ResponseEntity<>(new UserRegistrationResponseBean(0,"Full Name is mandatory"), HttpStatus.OK);}
        if (!StringUtils.isNotEmpty(userRegistrationRequestBean.getCnicNo())) {return new ResponseEntity<>(new UserRegistrationResponseBean(0,"CNIC No is mandatory"), HttpStatus.OK);}
        if (!StringUtils.isNotEmpty(userRegistrationRequestBean.getEmailAddress())) {return new ResponseEntity<>(new UserRegistrationResponseBean(0,"Email Address is mandatory"), HttpStatus.OK);}
        if (!StringUtils.isNotEmpty(userRegistrationRequestBean.getMobileNo())) {return new ResponseEntity<>(new UserRegistrationResponseBean(0,"Mobile No is mandatory"), HttpStatus.OK);}

        try {

            User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(userRegistrationRequestBean.getCnicNo());
            if (userByCnic != null) {
                return new ResponseEntity<>(new UserRegistrationResponseBean(0,"A user account with this CNIC number already exists in the system."), HttpStatus.OK);
            }

            User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(userRegistrationRequestBean.getEmailAddress());
            if (userByEmail != null) {
                return new ResponseEntity<>(new UserRegistrationResponseBean(0,"A user account with this email account already exists in the system."), HttpStatus.OK);
            }

            Role role = roleService.findOneCustomerRole();
            if (role == null) {
                return new ResponseEntity<>(new UserRegistrationResponseBean(0,"Internal server error, customer role"), HttpStatus.OK);
            }

            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            String pwd = RandomStringUtils.random(30, characters);

            User user = new User();

            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            user.setCnicNo(userRegistrationRequestBean.getCnicNo());
            user.setEmailAddress(userRegistrationRequestBean.getEmailAddress());
            user.setStatus(UserStatus.DEACTIVE);
            user.setPassword(pwd);

            user.setFullName(userRegistrationRequestBean.getFullName());
            user.setMobileNo(userRegistrationRequestBean.getMobileNo());

            usersServices.create(user);

            String url = "https://befiler.com/access/confirmpassword" + "?id=" + user.getId() + "&token=" + user.getPassword();
            String emailTemplate= EmailUtil.emailTemaplate(url,user.getFullName());

            EmailSender.sendEmail(emailTemplate, "Welcome To Befiler", user.getEmailAddress());
            return new ResponseEntity<>(new UserRegistrationResponseBean(1,"Thank you for joining Befiler, Please check your email"), HttpStatus.OK);
        } catch (Exception e) {
            MyPrint.println("___________ Data ROLL Back _____________");
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new UserRegistrationResponseBean(0,"Internal Server Error"), HttpStatus.OK);
        }
    }
}
