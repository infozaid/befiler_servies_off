package com.arittek.befiler_services.repository.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>, RevisionRepository<Role, Integer, Integer> {

    Role findOneByIdAndIsActive(Integer roleId, Integer isActive);
    Role findByNameAndIsActive(String name, Integer isActive);

    List<Role> findAll();
    List<Role> findAllByIsActive(Integer isActive);


}
