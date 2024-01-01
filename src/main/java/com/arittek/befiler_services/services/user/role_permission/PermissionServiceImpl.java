package com.arittek.befiler_services.services.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Permission;
import com.arittek.befiler_services.repository.user.role_permission.PermissionRepository;
import com.arittek.befiler_services.services.user.role_permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionRepository permissionRepository;
    @Override
    public Permission findOne(Integer id) {
        return permissionRepository.findById(id).get();
    }

    @Override
    public Permission findByName(String name) {
        return permissionRepository.findByNameAndIsActive(name,1);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Permission create(Permission permission) {
        if(permission.getId()!=null){
            return  null;
        }
        Permission saved = permissionRepository.save(permission);
        return saved;
    }

    @Override
    public Permission delete(Permission permission) {
        if(permission.getId() != null){
            Permission persisted = findOne(permission.getId());
            if(persisted == null){
                return null;
            }
            permission.setIsActive(0);
            permissionRepository.save(permission);
            return persisted;
        }
        return null;
    }

    @Override
    public Permission update(Permission permission) {
        if(permission.getId() != null){
            Permission persisted = findOne(permission.getId());
            if(persisted == null){
                return null;
            }
            Permission updatedBranch = permissionRepository.save(permission);
            return updatedBranch;
        }
        return null;
    }

    @Override
    public List<Permission> findAllByActive() {
        return permissionRepository.findAllByIsActiveOrderByControllerName(1);
    }

    @Override
    public List<Permission> getAllByTableName(){
        return permissionRepository.getAllByTableName();
    }

    @Override
    public List<Permission> findAllByIsActiveAndControllerName(Integer isActive, String controllerName) {
        return permissionRepository.findAllByIsActiveAndControllerName(isActive,controllerName);
    }
}
