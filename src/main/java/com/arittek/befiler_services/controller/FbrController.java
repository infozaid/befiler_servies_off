package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.FbrRegisteredUser;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.FbrRegisteredUserRepository;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.*;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/fbr")
public class FbrController {

    private UsersServices usersServices;
    private FbrRegisteredUserRepository fbrRegisterdUserRepository;

    @Autowired
    public FbrController(UsersServices usersServices, FbrRegisteredUserRepository fbrRegisterdUserRepository) {
        this.usersServices = usersServices;
        this.fbrRegisterdUserRepository = fbrRegisterdUserRepository;
    }

    FbrAllService fbrAllService=new FbrAllService();

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<FbrCaputerCodeBean> getFbrRegistration() throws Exception {
        FbrCaputerCodeBean bean=new FbrCaputerCodeBean();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String pwd = RandomStringUtils.random(6, characters);
        MyPrint.println(pwd);
        try {
            bean = fbrAllService.getFbrCaptureCodeFromFbrWebSite(pwd);  //one just for ignoring null value it will be remove when user send its usrId
            bean.setCaputreUrl(bean.getCaputreUrl());
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "refresh" ,produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<FbrCaputerCodeBean> refreshCaptcha() throws Exception {
        FbrCaputerCodeBean bean = new FbrCaputerCodeBean();
        try {
            bean = fbrAllService.refreshCaptcha(); //one just for ignoring null value it will be remove when user send its usrId
            bean.setCaputreUrl(bean.getCaputreUrl());
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }
    }


    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/getFederalNTN" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FbrBean> getTaxFormMinimal(@RequestBody FbrInputBean fbrInputBean) {
        MyPrint.println("Inputs-----------------"+fbrInputBean.getSearchBy()+" --"+fbrInputBean.getParm1()+"---"+fbrInputBean.getParm2());
        try {
            //SEARCH BY 1 FOR NTN NUMBER
            FbrBean bean=FbrUtil.getFbrNTN(fbrInputBean.getSearchBy(),fbrInputBean.getParm1(),fbrInputBean.getParm2());
            System.out.println("..............................-----------"+bean.getName());
            return new ResponseEntity<>(bean, HttpStatus.OK);

        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new FbrBean(), HttpStatus.OK);
        }
    }



    /*@SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/userAndFbrRegistration", produces = "application/json", method = {RequestMethod.POST})
    public Status createUsers(@RequestBody FbrAndUserRegistrationBean fbrAndUserRegistrationBean) throws Exception {
        if (fbrAndUserRegistrationBean != null && fbrAndUserRegistrationBean.getUserLogin() != null && fbrAndUserRegistrationBean.getUserDetail() != null) {
            FbrCaputerCodeBean res = new FbrCaputerCodeBean();
            try {
                CorporateEmployee corporateEmployee = corporateEmployeeServices.findCorporateEmployeeByCnicNo(fbrAndUserRegistrationBean.getUserLogin().getCnic());
                User user = new User();

                if (fbrAndUserRegistrationBean.getUserDetail().getCaptcha() != null && fbrAndUserRegistrationBean.getUserDetail().getCnic() != null && fbrAndUserRegistrationBean.getUserDetail().getPrefix() != null && fbrAndUserRegistrationBean.getUserDetail().getEmail() != null ) {


                    Status status = fbrAllService.submitFbrRegistration1(fbrAndUserRegistrationBean);
                    FbrRegisteredUser fbrRegisteredUser=new FbrRegisteredUser();
                    fbrRegisteredUser.setPrefix(fbrAndUserRegistrationBean.getUserDetail().getPrefix());
                    fbrRegisteredUser.setFirstName(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
                    fbrRegisteredUser.setLastName(fbrAndUserRegistrationBean.getUserDetail().getLastName());
                    fbrRegisteredUser.setCellNo(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                    fbrRegisteredUser.setEmail(fbrAndUserRegistrationBean.getUserDetail().getEmail());
                    fbrRegisteredUser.setEmailConfig(fbrAndUserRegistrationBean.getUserDetail().getEmail());
                    fbrRegisteredUser.setCurrentDae(CommonUtil.getCurrentTimestamp());
                    fbrRegisteredUser.setCaptcha(fbrAndUserRegistrationBean.getUserDetail().getCaptcha());
                    fbrRegisteredUser.setCnic(fbrAndUserRegistrationBean.getUserDetail().getCnic());
                    fbrRegisteredUser.setCellNoConfig(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());

                    *//*fbrRegisteredUser.setUserFirstName(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
                    fbrRegisteredUser.setUserLastName(fbrAndUserRegistrationBean.getUserDetail().getLastName());
                    //fbrRegisteredUser.setUserServiceProvider(fbrAndUserRegistrationBean.);
                    fbrRegisteredUser.setUserCellNo(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                    fbrRegisteredUser.setUserCellConfig(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                    fbrRegisteredUser.setUserEmailCode(fbrAndUserRegistrationBean.getUserDetail().getEmailPin());
                    fbrRegisteredUser.setUserEmailConfig(fbrAndUserRegistrationBean.getUserDetail().getEmail());
                    fbrRegisteredUser.setUserSmsCode(fbrAndUserRegistrationBean.getUserDetail().getSmsPin());*//*

                    //fbrRegisteredUser.
                    fbrRegisterdUserRepository.save(fbrRegisteredUser);
                    if (status.getMessage()== "Error"){
                        return new Status(status.getCode(), status.getMessage());
                    }
                    //   System.out.print("after method calll");

                    UserType userType;
                    if (corporateEmployee != null) {
                        userType = usersServices.findUserTypeById(2);
                        corporateEmployee.setStatus(appStatusServices.findOneByActiveStatus());
                        corporateEmployeeServices.saveOrUpdate(corporateEmployee);
                    } else {
                        userType = usersServices.findUserTypeById(1);
                    }

                    User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(fbrAndUserRegistrationBean.getUserLogin().getCnic());
                    if (userByCnic != null) {
                        return new Status(0, "A user account with this CNIC number already exists in the system.");
                    }

                    User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(fbrAndUserRegistrationBean.getUserLogin().getEmail());
                    if (userByEmail != null) {
                        return new Status(0, "A user account with this email account already exists in the system.");
                    }

                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                    String pwd = RandomStringUtils.random(30, characters);
                    MyPrint.println(pwd);


                    user.setUserType(userType);
                *//*user.setRole(roleRepository.findOne(Integer.parseInt(userRegistration.getUserLogin().getRole())));*//*
                    user.setCnicNo(fbrAndUserRegistrationBean.getUserLogin().getCnic());
                    user.setEmailAddress(fbrAndUserRegistrationBean.getUserLogin().getEmail());
                    *//*user.setStatus(usersServices.findUserStatusById(0));*//*
                    user.setStatus(UserStatus.DEACTIVE);
                    user.setPassword(pwd);

                    usersServices.create(user);

                    UserDetail userDetail = new UserDetail();
                    userDetail.setFirstName(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
                    *//*userDetail.setLastName(userRegistration.getUserDetail().getLastName());*//*
                    userDetail.setMobileNo(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                    userDetail.setCreateDate(CommonUtil.getCurrentTimestamp());
                    userDetail.setUser(user);

                    userDetailRepository.save(userDetail);

                    MyPrint.println("URL::::" + fbrAndUserRegistrationBean.getUrl());

                    String userIdIncrip = Base64.encode(Constants.ENCRIPT_ID.toString() + "," + userDetail.getId().toString());
                    String[] emails = {user.getEmailAddress()};
                    EmailSender.sendEmail(" <!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<title>Welcome to email</title>" +
                            "</head>" +
                            "<body >" +
                            "<div style=\"padding-left:80px\">" +
                            "<img src=\"http://ahshma.com/kirat/welcom3.png\"> " +
                            "" +
                            "<br><br>" +
                            "<div style=\"padding-left:100px\">" +
                            "<br>" +
                            "<div style=\"padding-left:130px\">" +
                            "<a href=\"" + fbrAndUserRegistrationBean.getUrl() + "?id=" + user.getId() + "&token=" + user.getPassword() + "\" style=\"background-color: #1FA4A0; " +
                            "padding-left:300px;" +
                            "border: none;" +
                            "color: white;" +
                            "padding: 10px 32px;" +
                            "text-align: center;" +
                            "text-decoration: none;" +
                            "display: inline-block;" +
                            "font-size: 16px;" +
                            "margin: 4px 2px;" +
                            "cursor: pointer;" +
                            "border-radius: 25px;" +
                            "\">" +
                            "Click Here to Continue</a> </div>" +
                            "<br><br>" +
                            "<div style=\"padding-left:111px\">" +
                            "<span style=\"font-size:16px;font-weight:bold;\">Follow us<span>" +
                            "<a href=\"https://www.facebook.com/\" style=\"text-decoration: none;color: black;\"><img src=\"http://ahshma.com/kirat/facebook1.png\"style=\"vertical-align:middle\"></a>" +
                            "<a href=\"https://twitter.com/\"style=\"text-decoration: none;color:black;\"><img src=\"http://ahshma.com/kirat/twitter1.png\"style=\"vertical-align:middle\"> </a>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html> ", "Welcome To Befiler", emails, "html");
                    return new Status(1, "User and Fbr Create ");
                }



                if (fbrAndUserRegistrationBean.getUserDetail().getCaptcha() == null && fbrAndUserRegistrationBean.getUserDetail().getCnic() == null && fbrAndUserRegistrationBean.getUserDetail().getPrefix() == null && fbrAndUserRegistrationBean.getUserDetail().getEmail() == null) {
                    UserType userType;
                    if (corporateEmployee != null) {
                        userType = usersServices.findUserTypeById(2);
                        corporateEmployee.setStatus(appStatusServices.findOneByActiveStatus());
                        corporateEmployeeServices.saveOrUpdate(corporateEmployee);
                    } else {
                        userType = usersServices.findUserTypeById(1);
                    }

                    User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(fbrAndUserRegistrationBean.getUserLogin().getCnic());
                    if (userByCnic != null) {
                        return new Status(0, "A user account with this CNIC number already exists in the system.");
                    }

                    User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(fbrAndUserRegistrationBean.getUserLogin().getEmail());
                    if (userByEmail != null) {
                        return new Status(0, "A user account with this email account already exists in the system.");
                    }

                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                    String pwd = RandomStringUtils.random(30, characters);
                    MyPrint.println(pwd);


                    user.setUserType(userType);
                        *//*user.setRole(roleRepository.findOne(Integer.parseInt(userRegistration.getUserLogin().getRole())));*//*
                    user.setCnicNo(fbrAndUserRegistrationBean.getUserLogin().getCnic());
                    user.setEmailAddress(fbrAndUserRegistrationBean.getUserLogin().getEmail());
                    *//*user.setStatus(usersServices.findUserStatusById(0));*//*
                    user.setStatus(UserStatus.DEACTIVE);
                    user.setPassword(pwd);

                    usersServices.create(user);

                    UserDetail userDetail = new UserDetail();
                    userDetail.setFirstName(fbrAndUserRegistrationBean.getUserDetail().getFirstName());
                        *//*userDetail.setLastName(userRegistration.getUserDetail().getLastName());*//*
                    userDetail.setMobileNo(fbrAndUserRegistrationBean.getUserDetail().getMobileNo());
                    userDetail.setCreateDate(CommonUtil.getCurrentTimestamp());
                    userDetail.setUser(user);

                    userDetailRepository.save(userDetail);

                    MyPrint.println("URL::::" + fbrAndUserRegistrationBean.getUrl());

                    String userIdIncrip = Base64.encode(Constants.ENCRIPT_ID.toString() + "," + userDetail.getId().toString());
                    String[] emails = {user.getEmailAddress()};
                    EmailSender.sendEmail(" <!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<title>Welcome to email</title>" +
                            "</head>" +
                            "<body >" +
                            "<div style=\"padding-left:80px\">" +
                            "<img src=\"http://ahshma.com/kirat/welcom3.png\"> " +
                            "" +
                            "<br><br>" +
                            "<div style=\"padding-left:100px\">" +
                            "<br>" +
                            "<div style=\"padding-left:130px\">" +
                            "<a href=\"" + fbrAndUserRegistrationBean.getUrl() + "?id=" + user.getId() + "&token=" + user.getPassword() + "\" style=\"background-color: #1FA4A0; " +
                            "padding-left:300px;" +
                            "border: none;" +
                            "color: white;" +
                            "padding: 10px 32px;" +
                            "text-align: center;" +
                            "text-decoration: none;" +
                            "display: inline-block;" +
                            "font-size: 16px;" +
                            "margin: 4px 2px;" +
                            "cursor: pointer;" +
                            "border-radius: 25px;" +
                            "\">" +
                            "Click Here to Continue</a> </div>" +
                            "<br><br>" +
                            "<div style=\"padding-left:111px\">" +
                            "<span style=\"font-size:16px;font-weight:bold;\">Follow us<span>" +
                            "<a href=\"https://www.facebook.com/\" style=\"text-decoration: none;color: black;\"><img src=\"http://ahshma.com/kirat/facebook1.png\"style=\"vertical-align:middle\"></a>" +
                            "<a href=\"https://twitter.com/\"style=\"text-decoration: none;color:black;\"><img src=\"http://ahshma.com/kirat/twitter1.png\"style=\"vertical-align:middle\"> </a>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html> ", "Welcome To Befiler", emails, "html");


                    return new Status(2, "User Creat ");
                }

            } catch (Exception e) {
                MyPrint.println("___________ Data ROLL Back _____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new Status(0, CommonUtil.getRootCause(e).getMessage());
            }}
        return new Status(0, "Incomplete Data");

    }*/



    @SuppressWarnings("unchecked")
    /*@Transactional*/
    @RequestMapping(value = "/fbrRegisteration" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FbrCaputerCodeBean> fbrRegisteration(@RequestBody FbrRegistrationBean fbrRegisterationBean) {
        FbrCaputerCodeBean res=new FbrCaputerCodeBean();
        try {
            FbrCaputerCodeBean response=fbrAllService.submitFbrRegistration(fbrRegisterationBean);
            /*User user=usersServices.findOne(fbrRegisterationBean.getUserId());
            if(user != null) {
                FbrRegisteredUser fbrRegistredUser = new FbrRegisteredUser();
                fbrRegistredUser.setUser(user);
                fbrRegistredUser.setCnic(fbrRegisterationBean.getCnic());
                fbrRegistredUser.setPrefix(fbrRegisterationBean.getPrefix());
                fbrRegistredUser.setFirstName(fbrRegisterationBean.getFirstName());
                fbrRegistredUser.setLastName(fbrRegisterationBean.getLastName());
                fbrRegistredUser.setServiceProvider(fbrRegisterationBean.getCurrentService());
                fbrRegistredUser.setCellNo(fbrRegisterationBean.getCellNo());
                fbrRegistredUser.setEmail(fbrRegisterationBean.getEmail());
                fbrRegistredUser.setCaptcha(fbrRegisterationBean.getCaptcha());
                fbrRegistredUser.setCellNoConfig(fbrRegisterationBean.getCellNoCong());
                fbrRegistredUser.setEmailConfig(fbrRegisterationBean.getEmailCong());
                fbrRegistredUser.setStatus(false);
                fbrRegisterdUserRepository.save(fbrRegistredUser);
            }*/
            return new ResponseEntity<FbrCaputerCodeBean>(response, HttpStatus.OK);
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<FbrCaputerCodeBean>(res, HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/fbrRegisteration2" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FbrCaputerCodeBean> fbrRegisteration2(@RequestBody FbrRegistrationBean fbrRegisterationBean) { // change from FbrRegisterationBean
        FbrCaputerCodeBean response=new FbrCaputerCodeBean();
        try {
            FbrCaputerCodeBean response2=fbrAllService.submitFbrRegistration2(fbrRegisterationBean);

            /*User user=usersServices.findOneByIdAndStatus(fbrRegisterationBean.getUserId(), usersServices.findUserStatusById(1));*/
            User user=usersServices.findOneByIdAndStatus(fbrRegisterationBean.getUserId(), UserStatus.ACTIVE);
            if(user != null){
                FbrRegisteredUser fbrRegistredUser = fbrRegisterdUserRepository.findById(fbrRegisterationBean.getId()).orElse(null); //FbrRegisteredUser
                if(fbrRegistredUser != null){
                    fbrRegistredUser.setEmailCode(fbrRegisterationBean.getEmailPin());
                    fbrRegistredUser.setSmsCode(fbrRegisterationBean.getSmsPin());
                    fbrRegistredUser.setStatus(true);
                    fbrRegisterdUserRepository.save(fbrRegistredUser);
                }
                //fbrRegisterdUserServices.createFbrRegistredUser(fbrRegistredUser);

            }
            return new ResponseEntity<>(response2, HttpStatus.OK);
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    /*@SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/fbrSubmitLoginData" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> fbrRegisteration2(@RequestBody FbrLoginBean fbrLoginBean) {
        Status response=new Status();
        try {
            User user=usersServices.findOne(fbrLoginBean.getUserId());
            if(user != null) {
                FbrRegisteredUser fbrRegistredUser = fbrRegisterdUserRepository.findOne(fbrLoginBean.getId());
                if (fbrRegistredUser != null) {

                    if (fbrRegistredUser.getEmailCode() != null && fbrRegistredUser.getSmsCode() != null) {
                        fbrRegistredUser.setStatus(true);
                        fbrRegisterdUserRepository.save(fbrRegistredUser);
                        response.setCode(1);
                        response.setMessage("success");
                        MyPrint.println(":::::::::::::::::FbrLogin:::::sms code No=" + fbrRegistredUser.getSmsCode());
                        MyPrint.println(":::::::::::::::::FbrLogin::::: email code=" + fbrRegistredUser.getEmailCode());

                        return new ResponseEntity<Status>(response, HttpStatus.OK);
                    }
                }

            }
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<Status>(response, HttpStatus.OK);
        }
        return new ResponseEntity<Status>(response, HttpStatus.OK);
    }*///End method*/
}//End of class
