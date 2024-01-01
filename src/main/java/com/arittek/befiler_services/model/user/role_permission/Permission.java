package com.arittek.befiler_services.model.user.role_permission;

import com.arittek.befiler_services.model.generic.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="rp_um_permissions" )
public class Permission extends com.arittek.befiler_services.model.generic.GenericModel {
    private String name;
    private String serviceName;
    private String controllerName;
    private Integer isActive;

    @Transient
    private boolean isSelected;


    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "permissionList")
    @JsonBackReference
    private List<Role> roleList = new ArrayList<>();
}
