package com.arittek.befiler_services.repository.user;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserLoginAttempts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLoginAttemptsRepository extends JpaRepository<UserLoginAttempts, Integer>, RevisionRepository<UserLoginAttempts, Integer, Integer> {
    public List<UserLoginAttempts> findAllByUser(User user);
    public List<UserLoginAttempts> findAllByUserAndStatus(User user, Boolean status);
    public List<UserLoginAttempts> findAllByStatusAndUserOrderByIdDesc(Boolean status, User user);
    public List<UserLoginAttempts> findAllByStatusAndUserAndIdGreaterThan(Boolean status, User user, Integer id);

}
