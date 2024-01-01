package com.arittek.befiler_services.beans;


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
public class SalarySlabBean implements Serializable {

    private Integer id;
    private Integer serialNo;
    private Integer fixed;
    private String rate;
    private Integer lowerLimit;
    private Integer upperLimit;

    private Integer authorizerId;

}
