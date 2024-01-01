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
public class TaxformAssignBean implements Serializable {

    private Integer id;

    private Integer userId;
    private String userName;
    private String userEmail;

    private Integer taxformId;
    private String taxformYear;

    private Integer authorizerId;
    private String authorizerName;
    private String authorizerEmail;

    private String currentDate;

}

