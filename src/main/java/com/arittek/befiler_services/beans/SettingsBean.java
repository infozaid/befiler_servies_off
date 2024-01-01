package com.arittek.befiler_services.beans;


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
public class SettingsBean implements Serializable {
    private Integer id;
    private Integer authorizerId;
    private Integer appStatusId;
    private Timestamp currentDate;
    private Integer daysToSentMarketing;
    private Integer maxLoginAttemptsCount;
}
