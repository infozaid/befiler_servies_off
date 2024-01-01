package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.model.enums.AppStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CorporateEmployeeBean implements Serializable {

    private Integer id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String cnicNo;
    private String designation;
    private String department;
    private AppStatus status;
    private Integer corporateId;
    private Integer userId;
    private Timestamp currentDate;
    private Boolean paymentByCorporate;
    private Double totalAmount;
}
