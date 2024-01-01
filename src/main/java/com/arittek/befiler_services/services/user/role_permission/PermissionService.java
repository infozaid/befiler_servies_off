package com.arittek.befiler_services.services.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Permission;

import java.util.List;

public interface PermissionService {


    Permission findOne(Integer id);
    Permission findByName(String name);

    List<Permission> findAll();

    Permission create(Permission rolePermissions);
    Permission delete(Permission rolePermissions);

    Permission update(Permission rolePermissions);

    List<Permission> findAllByActive();

    List<Permission> getAllByTableName();
    List<Permission> findAllByIsActiveAndControllerName(Integer isActive, String controllerName);

}
