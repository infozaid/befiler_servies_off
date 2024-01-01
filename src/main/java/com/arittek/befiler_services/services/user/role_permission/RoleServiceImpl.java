package com.arittek.befiler_services.services.user.role_permission;

import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.repository.user.role_permission.RoleRepository;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findOne(Integer roleId) {
        return roleRepository.findOneByIdAndIsActive(roleId, 1);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByNameAndIsActive(name,1);
    }

    @Override
    public Role create(Role role) {
        if(role.getId()!=null){
            return  null;
        }
        Role saved = roleRepository.save(role);
        return saved;
    }

    @Override
    public Role delete(Role role) {
        if(role.getId() != null){
            Role persisted = findOne(role.getId());
            if(persisted == null){
                return null;
            }
            role.setIsActive(0);
            roleRepository.save(role);
            return persisted;
        }
        return null;
    }

    @Override
    public Role update(Role role) {
        if(role.getId() != null){
            Role persisted = findOne(role.getId());
            if(persisted == null){
                return null;
            }
            Role updatedBranch = roleRepository.save(role);
            return updatedBranch;
        }
        return null;
    }

    @Override
    public Role findOneAdminRole() throws Exception {
        return roleRepository.findOneByIdAndIsActive(1, 1);
    }

    @Override
    public Role findOneLawyerRole() throws Exception {
        return roleRepository.findOneByIdAndIsActive(2, 1);
    }

    @Override
    public Role findOneAccountantRole() throws Exception {
        return roleRepository.findOneByIdAndIsActive(3, 1);
    }

    @Override
    public Role findOneCustomerRole() throws Exception {
        return roleRepository.findOneByIdAndIsActive(4, 1);
    }

    @Override
    public Role findOneOperationsRole() throws Exception {
        return roleRepository.findOneByIdAndIsActive(5, 1);
    }

    @Override
    public List<Role> findAllByActive() {
        return roleRepository.findAllByIsActive(1);
    }

}
