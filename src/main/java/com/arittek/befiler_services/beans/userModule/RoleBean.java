package com.arittek.befiler_services.beans.userModule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class RoleBean {

    private Integer id;
    private String name;
    private String description;
    private Integer isEditable;
    private String dashboard;
    private ArrayList<PermissionParentBean>  permissionParentBeanList= new ArrayList<>();

    private Integer authorizer;
    private Timestamp authDate;
    private Integer inputter;
    private String createDate;
    private Integer isActive;


}
