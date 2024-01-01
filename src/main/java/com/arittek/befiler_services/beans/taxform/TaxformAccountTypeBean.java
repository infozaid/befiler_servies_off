package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

public class TaxformAccountTypeBean implements Serializable {

    private Integer id;
    private Integer authorizerId;
    private Integer status;
    private String accountType;
    private String description;
    private Timestamp currentDate;
}
