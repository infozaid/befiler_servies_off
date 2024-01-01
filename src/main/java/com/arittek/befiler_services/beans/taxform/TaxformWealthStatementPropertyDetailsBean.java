package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformWealthStatementPropertyDetailsBean implements Serializable {
    private Integer id;
    private String propertyType;
    private String unitNo;
    private String areaLocalityRoad;
    private String city;
    private String area;
    private String propertyCost;
}
