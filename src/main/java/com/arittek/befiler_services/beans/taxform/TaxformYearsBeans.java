package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformYearsBeans implements Serializable {

    private Integer yearId;
    private Integer year;
    private String status;
    private String description;
    private Integer taxformId;
    private String taxformStatus;
    private Integer taxformStatusMobile;
    private String taxformStatusDescription;
    private Integer authorizerId;
    private Timestamp currentDate;
    private Boolean paymentCheck;
    private Boolean corporateCheck;
}
