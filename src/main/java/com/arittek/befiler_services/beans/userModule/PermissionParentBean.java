package com.arittek.befiler_services.beans.userModule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class PermissionParentBean {

    private String title;
    private ArrayList<PermissionBean> permissionList;

}
