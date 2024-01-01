package com.arittek.befiler_services.services.user;


import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.repository.user.UserLawyerRepository;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserLawyerServicesImpl implements UserLawyerServices{

    UserLawyerRepository userLawyerRepository;
    RoleService roleService;

    @Autowired
    public UserLawyerServicesImpl(UserLawyerRepository userLawyerRepository, RoleService roleService) {
        this.userLawyerRepository = userLawyerRepository;
        this.roleService=roleService;
    }

    @Override
    public User save(User lawyer) {
        if(lawyer.getId() != null){
            return null;
        }
        User savedUser = userLawyerRepository.save(lawyer);
        return savedUser;
    }

    @Override
    public User update(User lawyer) {
        if(lawyer != null) {
            return userLawyerRepository.save(lawyer);
        }
        return null;
    }

    @Override
    public boolean remove(User lawyer) {
        return false;
    }

    @Override
    public List<User> getAllActiveLawyers() throws Exception{
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findOneLawyerRole());

        return userLawyerRepository.findAllByRolesAndStatus(roles, UserStatus.ACTIVE);
    }

    @Override
    public User findOne(Integer id) {
        if(id != null){
        return userLawyerRepository.findById(id).get();
        }
        return null;
    }
}
