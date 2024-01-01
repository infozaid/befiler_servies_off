package com.arittek.befiler_services.model;

import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "terms_and_conditions")
public class TermsAndConditions extends GenericModel {

    @Column(name = "type")
    private Integer type;

    @Column(name = "type_description")
    private String typeDescription;

    @Column(name = "title")
    private String title;

    @Column(name = "descritpion")
    private String description;

    @Column(name = "status")
    private Integer status;
}
