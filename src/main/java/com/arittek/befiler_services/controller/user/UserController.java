package com.arittek.befiler_services.controller.user;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.beans.taxform.TaxformMinimalBean;
import com.arittek.befiler_services.beans.userModule.UserBean;
import com.arittek.befiler_services.model.enums.Direction;
import com.arittek.befiler_services.model.enums.OrderBy;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.util.*;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {

    private final RoleService roleService;
    private final UsersServices usersServices;
    private final TaxformServices taxformServices;
    private final FbrUserAccountInfoServices fbrUserAccountInfoServices;

    @Autowired
    public UserController(RoleService roleService, UsersServices usersServices, TaxformServices taxformServices, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.roleService = roleService;
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
    }


   /* @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/registeration", produces = "application/json", method = {RequestMethod.POST})
    public Status createUser(@RequestBody UserRegistration userRegistration) throws Exception {
        if (userRegistration != null && userRegistration.getUserLogin() != null && userRegistration.getUserDetail() != null) {

            User user = new User();
            try {

                Role role = roleService.findOneCustomerRole();
                if (role == null) {
                    return new Status(0, "Customer role is not defined");
                }

                User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getCnic());
                if (userByCnic != null) {
                    return new Status(0, " A user account with this CNIC number already exists in the system.");
                }

                User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getEmail());
                if (userByEmail != null) {
                    return new Status(0, "A user account with this email account already exists in the system.");
                }

                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                String pwd = RandomStringUtils.random(30, characters);

                *//*user.setUserType(userType);*//*
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                user.setCnicNo(userRegistration.getUserLogin().getCnic());
                user.setEmailAddress(userRegistration.getUserLogin().getEmail());
                user.setStatus(UserStatus.DEACTIVE);
                user.setPassword(pwd);

                user.setFullName(userRegistration.getUserDetail().getFirstName());
                user.setMobileNo(userRegistration.getUserDetail().getMobileNo());

                usersServices.create(user);

                MyPrint.println("URLss::::" + userRegistration.getUrl());

                String url=userRegistration.getUrl() + "?id=" + user.getId() + "&token=" + user.getPassword();
                String emailTemplate= EmailUtil.emailTemaplate(url,user.getFullName());

                *//*String userIdIncrip = Base64.encode(Constants.ENCRIPT_ID.toString() + "," + user.getId().toString());*//*

                EmailSender.sendEmail(emailTemplate, "Welcome To Befiler", user.getEmailAddress());
                return new Status(1, "Thank you for joining Befiler, Please check your email");
            } catch (Exception e) {
                MyPrint.println("___________ Data ROLL Back _____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new Status(0, CommonUtil.getRootCause(e).getMessage());
            }
        } else {
            return new Status(0, "Incomplete Data");
        }
    }*/

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/registration", produces = "application/json", method = {RequestMethod.POST})
    public Status createUser(@RequestBody UserRegistration userRegistration) throws Exception {
        if (userRegistration != null && userRegistration.getUserLogin() != null && userRegistration.getUserDetail() != null) {

            User user = new User();
            try {

                Role role = roleService.findOneCustomerRole();
                if (role == null) {
                    return new Status(0, "Customer role is not defined");
                }

                User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getCnic());
                if (userByCnic != null) {
                    return new Status(0, " A user account with this CNIC number already exists in the system.");
                }

                User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(userRegistration.getUserLogin().getEmail());
                if (userByEmail != null) {
                    return new Status(0, "A user account with this email account already exists in the system.");
                }

                String password = CommonUtil.passwordCredentials(userRegistration.getUserLogin().getPassword());

                if (!password.equals(userRegistration.getUserLogin().getPassword()))
                    return new Status(0, password);

                String encodedPassword = EncoderDecoder.getEncryptedSHA1Password(password);

                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setRoles(roles);
                user.setCnicNo(userRegistration.getUserLogin().getCnic());
                user.setEmailAddress(userRegistration.getUserLogin().getEmail());
                user.setStatus(UserStatus.ACTIVE);
                user.setPassword(encodedPassword);

                user.setFullName(userRegistration.getUserDetail().getFirstName());
                user.setMobileNo(userRegistration.getUserDetail().getMobileNo());

                usersServices.create(user);

                // MyPrint.println("URLss::::" + userRegistration.getUrl());

                // String url=userRegistration.getUrl() + "?id=" + user.getId() + "&token=" + user.getPassword();
                // String emailTemplate= EmailUtil.emailTemaplate(url,user.getFullName());

                //*String userIdIncrip = Base64.encode(Constants.ENCRIPT_ID.toString() + "," + user.getId().toString());*//*

                // EmailSender.sendEmail(emailTemplate, "Welcome To Befiler", user.getEmailAddress());

                return new Status(1, "Thank you for joining Befiler, Please sign in to continue");
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


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DetailedUserBean> getUsers(@PathVariable("id") Integer id) {
        User user = null;
        try {
            if (id != null && id != 0) {
                MyPrint.println("Inside First IF");
                user = usersServices.findOneByIdAndStatus(id, UserStatus.ACTIVE);
                if (user != null) {
                    MyPrint.println("Inside Second IF");
                    DetailedUserBean detailedUserBean = new DetailedUserBean();

                    detailedUserBean.setUserId(user.getId());
                    detailedUserBean.setCnic(user.getCnicNo());
                    detailedUserBean.setEmail(user.getEmailAddress());
                    detailedUserBean.setFirstName(user.getFullName());
                    detailedUserBean.setMobileNo(user.getMobileNo());

                    return new ResponseEntity<>(detailedUserBean, HttpStatus.OK);
                } else {
                    MyPrint.print("Inside First Else");
                    return new ResponseEntity<>(new DetailedUserBean(), HttpStatus.UNAUTHORIZED);
                }
            } else {
                MyPrint.println("Inside Second Else");
                return new ResponseEntity<>(new DetailedUserBean(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new DetailedUserBean(), HttpStatus.UNAUTHORIZED);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    List<UserLoginBean> getAllUsers() {

        List<UserLoginBean> userList = new ArrayList<UserLoginBean>();
        try {
            /*List<UserType> userTypeList = new ArrayList<>();
            userTypeList.add(usersServices.findUserTypeById(1));
            userTypeList.add(usersServices.findUserTypeById(2));*/

            /*List<User> userList1 = usersServices.findAllByStatusAndUserTypeIn(usersServices.findUserStatusById(1), userTypeList);*/
            /*List<User> userList1 = usersServices.findAllByStatusAndUserTypeIn(UserStatus.ACTIVE, userTypeList);*/
            List<User> userList1 = usersServices.findAllActiveCustomers();
            if (userList1.size() > 0) {
                for (User userLogin : userList1) {
                    UserLoginBean bean = new UserLoginBean();
                    /*bean = ObjectConverter.entityToBean(userLogin);*/
                    userList.add(bean);
                }
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
        }
        return userList;
    }

    //----------------------PUT USERS UPDATE-----------------------------------------

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.PUT})
    public ResponseEntity<Status> updateUser(@RequestBody DetailedUserBean detailedUserBean) throws Exception {
        if (detailedUserBean != null && detailedUserBean.getUserId() != null) {
            try {

                if (!StringUtils.isNotEmpty(detailedUserBean.getEmail())) {
                    return new ResponseEntity<>(new Status(0, "Please enter email address"), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(detailedUserBean.getCnic())) {
                    return new ResponseEntity<>(new Status(0, "Please enter CNIC no"), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(detailedUserBean.getFirstName())) {
                    return new ResponseEntity<>(new Status(0, "Please enter full name"), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(detailedUserBean.getMobileNo())) {
                    return new ResponseEntity<>(new Status(0, "Please enter mobile no"), HttpStatus.OK);
                }

                User user = usersServices.findOneByIdAndStatus(detailedUserBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0, "Session expired"), HttpStatus.OK);
                }

                User userByCnic = usersServices.findOneByCnicNoWhereUserIsActiveOrDeactive(detailedUserBean.getCnic());
                if (userByCnic != null && userByCnic.getId() != user.getId()) {
                    return new ResponseEntity<>(new Status(0, "A user account with this CNIC number already exists in the system."), HttpStatus.OK);
                }

                User userByEmail = usersServices.findOneByEmailAddressWhereUserIsActiveOrDeactive(detailedUserBean.getEmail());
                if (userByEmail != null && userByEmail.getId() != user.getId()) {
                    return new ResponseEntity<>(new Status(0, "A user account with this email account already exists in the system."), HttpStatus.OK);
                }

                user.setEmailAddress(detailedUserBean.getEmail());
                user.setCnicNo(detailedUserBean.getCnic());
                user.setFullName(detailedUserBean.getFirstName());
                user.setMobileNo(detailedUserBean.getMobileNo());

                usersServices.update(user);

                return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
            } catch (Exception e) {
                MyPrint.println("___________ Data ROLL Back _____________");
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<>(new Status(0, CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    //----------------------GET IF CNIC EXIST-----------------------------------------

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/isCnicExist/{cnic}", method = RequestMethod.GET)
    public
    @ResponseBody
    Status isCnicExist(@PathVariable("cnic") String cnic) {
        User user = null;
        try {
            if (StringUtils.isNotEmpty(cnic)) {
                user = usersServices.findOneByCnicNo(cnic);
                if (user != null)
                    return new Status(1, "Exist");
            } else
                return new Status(1, "Enter Value");
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new Status(1, CommonUtil.getRootCause(e).getMessage());
        }
        return new Status(0, "Not Exist");
    }

    //----------------------GET IF EMAIL EXISTS-----------------------------------------

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/isEmailExist", produces = "application/json", method = RequestMethod.POST)
    public
    @ResponseBody
    Status isEmailExist(@RequestBody TaxformBean taxformBean) {
        String email = taxformBean.getEmail();
        MyPrint.println("Email::::" + email);
        User user = null;
        try {
            if (email != null && !email.trim().equals("")) {
                user = (User) usersServices.findOneByEmailAddress(email);
                /*user = (UserDetail) daoServices.selectFromAndWhere(new UserDetail(), "email_address=" + email, null);*/
                if (user != null) {
                    MyPrint.println("If User is NOT NULL");
                    return new Status(1, "Exist");
                }
            } else
                return new Status(1, "Enter Value");
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new Status(0, CommonUtil.getRootCause(e).getMessage());
        }
        return new Status(0, "Not Exist");
    }

    //----------------------GET IF MOBILE EXISTS-----------------------------------------

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/isMobileNoExist/{mobileNo}", method = RequestMethod.GET)
    public
    @ResponseBody
    Status isMobileNoExist(@PathVariable("mobileNo") String mobileNo) {
        User user = null;
        try {
            if (mobileNo != null && !mobileNo.trim().equals("")) {
                user = usersServices.findOneByMobileNo(mobileNo);
                /*user = (UserDetail) daoServices.selectFromAndWhere(new UserDetail(), "mobile_no=" + email, null);*/
                if (user != null)
                    return new Status(1, "Exist");
            } else
                return new Status(1, "Enter Value");
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new Status(1, CommonUtil.getRootCause(e).getMessage());
        }
        return new Status(0, "Not Exist");
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/forgotPassword", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> fbrRegistration(@RequestBody UserRegistration userRegistration) {
        Status status = new Status();
        try {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            String pwd = RandomStringUtils.random(30, characters);
            MyPrint.println("password is " + pwd);

            User user = usersServices.findOneByEmailAddress(userRegistration.getUserLogin().getEmail());

            if (user != null && userRegistration.getUrl() != null) {
                user.setStatus(UserStatus.DEACTIVE);
                user.setPassword(pwd);
                usersServices.update(user);
                EmailSender.sendEmail(EmailUtil.forgotPasswordTemplete(userRegistration.getUrl(), user), "Password Reset Assistance", user.getEmailAddress());
                MyPrint.println("email sent");
                status.setCode(1);
                status.setMessage("Email sent");
                return new ResponseEntity<>(status, HttpStatus.OK);

            } else {
                status.setCode(0);
                status.setMessage("User Not found");
                return new ResponseEntity<>(status, HttpStatus.OK);
            }

        } catch (Exception e) {
            MyPrint.println("___________ Data ROLL Back _____________");
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
        }
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @RequestMapping(value = "/createPasswordCheck", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> urlCheckBeforeCreatePassword(@RequestBody PasswordBean passwordBean) {

        if (passwordBean != null && passwordBean.getUserId() != null && passwordBean.getToken() != null) {
            try {
                User user = usersServices.findOneByIdAndStatus(passwordBean.getUserId(), UserStatus.DEACTIVE);
                if (user != null && user.getPassword().equalsIgnoreCase(passwordBean.getToken().trim())) {
                    if (user.getStatus() != null && user.getStatus().getId() == 0) {
                        return new ResponseEntity<>(new Status(1, "url not expire "), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : User is already active/deleted"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Can't Detect User."), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data "), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<Status>(new Status(0, "Incomplete Data : Token"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/createPassword", method = RequestMethod.POST)
    public ResponseEntity<?> createPassword(@RequestBody PasswordBean passwordBean) throws Exception {
        try {

            if (passwordBean != null && passwordBean.getUserId() != null && passwordBean.getToken() != null) {
                User user = usersServices.findOneByIdAndStatus(passwordBean.getUserId(), UserStatus.DEACTIVE);
                if (user != null && user.getPassword().equalsIgnoreCase(passwordBean.getToken().trim())) {
                    if (user.getStatus() != null && user.getStatus().getId() == 0) {
                        if (passwordBean.getPassword() != null && passwordBean.getConfirmPassword() != null) {
                            String password = CommonUtil.passwordCredentials(passwordBean.getPassword());
                            if (!password.equals(passwordBean.getPassword()))
                                return new ResponseEntity<>(new Status(0, password), HttpStatus.OK);

                            if (passwordBean.getPassword().equals(passwordBean.getConfirmPassword())) {
                                user.setPassword(EncoderDecoder.getEncryptedSHA1Password(password));
                                /*user.setStatus(usersServices.findUserStatusById(1));*/
                                user.setStatus(UserStatus.ACTIVE);
                                user.setLastPasswordResetDate(CommonUtil.getCurrentTimestamp()); // last password reset date
                                usersServices.update(user);

                                return new ResponseEntity<>(new Status(1, "Password Created Successfully."), HttpStatus.OK);

                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Password & Confirm Password Doesn't Match"), HttpStatus.OK);
                            }
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Invalid Password Fields"), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : User is already active/deleted"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Can't Detect User."), HttpStatus.OK);
                }

            } else {
                return new ResponseEntity<>(new Status(0, "Incomplete Data : Token"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    public ResponseEntity<?> createTempPassword(@RequestBody UserBean userBean) throws Exception {
        try {

            User user = null;
            if (userBean.getId() != null) {
                user = usersServices.findOneByIdAndStatusIn(userBean.getId(), Arrays.asList(UserStatus.ACTIVE, UserStatus.DEACTIVE));
                if (user == null)
                    return new ResponseEntity<>(new Status(0, "Please select user"), HttpStatus.OK);
            } else
                return new ResponseEntity<>(new Status(0, "Please select user"), HttpStatus.OK);


            if (user.getEmailAddress() == null || !user.getEmailAddress().contains("@"))
                return new ResponseEntity<>(new Status(0, "Users email address is not correct"), HttpStatus.OK);

            String tempPassword = user.getEmailAddress().split("@")[0];

            user.setPassword(EncoderDecoder.getEncryptedSHA1Password(tempPassword));
            user.setStatus(UserStatus.ACTIVE);
            user.setLastPasswordResetDate(CommonUtil.getCurrentTimestamp());
            usersServices.update(user);

            EmailSender.sendEmail(EmailUtil.tempPasswordTemplete(user, tempPassword), "Password Recovery", user.getEmailAddress());

            return new ResponseEntity<>(new Status(1, "Password Created Successfully."), HttpStatus.OK);


        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@RequestBody PasswordBean passwordBean) throws Exception {
        try {

            User user;
            if (passwordBean.getUserId() != null && StringUtils.isNotEmpty(passwordBean.getOldPassword())) {
                String encodedPassword = EncoderDecoder.getEncryptedSHA1Password(passwordBean.getOldPassword().trim());
                user = usersServices.findOneByIdAndPassword(passwordBean.getUserId(), encodedPassword);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0, "Incorrect old password"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0, "Can't change password"), HttpStatus.OK);
            }

            String password = CommonUtil.passwordCredentials(passwordBean.getPassword());
            if (!password.equals(passwordBean.getPassword()))
                return new ResponseEntity<>(new Status(0, password), HttpStatus.OK);

            if (StringUtils.isNotEmpty(passwordBean.getPassword()) && StringUtils.isNotEmpty(passwordBean.getConfirmPassword())) {
                if (passwordBean.getPassword().equals(passwordBean.getConfirmPassword())) {

                    user.setPassword(EncoderDecoder.getEncryptedSHA1Password(passwordBean.getPassword()));
                    user.setStatus(UserStatus.ACTIVE);
                    user.setLastPasswordResetDate(CommonUtil.getCurrentTimestamp()); // last password reset date
                    usersServices.update(user);

                    return new ResponseEntity<>(new Status(1, "Password Updated Successfully."), HttpStatus.OK);

                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Password & Confirm Password Doesn't Match"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0, "Incomplete Data : Password or Confirm Password Field"), HttpStatus.OK);
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
    }



   /* @RequestMapping( value = "/customers", params = { "orderBy", "direction", "page", "size" }, produces = "application/json",  method = RequestMethod.GET )
    public ResponseEntity<StatusBean> getAllCustomers(@RequestParam("orderBy") String orderBy,
                                                      @RequestParam("direction") String direction,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        try {


            if (!(direction.equals(Direction.ASCENDING.getDirectionCode())
                    || direction.equals(Direction.DESCENDING.getDirectionCode()))) {
                return new ResponseEntity<>(new StatusBean(0, "Invalid sort direction"), HttpStatus.OK);
            }
            if (!(orderBy.equals(OrderBy.ID.getOrderByCode()) || orderBy.equals(OrderBy.FIRST_NAME.getOrderByCode()) || orderBy.equals(OrderBy.MOBILE_NO.getOrderByCode()) || orderBy.equals(OrderBy.STATUS.getOrderByCode()))) {
                return new ResponseEntity<>(new StatusBean(0, "Invalid orderBy condition"), HttpStatus.OK);
            }

            Page<User> userList = usersServices.findAllCustomers(orderBy, direction, page, size);

            System.out.println(userList.getTotalElements());
            System.out.println(userList.getTotalPages());

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

                List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<>();
                for (Taxform taxform : taxformList) {
                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                    taxformMinimalBean.setTaxformId(taxform.getId());
                    taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());

                    if (taxform.getStatus().getId() != 0) {
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                    } else {
                        taxformMinimalBean.setStatus("PERSONAL INFO");
                        if (taxform.getTaxformIncomeTaxSalary() != null ||
                                taxform.getTaxformIncomeTaxProperty() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                taxform.getTaxformIncomeTaxOtherSources() != null ||
                                (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                            taxformMinimalBean.setStatus("INCOME TAX");
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                            taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                        }

                        if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                taxform.getTaxformTaxDeductedCollectedOther() != null) {
                            taxformMinimalBean.setStatus("TAX DEDUCTED");
                        }

                        if (taxform.getTaxformWelthStatement() != null) {
                            taxformMinimalBean.setStatus("WEALTH STATEMENT");
                        }
                    }
                    taxformMinimalBeanList.add(taxformMinimalBean);
                }
                userDetailBean.setTaxformMinimalBeans(taxformMinimalBeanList);

                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo != null) {
                    FbrUserAccountInfoBean fbrUserAccountInfoBean = new FbrUserAccountInfoBean();
                    fbrUserAccountInfoBean.setNtnStatus(fbrUserAccountInfo.getFbrUserAccountInfoStatus().toString());

                    fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean);
                }
                userDetailBean.setFbrUserAccountInfoBeanList(fbrUserAccountInfoBeanList);

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
    }*/

   /* @RequestMapping(value = "/customers", params = {"orderBy", "direction", "page", "size"}, produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllCustomers(@RequestParam("orderBy") String orderBy,
                                                      @RequestParam("direction") String direction,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        try {

            if (!(direction.equals(Direction.ASCENDING.getDirectionCode())
                    || direction.equals(Direction.DESCENDING.getDirectionCode()))) {
                return new ResponseEntity<>(new StatusBean(0, "Invalid sort direction"), HttpStatus.OK);
            }


            if (!(orderBy.equals(OrderBy.ID.getOrderByCode()) || orderBy.equals(OrderBy.FIRST_NAME.getOrderByCode()) || orderBy.equals(OrderBy.EMAIL.getOrderByCode()) || orderBy.equals(OrderBy.CNIC.getOrderByCode()) || orderBy.equals(OrderBy.MOBILE_NO.getOrderByCode()) || orderBy.equals(OrderBy.CURR_DATE.getOrderByCode()) || orderBy.equals(OrderBy.STATUS.getOrderByCode()))) {

                return new ResponseEntity<>(new StatusBean(0, "Invalid orderBy condition"), HttpStatus.OK);
            }

            if(orderBy.equals("firstName"))
                orderBy = "fullName";
            else if(orderBy.equals("currDate"))
                orderBy = "createdDate";

            Page<User> userList = usersServices.findAllCustomers(orderBy, direction, page - 1, size);
            System.out.println(userList.getTotalElements());
            System.out.println(userList.getTotalPages());

            //<--By maqsood -->
            PaginationBean paginationBean = new PaginationBean();
            paginationBean.setTotalElements((int) userList.getTotalElements());
            paginationBean.setTotalPages((int) userList.getTotalPages());
            paginationBean.setPageSize((int) userList.getSize());
            paginationBean.setPageNumber((int) userList.getNumber());
            long i ;
            if (page > 1) {
                for (int count = 1; count < page; count++) {
                    i = i + size;
                    System.out.println("index " + i);
                }
            }//<--By maqsood -->
            List<UserDetailBean> userDetailBeanList = new ArrayList<>();
            for (User user : userList) {
                UserDetailBean userDetailBean = new UserDetailBean();

                //<--By maqsood -->
                while (i <= userList.getTotalElements()) {
                    userDetailBean.setSerialNo(i);
                    break;
                }
                i++;//<--By maqsood -->
                userDetailBean.setStatus(user.getStatus().name());
                userDetailBean.setCnic(user.getCnicNo());
                userDetailBean.setEmail(user.getEmailAddress());
                userDetailBean.setCurrDate(CommonUtil.changeTimestampToString(user.getCreatedDate()));

                userDetailBean.setId(user.getId());
                userDetailBean.setMobileNo(user.getMobileNo());
                userDetailBean.setAddress(user.getAddress());
                userDetailBean.setFirstName(user.getFullName());

                List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<>();
                for (Taxform taxform : taxformList) {
                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                    taxformMinimalBean.setTaxformId(taxform.getId());
                    taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());

                    if (taxform.getStatus().getId() != 0) {
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                    } else {
                        taxformMinimalBean.setStatus("PERSONAL INFO");
                        if (taxform.getTaxformIncomeTaxSalary() != null ||
                                taxform.getTaxformIncomeTaxProperty() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                taxform.getTaxformIncomeTaxOtherSources() != null ||
                                (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                            taxformMinimalBean.setStatus("INCOME TAX");
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                            taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                        }

                        if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                taxform.getTaxformTaxDeductedCollectedOther() != null) {
                            taxformMinimalBean.setStatus("TAX DEDUCTED");
                        }

                        if (taxform.getTaxformWelthStatement() != null) {
                            taxformMinimalBean.setStatus("WEALTH STATEMENT");
                        }
                    }
                    taxformMinimalBeanList.add(taxformMinimalBean);
                }
                userDetailBean.setTaxformMinimalBeans(taxformMinimalBeanList);

                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo != null) {
                    FbrUserAccountInfoBean fbrUserAccountInfoBean = new FbrUserAccountInfoBean();
                    fbrUserAccountInfoBean.setNtnStatus(fbrUserAccountInfo.getFbrUserAccountInfoStatus().toString());

                    fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean);
                }
                userDetailBean.setFbrUserAccountInfoBeanList(fbrUserAccountInfoBeanList);

                userDetailBeanList.add(userDetailBean);


            }
            StatusBean statusBean = new StatusBean(1, "Success");

            statusBean.setPagination(paginationBean);       // by maqsood

            statusBean.setResponse(userDetailBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }*/

    /*@RequestMapping(value = "/customers", params = {"orderBy", "direction", "page", "size"}, produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllCustomers(@RequestParam("orderBy") String orderBy,
                                                      @RequestParam("direction") String direction,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size) {
        try {

            if (!(direction.equals(Direction.ASCENDING.getDirectionCode())
                    || direction.equals(Direction.DESCENDING.getDirectionCode()))) {
                return new ResponseEntity<>(new StatusBean(0, "Invalid sort direction"), HttpStatus.OK);
            }


            if (!(orderBy.equals(OrderBy.ID.getOrderByCode()) || orderBy.equals(OrderBy.FIRST_NAME.getOrderByCode()) || orderBy.equals(OrderBy.EMAIL.getOrderByCode()) || orderBy.equals(OrderBy.CNIC.getOrderByCode()) || orderBy.equals(OrderBy.MOBILE_NO.getOrderByCode()) || orderBy.equals(OrderBy.CURR_DATE.getOrderByCode()) || orderBy.equals(OrderBy.STATUS.getOrderByCode()))) {

                return new ResponseEntity<>(new StatusBean(0, "Invalid orderBy condition"), HttpStatus.OK);
            }

            if(orderBy.equals("firstName"))
                orderBy = "fullName";
            else if(orderBy.equals("currDate"))
                orderBy = "createdDate";

            Page<User> userList = usersServices.findAllCustomers(orderBy, direction, page - 1, size);
            System.out.println(userList.getTotalElements());
            System.out.println(userList.getTotalPages());

            //<--By maqsood -->
            PaginationBean paginationBean = new PaginationBean();
            paginationBean.setTotalElements((int) userList.getTotalElements());
            paginationBean.setTotalPages((int) userList.getTotalPages());
            paginationBean.setPageSize((int) userList.getSize());
            paginationBean.setPageNumber((int) userList.getNumber());
            long i = 1;
            if (page > 1) {
                for (int count = 1; count < page; count++) {
                    i = i + size;
                    System.out.println("index " + i);
                }
            }//<--By maqsood -->
            List<UserDetailBean> userDetailBeanList = new ArrayList<>();
            for (User user : userList) {
                UserDetailBean userDetailBean = new UserDetailBean();

                //<--By maqsood -->
                while (i <= userList.getTotalElements()) {
                    userDetailBean.setSerialNo(i);
                    break;
                }
                i++;//<--By maqsood -->
                userDetailBean.setStatus(user.getStatus().name());
                userDetailBean.setCnic(user.getCnicNo());
                userDetailBean.setEmail(user.getEmailAddress());
                userDetailBean.setCurrDate(CommonUtil.changeTimestampToString(user.getCreatedDate()));

                userDetailBean.setId(user.getId());
                userDetailBean.setMobileNo(user.getMobileNo());
                userDetailBean.setAddress(user.getAddress());
                userDetailBean.setFirstName(user.getFullName());

                List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<>();
                for (Taxform taxform : taxformList) {
                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                    taxformMinimalBean.setTaxformId(taxform.getId());
                    taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());

                    if (taxform.getStatus().getId() != 0) {
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                    } else {
                        taxformMinimalBean.setStatus("PERSONAL INFO");
                        if (taxform.getTaxformIncomeTaxSalary() != null ||
                                taxform.getTaxformIncomeTaxProperty() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                taxform.getTaxformIncomeTaxOtherSources() != null ||
                                (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                            taxformMinimalBean.setStatus("INCOME TAX");
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                            taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                        }

                        if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                taxform.getTaxformTaxDeductedCollectedOther() != null) {
                            taxformMinimalBean.setStatus("TAX DEDUCTED");
                        }

                        if (taxform.getTaxformWelthStatement() != null) {
                            taxformMinimalBean.setStatus("WEALTH STATEMENT");
                        }
                    }
                    taxformMinimalBeanList.add(taxformMinimalBean);
                }
                userDetailBean.setTaxformMinimalBeans(taxformMinimalBeanList);

                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo != null) {
                    FbrUserAccountInfoBean fbrUserAccountInfoBean = new FbrUserAccountInfoBean();
                    fbrUserAccountInfoBean.setNtnStatus(fbrUserAccountInfo.getFbrUserAccountInfoStatus().toString());

                    fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean);
                }
                userDetailBean.setFbrUserAccountInfoBeanList(fbrUserAccountInfoBeanList);

                userDetailBeanList.add(userDetailBean);


            }
            StatusBean statusBean = new StatusBean(1, "Success");

            statusBean.setPagination(paginationBean);       // by maqsood

            statusBean.setResponse(userDetailBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }*/


    //<--Gulshan Kumar-->
    @RequestMapping(value = "/customers", params = {"orderBy", "direction", "page", "size"}, produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllCustomers(@RequestParam("orderBy") String orderBy,
                                                      @RequestParam("direction") String direction,
                                                      @RequestParam("page") int page,
                                                      @RequestParam("size") int size,
                                                      @RequestParam(value = "search", defaultValue = "") String search) {
        try {

            if (!(direction.equals(Direction.ASCENDING.getDirectionCode())
                    || direction.equals(Direction.DESCENDING.getDirectionCode()))) {
                return new ResponseEntity<>(new StatusBean(0, "Invalid sort direction"), HttpStatus.OK);
            }


            if (!(orderBy.equals(OrderBy.ID.getOrderByCode()) || orderBy.equals(OrderBy.FIRST_NAME.getOrderByCode()) || orderBy.equals(OrderBy.EMAIL.getOrderByCode()) || orderBy.equals(OrderBy.CNIC.getOrderByCode()) || orderBy.equals(OrderBy.MOBILE_NO.getOrderByCode()) || orderBy.equals(OrderBy.CURR_DATE.getOrderByCode()) || orderBy.equals(OrderBy.STATUS.getOrderByCode()))) {

                return new ResponseEntity<>(new StatusBean(0, "Invalid orderBy condition"), HttpStatus.OK);
            }

            if (orderBy.equals("firstName"))
                orderBy = "fullName";
            else if (orderBy.equals("currDate"))
                orderBy = "createdDate";
            else if (orderBy.equals("email")) {
                orderBy = "emailAddress";
            } else if (orderBy.equals("cnic")) {
                orderBy = "cnicNo";
            }

            if (search.equals(UserStatus.DEACTIVE.toString()))
                search = "0";
            else if (search.equals(UserStatus.ACTIVE.toString()))
                search = "1";
            else if (search.equals(UserStatus.DELETED.toString()))
                search = "2";
            else if (!orderBy.equals("currDate"))
                search = search.toLowerCase();

            //Check date format
            String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(search);
            if (matcher.matches()) {
                Date date = new SimpleDateFormat("dd/MM/yyyy").parse(search);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                search = format.format(date);

            }

            System.out.println("search: " + search);
            Page<User> userList = usersServices.findAllCustomers(orderBy, direction, page - 1, size, search);
            System.out.println(userList.getTotalElements());
            System.out.println(userList.getTotalPages());

            //<--By maqsood -->
            PaginationBean paginationBean = new PaginationBean();
            paginationBean.setTotalElements((int) userList.getTotalElements());
            paginationBean.setTotalPages((int) userList.getTotalPages());
            paginationBean.setPageSize((int) userList.getSize());
            paginationBean.setPageNumber((int) userList.getNumber());
            long i = 0;

            if (orderBy.equals("id")) {
                if (direction.equals("DESC")) {
                    i = 1;
                    if (page > 1) {
                        for (int count = 1; count < page; count++) {
                            i = i + size;
                            System.out.println("index " + i);
                        }
                    }
                } else if (direction.equals("ASC")) {
                    i = userList.getTotalElements();
                    if (page > 1) {
                        for (int count = 1; count < page; count++) {
                            i = i - size;
                            System.out.println("index " + i);
                        }
                    }
                }
            } else {
                if (direction.equals("ASC")) {
                    i = 1;
                    if (page > 1) {
                        for (int count = 1; count < page; count++) {
                            i = i + size;
                            System.out.println("index " + i);
                        }
                    }
                } else if (direction.equals("DESC")) {
                    i = userList.getTotalElements();
                    if (page > 1) {
                        for (int count = 1; count < page; count++) {
                            i = i - size;
                            System.out.println("index " + i);
                        }
                    }
                }
            }

            //<--By maqsood -->
            List<UserDetailBean> userDetailBeanList = new ArrayList<>();
            for (User user : userList) {
                UserDetailBean userDetailBean = new UserDetailBean();

                //<--By maqsood -->
                while (i <= userList.getTotalElements()) {
                    userDetailBean.setSerialNo(i);
                    break;
                }

                if (orderBy.equals("id")) {

                    if (direction.equals("DESC"))
                        i++;
                    else if (direction.equals("ASC"))
                        i--;
                } else {

                    if (direction.equals("ASC"))
                        i++;
                    else if (direction.equals("DESC"))
                        i--;
                }

                userDetailBean.setStatus(user.getStatus().name());
                userDetailBean.setCnic(user.getCnicNo());
                userDetailBean.setEmail(user.getEmailAddress());
                userDetailBean.setCurrDate(CommonUtil.changeTimestampToString(user.getCreatedDate()));

                userDetailBean.setId(user.getId());
                userDetailBean.setMobileNo(user.getMobileNo());
                userDetailBean.setAddress(user.getAddress());
                userDetailBean.setFirstName(user.getFullName());

                List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<>();
                for (Taxform taxform : taxformList) {
                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();
                    taxformMinimalBean.setTaxformId(taxform.getId());
                    taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());

                    if (taxform.getStatus().getId() != 0) {
                        taxformMinimalBean.setStatus(taxform.getStatus().getStatus());
                    } else {
                        taxformMinimalBean.setStatus("PERSONAL INFO");
                        if (taxform.getTaxformIncomeTaxSalary() != null ||
                                taxform.getTaxformIncomeTaxProperty() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                                taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                                taxform.getTaxformIncomeTaxOtherSources() != null ||
                                (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                                (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {

                            taxformMinimalBean.setStatus("INCOME TAX");
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                            taxformMinimalBean.setStatus("TAX CREDITS & ALLOWANCES");
                        }

                        if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                                (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                                taxform.getTaxformTaxDeductedCollectedOther() != null) {
                            taxformMinimalBean.setStatus("TAX DEDUCTED");
                        }

                        if (taxform.getTaxformWelthStatement() != null) {
                            taxformMinimalBean.setStatus("WEALTH STATEMENT");
                        }
                    }
                    taxformMinimalBeanList.add(taxformMinimalBean);
                }
                userDetailBean.setTaxformMinimalBeans(taxformMinimalBeanList);

                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo != null) {
                    FbrUserAccountInfoBean fbrUserAccountInfoBean = new FbrUserAccountInfoBean();
                    fbrUserAccountInfoBean.setNtnStatus(fbrUserAccountInfo.getFbrUserAccountInfoStatus().toString());

                    fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean);
                }
                userDetailBean.setFbrUserAccountInfoBeanList(fbrUserAccountInfoBeanList);

                userDetailBeanList.add(userDetailBean);


            }
            StatusBean statusBean = new StatusBean(1, "Success");

            statusBean.setPagination(paginationBean);       // by maqsood

            statusBean.setResponse(userDetailBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }
    //<--End-->

    //get Lawyers...
    @RequestMapping(value = "/lawyers", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllLawyers() {
        try {
            List<User> userList = usersServices.findAllActiveLawyers();

            List<UserDetailBean> userDetailBeanList = new ArrayList<>();
            for (User user : userList) {
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
            StatusBean statusBean = new StatusBean(1, "Success");
            statusBean.setResponse(userDetailBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

}