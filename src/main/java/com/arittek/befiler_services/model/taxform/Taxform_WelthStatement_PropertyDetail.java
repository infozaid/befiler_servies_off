package com.arittek.befiler_services.model.taxform;

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
@Table(name = "taxform_welth_statement_property_detail")
public class Taxform_WelthStatement_PropertyDetail extends GenericModel{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "property_type")
    private String propertyType;

    @Column(name = "unit_no")
    private String unitNo;

    @Column(name = "area_locality_road")
    private String areaLocalityRoad;

    @Column(name = "city")
    private String city;

    @Column(name = "area")
    private String area;

    @Column(name = "property_cost")
    private Double propertyCost;
}
