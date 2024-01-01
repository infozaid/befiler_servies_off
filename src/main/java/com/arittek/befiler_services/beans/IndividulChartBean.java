package com.arittek.befiler_services.beans;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class IndividulChartBean implements Serializable {
    private Double totalAmount;
    private Integer totalTaxform;
    private Integer taxformComplete;
    private Integer taxformIncomplete;
    private Integer ntn;
    private Integer signUp;
}
