package com.arittek.befiler_services.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AssignBean {
    private Integer id;

    private Integer userId;
    private String userEmail;
    private String userName;
}
