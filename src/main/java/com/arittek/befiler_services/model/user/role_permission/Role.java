package com.arittek.befiler_services.model.user.role_permission;

import com.arittek.befiler_services.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
/*@EqualsAndHashCode(callSuper = true)*/
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "rp_um_roles")
public class Role extends com.arittek.befiler_services.model.generic.GenericModel {

    private String display_name;
    private String description;
    private Integer isEditable;
    private String dashboard;

    @Column(name = "name")
    private String name;

    @Column(name = "is_active")
    private Integer isActive;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name = "rp_um_permission_role", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {
            @JoinColumn(name = "permission_id")})
    @JsonIgnore
    private List<Permission> permissionList = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.ALL}, mappedBy = "roles")
    @JsonIgnore
    private Set<User> userList = new HashSet<>();

    @Transient
    private Set<String> permissionsNameList;

    public Set<String> getPermissionsNameList() {
        permissionsNameList = new HashSet<String>();
        List<Permission> perlist = getPermissionList();
        for (Permission per : perlist) {
            if(per.getIsActive()==1){
                permissionsNameList.add(per.getName());
            }

        }
        return permissionsNameList;
    }
}
