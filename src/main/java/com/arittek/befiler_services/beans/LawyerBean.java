package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.beans.taxform.TaxformAssignBean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class LawyerBean implements Serializable {

    private Integer userId;
    private Integer userDetailId;

    private String firstName;
    private String lastName;

    private String mobileNo;
    private String address;

    private List<TaxformAssignBean> taxformAssignBeanList;
}
