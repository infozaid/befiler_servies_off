package com.arittek.befiler_services.repository.user;

import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, RevisionRepository<User, Integer, Integer> {



    User findOneByIdAndStatusIn(Integer userId, List<UserStatus> userStatusList);

    User findOneByIdAndStatus(Integer userId, UserStatus userStatus);
    User findOneByIdAndPassword(Integer userId, String password);

    User findOneByCnicNo(String cnicNo);
    User findOneByCnicNoAndStatus(String cnicNo, UserStatus status);
    User findOneByCnicNoAndStatusNotIn(String cnicNo, UserStatus status);

    User findOneByEmailAddress(String emailAddress);
    User findOneByEmailAddressAndStatusNotIn(String cnicNo, UserStatus status);

    User findOneByCnicNoAndIdNotIn(String cnicNo, Integer userId);
    User findOneByEmailAddressAndIdNotIn(String emailAddress, Integer userId);
    User findOneByCnicNoAndPassword(String cnicNo, String password);
    User findOneByEmailAddressOrCnicNo(String emailAddress, String cnicNo);
    User findOneByEmailAddressAndPassword(String emailAddress, String password);
    User findOneByEmailAddressOrCnicNoAndIdNotIn(String emailAddress, String cnicNo, Integer userId);

    User findOneByMobileNo(String mobileNo);

    List<User> findAll();

    List<User> findAllByCreatedBy(User createdBy);

    /*List<User> findAllByUserTypeAndStatus(UserType userType, UserStatus userStatus);

    List<User> findAllByStatusAndUserTypeIn(UserStatus userStatus, List<UserType> userTypeList);
    List<User> findAllByStatusAndUserTypeNotIn(UserStatus userStatus, List<UserType> userTypeList);

    List<User> findAllByStatusInAndUserTypeIn(List<UserStatus> userStatusList, List<UserType> userTypeList);
    List<User> findAllByStatusNotInAndUserTypeNotIn(List<UserStatus> userStatusList, List<UserType> userTypeList);*/

    User findOneByIdAndRolesAndStatus(Integer id, Set<Role> roles, UserStatus status);

    List<User> findAllByRolesAndStatus(Set<Role> roles, UserStatus status);

//    @Query(value = "SELECT COUNT(u.status) FROM roles l JOIN  User u ON l.user_id = u.id JOIN  Role r ON l.role_id =r.id WHERE r.id = 4 AND u.status=1", nativeQuery = true)
//    Integer findCountByRolesAndStatus();

//    @Query(value = "SELECT * from User u where u.status='1'", nativeQuery = true)
//    List<User> findByStatus();

    List<User> findAllByRolesAndStatusOrderByIdDesc(Set<Role> roles, UserStatus status);

    List<User> findAllByRolesOrderByIdDesc(Set<Role> roles);
    Page<User> findAllByRoles(Set<Role> roles, Pageable pageable);

    //<--Gulshan Kumar-->
    @Query(value = "SELECT u FROM User u INNER JOIN FETCH u.roles role WHERE role.id = 4  AND (u.status LIKE CONCAT('%',?1,'%') OR LOWER(u.fullName) like CONCAT('%',?1,'%') OR LOWER(u.emailAddress) like CONCAT('%',?1,'%') OR u.cnicNo like CONCAT('%',?1,'%') OR u.mobileNo like CONCAT('%',?1,'%') OR u.createdDate like CONCAT('%',?1,'%'))",
            countQuery = "SELECT count(u) FROM User u INNER JOIN u.roles role WHERE role.id = 4  AND (u.status LIKE CONCAT('%',?1,'%')  OR LOWER(u.fullName) like CONCAT('%',?1,'%') OR LOWER(u.emailAddress) like CONCAT('%',?1,'%') OR u.cnicNo like CONCAT('%',?1,'%') OR u.mobileNo like CONCAT('%',?1,'%') OR u.createdDate like CONCAT('%',?1,'%'))")
    Page<User> findAllCustomer(String search, Pageable pageable);
    ///<--End-->
    List<User> findAllByStatusNotInAndRolesNotIn(List<UserStatus> userStatusList, Set<Role> roles);
}