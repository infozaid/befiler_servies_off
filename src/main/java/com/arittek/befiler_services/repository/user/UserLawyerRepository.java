package com.arittek.befiler_services.repository.user;


import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.List;
import java.util.Set;

public interface UserLawyerRepository extends JpaRepository<User, Integer>, RevisionRepository<User, Integer, Integer> {

    List<User> findAllByRolesAndStatus(Set<Role> roles, UserStatus status);
}
