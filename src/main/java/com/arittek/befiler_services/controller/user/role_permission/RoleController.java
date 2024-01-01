package com.arittek.befiler_services.controller.user.role_permission;

import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.userModule.PermissionBean;
import com.arittek.befiler_services.beans.userModule.PermissionParentBean;
import com.arittek.befiler_services.beans.userModule.RoleBean;
import com.arittek.befiler_services.model.user.role_permission.Permission;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.role_permission.PermissionService;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;

//import com.arittek.beans.userModule.PermissionBean;


@Controller
@RequestMapping("/role")
public class RoleController {

    private RoleService roleService;
    private PermissionService permissionService;

    @Autowired
    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Transactional()
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Status> create(@RequestBody RoleBean roleBean) {
        try {
            if (roleBean.getName() == null) {
                return new ResponseEntity<Status>(new Status(0, "Please enter role title!"), HttpStatus.OK);
            }
            if (roleBean.getName().length() > 255) {
                return new ResponseEntity<Status>(new Status(0, "Role title can not greater then 255 characters!"), HttpStatus.OK);
            }
            if (roleService.findByName(roleBean.getName()) != null) {
                return new ResponseEntity<Status>(new Status(0, roleBean.getName() + " already exits!"), HttpStatus.OK);
            }
            Role role = new Role();
            if (roleBean.getName() != null) {
                role.setName(roleBean.getName());
                role.setDisplay_name(roleBean.getName());
            }
            if (roleBean.getDescription() != null) {
                if (roleBean.getDescription().length() > 255) {
                    return new ResponseEntity<Status>(new Status(0, "Role description can not greater then 255 characters!"), HttpStatus.OK);
                }
                role.setDescription(roleBean.getDescription());
            }
            if (roleBean.getName() != null) {
                role.setName(roleBean.getName());
            }
            role.setDescription(roleBean.getDescription());
            role.setIsActive(1);
            if (roleBean.getPermissionParentBeanList() != null && !roleBean.getPermissionParentBeanList().isEmpty()) {
                role.setPermissionList(new ArrayList<>());
                for (PermissionParentBean permissionParentBean : roleBean.getPermissionParentBeanList()) {
                    for (PermissionBean permissionBean : permissionParentBean.getPermissionList()) {
                        if (permissionBean.isSelected()) {
                            Permission permission = new Permission();
                            if (permissionBean.getId() != null) {
                                permission.setId(permissionBean.getId());
                                role.getPermissionList().add(permission);
                            }
                        }
                    }
                }
            }
            roleService.create(role);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<Status>(new Status(0, "Exception occurred: " + e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(1, "Role Created!"), HttpStatus.OK);
    }

    @Transactional()
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public ResponseEntity<Status> update(@RequestBody RoleBean roleBean) {

        try {
            Role role = null;
            if (roleBean.getId() == null || roleBean.getName() == null) {
                return new ResponseEntity<Status>(new Status(0, "Please enter role title!"), HttpStatus.OK);
            }
            if ((role = roleService.findOne(roleBean.getId())) == null) {
                return new ResponseEntity<Status>(new Status(0, "Role not found!"), HttpStatus.OK);
            }

            if (roleBean.getName() != null) {
                role.setName(roleBean.getName());
                role.setDisplay_name(roleBean.getName());
            }
            if (roleBean.getDescription() != null) {
                role.setDescription(roleBean.getDescription());
            }
            if (roleBean.getIsActive() != null) {
                role.setIsActive(roleBean.getIsActive());
            }

            if (roleBean.getPermissionParentBeanList() != null && !roleBean.getPermissionParentBeanList().isEmpty()) {
                role.setPermissionList(new ArrayList<>());
                for (PermissionParentBean permissionParentBean : roleBean.getPermissionParentBeanList()) {
                    for (PermissionBean permissionBean : permissionParentBean.getPermissionList()) {
                        if (permissionBean.isSelected()) {
                            Permission permission = new Permission();
                            if (permissionBean.getId() != null) {
                                permission.setId(permissionBean.getId());
                            }
                            if (permissionBean.getName() != null) {
                                permission.setName(permission.getName());
                            }
                            if (permissionBean.getControllerName() != null) {
                                permission.setControllerName(permission.getControllerName());
                            }
                            if (permissionBean.getServiceName() != null) {
                                permission.setServiceName(permission.getServiceName());
                            }
                            if (permissionBean.getIsActive() != null) {
                                permission.setIsActive(permission.getIsActive());
                            }

                            role.getPermissionList().add(permission);
                        }
                    }
                }
            }

            roleService.update(role);

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<Status>(new Status(0, "Exception occurred!"), HttpStatus.OK);
        }

        return new ResponseEntity<Status>(new Status(1, "Role updated!"), HttpStatus.OK);
    }

    @Transactional()
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResponseEntity<Status> delete(@RequestBody Role role) {
        try {

            if (roleService.findOne(role.getId()) == null) {
                return new ResponseEntity<Status>(new Status(0, "Role not found!"), HttpStatus.OK);
            } else {
                Role persistRole = roleService.findOne(role.getId());
                if (persistRole != null) {
                    if(persistRole.getUserList()==null || persistRole.getUserList().isEmpty()){
                        roleService.delete(roleService.findOne(role.getId()));
                    }else {
                        return new ResponseEntity<Status>(new Status(0, "Role can't be delete! It is assigned to users"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<Status>(new Status(0, "Role not found!"), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<Status>(new Status(0, "Exception occurred!"), HttpStatus.OK);

        }
        return new ResponseEntity<Status>(new Status(1, "Role deleted successfully!"), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<RoleBean>> getRoleList() {
        ArrayList<Role> roleList = null;
        ArrayList<RoleBean> roleBeanList = new ArrayList<>();
        try {
            roleList = (ArrayList) roleService.findAllByActive();
            if (roleList != null) {
                for (Role role : roleList) {
                    RoleBean roleBean = new RoleBean();
                    roleBean.setId(role.getId());
                    if (role.getName() != null) {
                        roleBean.setName(role.getName());
                    }
                    if (role.getDescription() != null) {
                        roleBean.setDescription(role.getDescription());
                    }
                    roleBeanList.add(roleBean);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(roleBeanList, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public ResponseEntity<RoleBean> getRoleByID(@PathVariable(value = "id") Integer id) {
        Role role = null;
        RoleBean roleBean = new RoleBean();
        try {
            role = roleService.findOne(id);
            if (role != null) {
                roleBean.setId(role.getId());
                roleBean.setPermissionParentBeanList(new ArrayList<>());
                if (role.getName() != null) {
                    roleBean.setName(role.getName());
                }
                if (role.getDescription() != null) {
                    roleBean.setDescription(role.getDescription());
                }
                if (role.getCreatedDate() != null) {
                    roleBean.setCreateDate(CommonUtil.changeTimestampToString(role.getCreatedDate()));
                }
                if (role.getIsActive() != null) {
                    roleBean.setIsActive(role.getIsActive());
                }
                if (role.getPermissionList() != null) {
                    roleBean.setIsActive(role.getIsActive());
                }

                ArrayList<PermissionParentBean> permissionParentBeanList = new ArrayList<>();
                ArrayList<String> controllerList = null;
                try {
                    controllerList = (ArrayList) permissionService.getAllByTableName();
                    if (controllerList != null) {
                        for (String controllerName : controllerList) {
                            ArrayList<Permission> permissionByControllerNameList = (ArrayList<Permission>) permissionService.findAllByIsActiveAndControllerName(1, controllerName);

                            if (permissionByControllerNameList != null && !permissionByControllerNameList.isEmpty()) {
                                PermissionParentBean permissionParentBean = new PermissionParentBean();
                                permissionParentBean.setTitle(controllerName);
                                permissionParentBean.setPermissionList(new ArrayList<>());
                                for (Permission permission : permissionByControllerNameList) {
                                    PermissionBean permissionBean = new PermissionBean();
                                    permissionBean.setId(permission.getId());
                                    if (permission.getControllerName() != null) {
                                        permissionBean.setControllerName(permission.getControllerName());
                                    }
                                    if (permission.getName() != null) {
                                        permissionBean.setName(permission.getName());
                                    }
                                    if (permission.getServiceName() != null) {
                                        permissionBean.setServiceName(permission.getServiceName());
                                    }
                                    if (permission.getIsActive() != null) {
                                        permissionBean.setIsActive(permission.getIsActive());
                                    }

                                    permissionBean.setSelected(false);
                                    permissionParentBean.getPermissionList().add(permissionBean);
                                }
                                permissionParentBeanList.add(permissionParentBean);
                            }

                        }
                    }
                } catch (Exception e) {
                    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                    Logger4j.getLogger().error("Exception : " + e);
                    e.printStackTrace();
                }

                for (Permission permission : role.getPermissionList()) {
                    int permissionParentIndex = 0;
                    for (PermissionParentBean permissionParentBean : permissionParentBeanList) {
                        if (permissionParentBean.getTitle().equalsIgnoreCase(permission.getControllerName())) {
                            int permissionBeanIndex = 0;
                            for (PermissionBean permissionBean : permissionParentBean.getPermissionList()) {
                                if (permissionBean.getId() == (permission.getId())) {
                                    permissionParentBeanList.get(permissionParentIndex).getPermissionList().get(permissionBeanIndex).setSelected(true);
                                }
                                permissionBeanIndex++;
                            }
                        }
                        permissionParentIndex++;
                    }
                }

                roleBean.setPermissionParentBeanList(permissionParentBeanList);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            e.printStackTrace();
        }
        return new ResponseEntity<RoleBean>(roleBean, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "permissionList", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<PermissionParentBean>> getAllPermissions() {
        ArrayList<PermissionParentBean> permissionParentBeanList = new ArrayList<>();
        ArrayList<String> controllerList = null;
        try {
            controllerList = (ArrayList) permissionService.getAllByTableName();
            if (controllerList != null) {
                for (String controllerName : controllerList) {
                    ArrayList<Permission> permissionByControllerNameList = (ArrayList<Permission>) permissionService.findAllByIsActiveAndControllerName(1, controllerName);

                    if (permissionByControllerNameList != null && !permissionByControllerNameList.isEmpty()) {
                        PermissionParentBean permissionParentBean = new PermissionParentBean();
                        permissionParentBean.setTitle(controllerName);
                        permissionParentBean.setPermissionList(new ArrayList<>());
                        for (Permission permission : permissionByControllerNameList) {
                            PermissionBean permissionBean = new PermissionBean();
                            permissionBean.setId(permission.getId());
                            if (permission.getControllerName() != null) {
                                permissionBean.setControllerName(permission.getControllerName());
                            }
                            if (permission.getName() != null) {
                                permissionBean.setName(permission.getName());
                            }
                            if (permission.getServiceName() != null) {
                                permissionBean.setServiceName(permission.getServiceName());
                            }
                            if (permission.getIsActive() != null) {
                                permissionBean.setIsActive(permission.getIsActive());
                            }
                            permissionBean.setSelected(false);
                            permissionParentBean.getPermissionList().add(permissionBean);
                        }
                        permissionParentBeanList.add(permissionParentBean);
                    }

                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            e.printStackTrace();
        }
        return new ResponseEntity<ArrayList<PermissionParentBean>>(permissionParentBeanList, HttpStatus.OK);
    }

}
