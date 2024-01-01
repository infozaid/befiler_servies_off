package com.arittek.befiler_services.services.user;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.model.user.UserLoginAttempts;
import com.arittek.befiler_services.repository.corporateRepository.CorporateEmployeeRepository;
import com.arittek.befiler_services.repository.corporateRepository.CorporateRepository;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.repository.user.UserAttemptsCountRepository;
import com.arittek.befiler_services.repository.user.UserLoginAttemptsRepository;
import com.arittek.befiler_services.repository.user.UserPinRepository;
import com.arittek.befiler_services.repository.user.UserRepository;
import com.arittek.befiler_services.security.JwtUser;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsersServicesImpl implements  UsersServices{



    @Autowired
    UserRepository userRepository;

    @Autowired
    CorporateRepository corporateRepository;


    @Autowired
    UserLoginAttemptsRepository userLoginAttemptsRepository;

    @Autowired
    UserPinRepository userPinRepository;

    @Autowired
    CorporateEmployeeRepository corporateEmployeeRepository;

    /*@Autowired
    private AppStatusServices appStatusServices;*/

    @Autowired
    private UserAttemptsCountRepository userAttemptsCountRepository;

    @Autowired
    private RoleService roleService;

    /*@Override
    public User findOne(Integer id) {
        User user = userRepository.findOne(id);
        return user;
    }*/

    @Override
    public User getUserFromToken() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return userRepository.findById(((JwtUser) auth.getPrincipal()).getId()).orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public User findOneActiveLawyerRecordById(Integer id) throws Exception {
        if (id != null) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findOneLawyerRole());

            return userRepository.findOneByIdAndRolesAndStatus(id, roles, UserStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public User findOneByIdAndStatusIn(Integer userId, List<UserStatus> userStatusList) throws Exception {
        if (userId != null && userStatusList != null) {
            return userRepository.findOneByIdAndStatusIn(userId, userStatusList);
        }
        return null;
    }

    @Override
    public User findOneByIdAndStatus(Integer userId, UserStatus userStatus) throws Exception {
        if (userId != null && userStatus != null) {
            return userRepository.findOneByIdAndStatus(userId, userStatus);
        }
        return null;
    }

    @Override
    public User findOneByIdAndPassword(Integer id, String password) throws Exception {
        if (id != null && StringUtils.isNotEmpty(password)) {
            return userRepository.findOneByIdAndPassword(id, password);
        }
        return null;
    }

    @Override
    public User findOneByCnicNoAndPassword(String cnicNo, String password) throws Exception {
        if (cnicNo != null && password != null) {
            return userRepository.findOneByCnicNoAndPassword(cnicNo, password);
        }
        return null;
    }

    @Override
    public User findOneByCnicNoAndStatus(String cnicNo, UserStatus status) throws Exception {
        if (cnicNo != null && !cnicNo.isEmpty() && status != null) {
            return userRepository.findOneByCnicNoAndStatus(cnicNo, status);
        }
        return null;
    }

    @Override
    public User findOneByCnicNoWhereUserIsActiveOrDeactive(String cnicNo) throws Exception {
        if (cnicNo != null && !cnicNo.isEmpty()) {
            /*return userRepository.findOneByCnicNoAndStatusNotIn(cnicNo, this.findUserStatusById(0));*/
            return userRepository.findOneByCnicNoAndStatusNotIn(cnicNo, UserStatus.DEACTIVE);
        }
        return null;
    }

    @Override
    public User findOneByMobileNo(String mobileNo) throws Exception {
        if (StringUtils.isNotEmpty(mobileNo)) {
            return userRepository.findOneByMobileNo(mobileNo);
        }
        return null;
    }

    @Override
    public User findOneByEmailAddressAndPassword(String emailAddress, String password) throws Exception {
        if (emailAddress != null && password != null) {
            return userRepository.findOneByEmailAddressAndPassword(emailAddress, password);
        }
        return null;
    }

    @Override
    public User create(User user) throws Exception {
        if(user.getId() != null){
            return null;
        }
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public User update(User user) throws Exception {
        if(user.getId() != null){
            User userPersisted = userRepository.findById(user.getId()).orElse(null);
            if(userPersisted == null){
                return null;
            }
            User updatedUser = userRepository.save(user);
            return updatedUser;
        }
        return null;
    }

    @Override
    public User findOneByCnicNo(String cnic) throws Exception {

        User user = userRepository.findOneByCnicNo(cnic);
        return user;
    }

    @Override
    public User findOneByEmailAddress(String emailAddress) throws Exception {
        if (emailAddress != null) {
            return userRepository.findOneByEmailAddress(emailAddress);
        }
        return null;
    }

    @Override
    public User findOneByEmailAddressWhereUserIsActiveOrDeactive(String emailAddress) throws Exception {
        if (emailAddress != null && !emailAddress.isEmpty()) {
            /*return userRepository.findOneByEmailAddressAndStatusNotIn(emailAddress, this.findUserStatusById(2));*/
            return userRepository.findOneByEmailAddressAndStatusNotIn(emailAddress, UserStatus.DELETED);
        }
        return null;
    }

    @Override
    public User findByUsername(String username) throws Exception {
        User user = null;

        user = userRepository.findOneByEmailAddress(username);
        if(user == null){
            user = userRepository.findOneByCnicNo(username);
        }
        return user;
    }

    @Override
    public List<User> findAllByAuthorizerRole(User authorizer) throws Exception {
        if (authorizer != null && authorizer.getRoles() != null) {
            if (checkIfUserIsOperations(authorizer)) {
                return userRepository.findAllByCreatedBy(authorizer);
            } else {
                Set<Role> roles = new HashSet<>();
                roles.add(roleService.findOneCustomerRole());

                List<UserStatus> userStatusList = new ArrayList<>();
                userStatusList.add(UserStatus.DELETED);

                return findAllByStatusNotInAndRolesNotIn(userStatusList, roles);
            }
        }
        return null;
    }

    @Override
    public List<User> findAllByStatusNotInAndRolesNotIn(List<UserStatus> userStatusList,  Set<Role>  roles) throws Exception {
        if (userStatusList != null && userStatusList.size() > 0 && roles != null && roles.size() > 0) {
            return userRepository.findAllByStatusNotInAndRolesNotIn(userStatusList, roles);
        }
        return null;
    }

    @Override
    public UserLoginAttempts createUserLoginAttempt(UserLoginAttempts userLoginAttempts) throws Exception {
        return userLoginAttemptsRepository.save(userLoginAttempts);
    }

    @Override
    public UserLoginAttempts updateUserLoginAttempt(UserLoginAttempts userLoginAttempts) throws Exception {
        if(userLoginAttempts != null && userLoginAttempts.getId() != null){
            UserLoginAttempts userLoginAttemptsPersisted = findOneUserLoginAttempt(userLoginAttempts.getId());
            if(userLoginAttemptsPersisted == null){
                return null;
            }
            return userLoginAttemptsRepository.save(userLoginAttempts);
        }
        return null;
    }

    @Override
    public UserLoginAttempts findOneUserLoginAttempt(Integer userLoginAttemptId) throws Exception {
        UserLoginAttempts userLoginAttempts = userLoginAttemptsRepository.findById(userLoginAttemptId).orElse(null);
        return userLoginAttempts;
    }

    @Override
    public User findOneById(Integer id) {
        if(id != null){
            return userRepository.findById(id).get();
        }
        return null;
    }

    @Override
    public List<UserLoginAttempts> findAllUserLoginAttempts() {
        List<UserLoginAttempts> userLoginAttemptsList = (List<UserLoginAttempts>) userLoginAttemptsRepository.findAll();
        return userLoginAttemptsList;
    }

    @Override
    public List<UserLoginAttempts> findAllUserLoginAttemptsByUser(User user) {
        List<UserLoginAttempts> userLoginAttemptss = new ArrayList<>();
        if (user != null && user.getId() != null) {
            User userCheck = userRepository.findById(user.getId()).orElse(null);
            if (userCheck != null) {
                userLoginAttemptss = (List<UserLoginAttempts>) userLoginAttemptsRepository.findAllByUser(userCheck);
                return  userLoginAttemptss;
            }
        }
        return userLoginAttemptss;
    }

    @Override
    public List<UserLoginAttempts> findAllUserLoginAttemptsByUserAndStatus(User user, Boolean status) {
        List<UserLoginAttempts> userLoginAttemptss = new ArrayList<>();
        if (user != null && user.getId() != null && status != null) {
            User userCheck = userRepository.findById(user.getId()).orElse(null);
            if (userCheck != null) {
                userLoginAttemptss = (ArrayList<UserLoginAttempts>) userLoginAttemptsRepository.findAllByUserAndStatus(userCheck, status);
                return  userLoginAttemptss;
            }
        }
        return userLoginAttemptss;
    }

    @Override
    public Integer userIncorrectLoginAttemptsCount(User user) throws Exception {

        if (user != null && user.getId() != null) {
            List<UserLoginAttempts> userLoginAttemptss = userLoginAttemptsRepository.findAllByStatusAndUserOrderByIdDesc(true, user);
            if (userLoginAttemptss != null && userLoginAttemptss.size() > 0) {
                UserLoginAttempts userLoginAttempts = userLoginAttemptss.get(0);
                List<UserLoginAttempts> userLoginAttemptsSize = userLoginAttemptsRepository.findAllByStatusAndUserAndIdGreaterThan(false, user, userLoginAttempts.getId());
                return userLoginAttemptsSize.size();
            }
        }
        return null;
    }

    @Override
    public User findOneByCnicNoAndIdNotIn(String cnicNo, Integer userId) throws Exception {
        if (cnicNo != null && !cnicNo.isEmpty() && userId != null) {
            return userRepository.findOneByCnicNoAndIdNotIn(cnicNo, userId);
        }
        return null;
    }

    @Override
    public User findOneByEmailAddressAndIdNotIn(String emailAddress, Integer userId) throws Exception {
        if (emailAddress != null && !emailAddress.isEmpty() && userId != null) {
            return userRepository.findOneByEmailAddressAndIdNotIn(emailAddress, userId);
        }
        return null;
    }

    @Override
    public User findByEmailAddressOrCnicNo(String emailAddress, String cnicNo) throws Exception {
        if (emailAddress != null && !emailAddress.isEmpty() && cnicNo != null && !cnicNo.isEmpty()) {
            return userRepository.findOneByEmailAddressOrCnicNo(emailAddress, cnicNo);
        }
        return null;
    }

    @Override
    public User findByEmailAddressOrCnicNoAndIdNotIn(String emailAddress, String cnicNo, Integer userId) throws Exception {
        if (emailAddress != null && !emailAddress.isEmpty() && cnicNo != null && !cnicNo.isEmpty() && userId != null) {
            return userRepository.findOneByEmailAddressOrCnicNoAndIdNotIn(emailAddress, cnicNo, userId);
        }
        return null;
    }

    @Override
    public UserAttemptsCount findCountsActiveRecordByUser(User user) throws Exception {
        if (user != null) {
            return userAttemptsCountRepository.findOneByUserAndStatus(user, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public UserAttemptsCount increasePaymentCountByOne(User user, String reason) throws Exception {
        if (user != null) {
            UserAttemptsCount userAttemptsCount = findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null) {
                userAttemptsCount.setStatus(AppStatus.DE_ACTIVE);

                userAttemptsCount = userAttemptsCountRepository.save(userAttemptsCount);

                UserAttemptsCount userAttemptsCount1 = new UserAttemptsCount();
                userAttemptsCount1.setUser(user);
                userAttemptsCount1.setStatus(AppStatus.ACTIVE);
                userAttemptsCount1.setLoginAttemptsCount(userAttemptsCount.getLoginAttemptsCount());
                userAttemptsCount1.setPaymentAttemptsCount(userAttemptsCount.getPaymentAttemptsCount() + 1);
                userAttemptsCount1.setReason(reason);
                userAttemptsCount1.setCurrDate(CommonUtil.getCurrentTimestamp());

                return userAttemptsCountRepository.save(userAttemptsCount1);
            }
            userAttemptsCount = new UserAttemptsCount();
            userAttemptsCount.setUser(user);
            userAttemptsCount.setStatus(AppStatus.ACTIVE);
            userAttemptsCount.setLoginAttemptsCount(0);
            userAttemptsCount.setPaymentAttemptsCount(1);
            userAttemptsCount.setReason(reason);
            userAttemptsCount.setCurrDate(CommonUtil.getCurrentTimestamp());

            return userAttemptsCountRepository.save(userAttemptsCount);
        }
        return null;
    }

    @Override
    public UserAttemptsCount resetPaymentCount(User user, String reason) throws Exception {
        if (user != null && reason != null) {
            UserAttemptsCount userAttemptsCount = findCountsActiveRecordByUser(user);
            if (userAttemptsCount != null) {
                /*userAttemptsCount.setStatus(appStatusServices.findOneByDeActiveStatus());*/
                userAttemptsCount.setStatus(AppStatus.DE_ACTIVE);

                userAttemptsCount = userAttemptsCountRepository.save(userAttemptsCount);

                UserAttemptsCount userAttemptsCount1 = new UserAttemptsCount();
                userAttemptsCount1.setUser(user);
                /*userAttemptsCount1.setStatus(appStatusServices.findOneByActiveStatus());*/
                userAttemptsCount1.setStatus(AppStatus.ACTIVE);
                userAttemptsCount1.setLoginAttemptsCount(userAttemptsCount.getLoginAttemptsCount());
                userAttemptsCount1.setPaymentAttemptsCount(0);
                userAttemptsCount1.setReason(reason + " :: Reset");
                userAttemptsCount1.setCurrDate(CommonUtil.getCurrentTimestamp());

                return userAttemptsCountRepository.save(userAttemptsCount1);
            }
            userAttemptsCount = new UserAttemptsCount();
            userAttemptsCount.setUser(user);
            /*userAttemptsCount.setStatus(appStatusServices.findOneByActiveStatus());*/
            userAttemptsCount.setStatus(AppStatus.ACTIVE);
            userAttemptsCount.setLoginAttemptsCount(0);
            userAttemptsCount.setPaymentAttemptsCount(0);
            userAttemptsCount.setReason(reason + " :: Reset");
            userAttemptsCount.setCurrDate(CommonUtil.getCurrentTimestamp());

            return userAttemptsCountRepository.save(userAttemptsCount);
        }
        return null;
    }

    @Override
    public List<User> findAllActiveLawyers() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneLawyerRole());

        return userRepository.findAllByRolesAndStatus(roles, UserStatus.ACTIVE);
    }

    @Override
    public List<User> findAllActiveCustomers() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneCustomerRole());

        return userRepository.findAllByRolesAndStatus(roles, UserStatus.ACTIVE);
    }

    @Override
    public List<User> findAllActiveCustomersDesc() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneCustomerRole());

        return userRepository.findAllByRolesAndStatusOrderByIdDesc(roles, UserStatus.ACTIVE);
    }

    @Override
    public List<User> findAllCustomersDesc() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneCustomerRole());

        return userRepository.findAllByRolesOrderByIdDesc(roles);
    }

    @Override
    public Page<User> findAllCustomers(String orderBy, String direction, int page, int size) throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneCustomerRole());

        Sort sort = null;
        if (direction.equals("ASC")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, orderBy));
        }
        if (direction.equals("DESC")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.DESC, orderBy));
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAllByRoles(roles, pageable);
    }

    //<--Gulshan Kumar-->
    @Override
    public Page<User> findAllCustomers(String orderBy, String direction, int page, int size, String search) throws Exception {
        Sort sort = null;
        if (direction.equals("ASC")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, orderBy));
        }
        if (direction.equals("DESC")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.DESC, orderBy));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAllCustomer(search, pageable);
    }
    //<--End-->

    @Override
    public Boolean checkIfUserIsAdmin(User user) throws Exception {
        if (user != null && user.getRoles() != null && user.getRoles().contains(roleService.findOneAdminRole())) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfUserIsLawyer(User user) throws Exception {
        if (user != null && user.getRoles() != null && user.getRoles().contains(roleService.findOneLawyerRole())) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfUserIsAccountant(User user) throws Exception {
        if (user != null && user.getRoles() != null && user.getRoles().contains(roleService.findOneAccountantRole())) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfUserIsCustomer(User user) throws Exception {
        if (user != null && user.getRoles() != null && user.getRoles().contains(roleService.findOneCustomerRole())) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkIfUserIsOperations(User user) throws Exception {
        if (user != null && user.getRoles() != null && user.getRoles().contains(roleService.findOneOperationsRole())) {
            return true;
        }
        return false;
    }

    @Override
    public Integer getActiveCustomerCount()throws Exception{
//        return userRepository.findByStatus();
        return null;
    }


}
