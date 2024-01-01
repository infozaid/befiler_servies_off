package com.arittek.befiler_services.services.user;

import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import com.arittek.befiler_services.model.user.UserLoginAttempts;
import com.arittek.befiler_services.model.user.role_permission.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

public interface UsersServices {

    User getUserFromToken() throws Exception;

    User findOneActiveLawyerRecordById(Integer id) throws Exception;

    User findOneByCnicNo(String cnic) throws Exception;
    User findOneByCnicNoAndStatus(String cnicNo, UserStatus status) throws Exception;
    User findOneByCnicNoWhereUserIsActiveOrDeactive(String cnicNo) throws Exception;

    User findOneByMobileNo(String mobileNo) throws Exception;

    User findOneByEmailAddress(String emailAddress) throws Exception;
    User findOneByEmailAddressWhereUserIsActiveOrDeactive(String emailAddress) throws Exception;

    User findByUsername(String username) throws Exception;
    User findOneByIdAndStatusIn(Integer userId, List<UserStatus> userStatusList) throws Exception;
    User findOneByIdAndStatus(Integer userId, UserStatus userStatus) throws Exception;
    User findOneByIdAndPassword(Integer id, String password)throws Exception;

    User findOneByCnicNoAndPassword(String cnicNo, String password) throws Exception;
    User findOneByEmailAddressAndPassword(String emailAddress, String password) throws Exception;

    User findOneByCnicNoAndIdNotIn(String cnicNo, Integer userId) throws Exception;
    User findOneByEmailAddressAndIdNotIn(String emailAddress, Integer userId) throws Exception;
    User findByEmailAddressOrCnicNo(String emailAddress, String cnicNo) throws Exception;
    User findByEmailAddressOrCnicNoAndIdNotIn(String emailAddress, String cnicNo, Integer userId) throws Exception;
    User findOneById(Integer id);

    User create(User user) throws Exception;
    User update(User user) throws Exception;

    List<User> findAllByStatusNotInAndRolesNotIn(List<UserStatus> userStatusList, Set<Role> roles) throws Exception;

    /*----------USER LOGIN ATTEMPTS SERVICES----------*/
    UserLoginAttempts createUserLoginAttempt(UserLoginAttempts userLoginAttempts) throws Exception;
    UserLoginAttempts updateUserLoginAttempt(UserLoginAttempts userLoginAttempts) throws Exception;
    UserLoginAttempts findOneUserLoginAttempt(Integer userLoginAttemptId) throws Exception;
    List<UserLoginAttempts> findAllUserLoginAttempts();
    List<UserLoginAttempts> findAllUserLoginAttemptsByUser(User user);
    List<UserLoginAttempts> findAllUserLoginAttemptsByUserAndStatus(User user, Boolean status);
    Integer userIncorrectLoginAttemptsCount(User user) throws Exception;

    /*USER ATTEMPTS COUNT SERVICES*/
    UserAttemptsCount findCountsActiveRecordByUser(User user) throws Exception;
    UserAttemptsCount increasePaymentCountByOne(User user, String reason) throws Exception;
    UserAttemptsCount resetPaymentCount(User user, String reason) throws Exception;

    List<User> findAllByAuthorizerRole(User authorizer) throws Exception;

    List<User> findAllActiveLawyers() throws Exception;
    List<User> findAllActiveCustomers() throws Exception;
    List<User> findAllActiveCustomersDesc() throws Exception;

    List<User> findAllCustomersDesc() throws Exception;
    Page<User> findAllCustomers(String orderBy, String direction, int page, int size) throws Exception;

    //<--Gulshan Kumar-->
    Page<User> findAllCustomers(String orderBy, String direction, int page, int size,String search) throws Exception;
    //<--End-->

    Boolean checkIfUserIsAdmin(User user) throws Exception;
    Boolean checkIfUserIsLawyer(User user) throws Exception;
    Boolean checkIfUserIsAccountant(User user) throws Exception;
    Boolean checkIfUserIsCustomer(User user) throws Exception;
    Boolean checkIfUserIsOperations(User user) throws Exception;

    Integer getActiveCustomerCount()throws Exception;
}
