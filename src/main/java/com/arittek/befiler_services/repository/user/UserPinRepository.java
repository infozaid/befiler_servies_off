package com.arittek.befiler_services.repository.user;

import com.arittek.befiler_services.model.user.UserPin;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserPinRepository extends CrudRepository<UserPin, Integer> {

    UserPin findByCode(String code);
    UserPin findAllByUserAndCode(User user, String code);

}
