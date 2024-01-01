package com.arittek.befiler_services.repository.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer>, RevisionRepository<Permission, Integer, Integer> {
    List<Permission> findAll();
    List<Permission> deleteByIdIn(Integer id);
    List<Permission> findAllByIsActiveOrderByControllerName(Integer isActive);
    List<Permission> findAllByIsActiveAndControllerName(Integer isActive, String controllerName);

    Permission findByNameAndIsActive(String name, Integer isActive);


    @Query("SELECT DISTINCT(per.controllerName) FROM Permission per where per.isActive = 1 ")
    List<Permission> getAllByTableName();
}
