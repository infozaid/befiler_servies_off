package com.arittek.befiler_services.services.user;

import com.arittek.befiler_services.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserLawyerServices {

    User save(User lawyer);
    User update(User lawyer);
    boolean remove(User lawyer);

    public List<User> getAllActiveLawyers()throws Exception;

    public User findOne(Integer id);
}
