package com.arittek.befiler_services.repository.user;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.UserAttemptsCount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAttemptsCountRepository extends CrudRepository<UserAttemptsCount, Integer> {
    UserAttemptsCount findOneByUserAndStatus(User user, AppStatus status);
}
