package com.arittek.befiler_services.beans.userModule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class PermissionBean {

    private Integer id;
    private String name;
    private String serviceName;
    private String controllerName;
    private String created_at;
    private String updated_at;
    private Integer isActive;

    private boolean isSelected;




}
