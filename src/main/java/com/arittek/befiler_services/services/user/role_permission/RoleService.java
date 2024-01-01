package com.arittek.befiler_services.services.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Role;

import java.util.List;

public interface RoleService {


    Role findOne(Integer id);
    Role findByName(String name);

    Role create(Role rolePermissions);
    Role delete(Role rolePermissions);

    Role update(Role rolePermissions);

    Role findOneAdminRole() throws Exception;
    Role findOneLawyerRole() throws Exception;
    Role findOneAccountantRole() throws Exception;
    Role findOneCustomerRole() throws Exception;
    Role findOneOperationsRole() throws Exception;

    List<Role> findAllByActive();
}
