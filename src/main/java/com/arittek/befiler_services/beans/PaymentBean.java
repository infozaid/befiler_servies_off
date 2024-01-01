package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentBean implements Serializable {

    private Integer id;

    private String paymentMethod;

    private String productType;

    private Integer taxformId;
    private Integer taxformYear;


    private Integer fbrId;

    private Double amount;

    private Integer userId;
    private String userName;
    private String userCnicNo;

    private Integer taxformYearId;
    private Integer status;
    private String currentDate;
    private String description;
    private String detail;


    private String transactionId;
    private String orderId;
    private String approveId;

    private String fileStatus;

    private String promoCode;

    private String paymentDate;

    private Integer lawyerId;
    private String lawyerName;

    private List<PaymentBean> ids;
}
