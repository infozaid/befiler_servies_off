package com.arittek.befiler_services.controller.user;

import com.arittek.befiler_services.beans.UserPinBean;
import com.arittek.befiler_services.beans.userModule.MinimalUserBean;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Settings;
import com.arittek.befiler_services.model.user.UserPin;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserLoginAttempts;
import com.arittek.befiler_services.repository.user.UserLoginAttemptsRepository;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.security.JwtAuthenticationRequest;
import com.arittek.befiler_services.security.JwtTokenUtil;
import com.arittek.befiler_services.security.UserAuthenticationProvider;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.user.UserPinServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.*;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

@RestController
public class UserLoginController {

    @Value("${jwt.header}")
    private String tokenHeader;
    private UsersServices usersServices;
    private UserAuthenticationProvider userAuthenticationProvider;
    private JwtTokenUtil jwtTokenUtil;
    private UserLoginAttemptsRepository userLoginAttemptsRepository;
    private UserPinServices userPinServices;
    private SettingsServices settingsServices;

    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    public UserLoginController(UsersServices usersServices, UserAuthenticationProvider userAuthenticationProvider, JwtTokenUtil jwtTokenUtil, UserLoginAttemptsRepository userLoginAttemptsRepository, UserPinServices userPinServices, SettingsServices settingsServices, UserDetailsService userDetailsService) {
        this.usersServices = usersServices;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userLoginAttemptsRepository = userLoginAttemptsRepository;
        this.userPinServices = userPinServices;
        this.settingsServices = settingsServices;
        this.userDetailsService = userDetailsService;
    }

    Integer attemptsCount;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, Device device, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, Exception {

        MyPrint.println("Device ::::: Mobile :: " + device.isMobile() + " :: Normal :: " + device.isNormal() + " :: Tablet :: " + device.isTablet());

        if (authenticationRequest.getUsername() != null && authenticationRequest.getPassword() != null) {

            User username = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(authenticationRequest.getUsername()); // check if available into database returns
            UserPin userPin = new UserPin();
            UserLoginAttempts userLoginAttempts = new UserLoginAttempts();

            if (username != null) {
                userLoginAttempts.setUser(username);
                userLoginAttempts.setIpAddress(request.getRemoteAddr());
                userLoginAttempts.setMacAddress(MacAddress.getMac(request.getRemoteAddr()));

                final Authentication authentication = userAuthenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                ));

                if (authentication != null) {

                    userLoginAttempts.setStatus(true);
                    usersServices.createUserLoginAttempt(userLoginAttempts);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Logger4j.getLogger().info("Inside Auth Service :: 2");

                    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
                    /*final String token = jwtTokenUtil.generateToken(userDetails, device);*/
                    final String token = jwtTokenUtil.generateToken(userDetails);

                    if(username.getStatus().getId()==0 ) {

                        String autoCode = getAutoGenerateCode();
                        userPin.setStatus(true);
                        userPin.setUser(username);
                        userPin.setCode(autoCode);
                        userPin.setGenerateDate(CommonUtil.getCurrentTimestamp());
                        userPin.setExpiredDate(CommonUtil.getExpireTimestamp());

                        MyPrint.println(autoCode+" code");

                        try {
                            EmailSender.sendEmail(EmailUtil.pinCodeTemplete(username,autoCode),"Activation Pin Code", username.getEmailAddress());
                            MyPrint.println("code sent to your email : ");

                        } catch (Exception e) {
                            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                            Logger4j.getLogger().error("Exception : " , e);
                        }

                        userPinServices.save(userPin);

                        MinimalUserBean minimalUserBean = new MinimalUserBean();
                        minimalUserBean.setStatus(2);
                        minimalUserBean.setMessage("Lock");
                        minimalUserBean.setUserId(username.getId());

                        return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
                    }

                    MinimalUserBean minimalUserBean = new MinimalUserBean();
                    minimalUserBean.setStatus(1);
                    minimalUserBean.setMessage("Active");
                    minimalUserBean.setToken(token);
                    minimalUserBean.setUserId(username.getId());
                    minimalUserBean.setFirstName(username.getFullName());
                    minimalUserBean.setCnic(username.getCnicNo());
                    minimalUserBean.setEmail(username.getEmailAddress());
                    minimalUserBean.setMobileNo(username.getMobileNo());
                    if(username.getRoles().size()>0){
                        for (Role role:username.getRoles()){
                            minimalUserBean.setRoleId(role.getId());
                            minimalUserBean.setRole(role.getDisplay_name());
                            minimalUserBean.setPermissionList(role.getPermissionsNameList());
                        }
                    }
                    return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
                } else {
                    Settings settings = settingsServices.getActiveRecord();

                    Integer maxLoginAttemptsCounts;
                    if (settings != null && settings.getMaxLoginAttempts() != null) {
                        maxLoginAttemptsCounts = settings.getMaxLoginAttempts();
                    } else {
                        maxLoginAttemptsCounts = 3;
                    }

                    MyPrint.println(username.getEmailAddress());
                    attemptsCount = usersServices.userIncorrectLoginAttemptsCount(username);

                    if (attemptsCount != null && attemptsCount >= maxLoginAttemptsCounts) {
                        username.setStatus(UserStatus.DEACTIVE);
                        usersServices.update(username);

                        if(authentication != null) {
                            MyPrint.println(authenticationRequest.getUsername());
                            MyPrint.println(authenticationRequest.getPassword());

                            userPin.setUser(username);
                            userPin.setGenerateDate(CommonUtil.getCurrentTimestamp());
                            userPinServices.save(userPin);
                        }
                    }
                    userLoginAttempts.setStatus(false);
                    usersServices.createUserLoginAttempt(userLoginAttempts);
                }
            } else {
                userLoginAttempts.setStatus(false);
                userLoginAttempts.setIpAddress(request.getRemoteAddr());
                userLoginAttempts.setMacAddress(MacAddress.getMac(request.getRemoteAddr()));
                if (authenticationRequest.getUsername() != null) {
                    userLoginAttempts.setUserName(authenticationRequest.getUsername());
                }
                if (authenticationRequest.getPassword() != null) {
                    userLoginAttempts.setPassword(authenticationRequest.getPassword());
                }
                usersServices.createUserLoginAttempt(userLoginAttempts);
            }
        }
        MinimalUserBean minimalUserBean = new MinimalUserBean();
        minimalUserBean.setStatus(0);
        return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
    }


    @RequestMapping(value = "/verifyPin", method = RequestMethod.POST)
    public ResponseEntity<MinimalUserBean> verifyPin(@RequestBody UserPinBean userPinBean, Device device) {
        if(userPinBean != null && userPinBean.getUserId() != null && userPinBean.getCode() != null) {
            try {
                User user = usersServices.findOneByIdAndStatus(userPinBean.getUserId(), UserStatus.DEACTIVE);
                if (user != null) {
                    MyPrint.println("user :::::::::::::::id: "+user.getId());

                    UserPin userPin = userPinServices.findAllByUserAndCode(user,userPinBean.getCode());
                    if(userPin != null) {

                        java.sql.Timestamp currentTimestamp = CommonUtil.getCurrentTimestamp();
                        java.sql.Timestamp expireTimestamp = userPin.getExpiredDate();

                        if(currentTimestamp.after(expireTimestamp)){ // baad men
                            userPin.setStatus(false);
                            userPinServices.update(userPin);

                            MyPrint.println("Your code time has been expired on : "+expireTimestamp);

                            MinimalUserBean minimalUserBean = new MinimalUserBean();
                            minimalUserBean.setMessage("code date has been expired on "+expireTimestamp);
                            minimalUserBean.setStatus(0);
                            return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);

                        } else if(currentTimestamp.before(expireTimestamp)) {
                            userPin.setStatus(false);
                            userPin.setVerifiedDate(currentTimestamp);
                            userPinServices.update(userPin);

                            user.setStatus(UserStatus.ACTIVE);
                            usersServices.update(user);

                            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmailAddress());
                            /*final String token = jwtTokenUtil.generateToken(userDetails, device);*/
                            final String token = jwtTokenUtil.generateToken(userDetails);

                            MinimalUserBean minimalUserBean = new MinimalUserBean();
                            minimalUserBean.setStatus(1);
                            minimalUserBean.setMessage("your account has been unlocked! now : ");
                            minimalUserBean.setToken(token);
                            minimalUserBean.setUserId(user.getId());
                            minimalUserBean.setFirstName(user.getFullName());
                            minimalUserBean.setCnic(user.getCnicNo());
                            minimalUserBean.setEmail(user.getEmailAddress());
                            minimalUserBean.setMobileNo(user.getMobileNo());

                            if(user.getRoles().size()>0){
                                for (Role role:user.getRoles()){
                                    minimalUserBean.setRoleId(role.getId());
                                    minimalUserBean.setRole(role.getDisplay_name());
                                    minimalUserBean.setPermissionList(role.getPermissionsNameList());
                                }
                            }

                            MyPrint.println(" current date : "+currentTimestamp);
                            return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
                        }
                    }
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                MinimalUserBean minimalUserBean = new MinimalUserBean();
                minimalUserBean.setStatus(0);
                minimalUserBean.setMessage("Incomplete data");
                return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
            }
        }

        MinimalUserBean minimalUserBean = new MinimalUserBean();
        minimalUserBean.setStatus(0);
        minimalUserBean.setMessage("Invalid Code");
        return new ResponseEntity<>(minimalUserBean, HttpStatus.OK);
    }

    protected String getAutoGenerateCode() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
}
