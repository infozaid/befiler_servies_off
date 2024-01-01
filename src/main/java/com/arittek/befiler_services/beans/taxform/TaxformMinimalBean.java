package com.arittek.befiler_services.beans.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TaxformMinimalBean implements Serializable {

    private Long serialNo;
    private Integer code;
    private String message;

    private Integer taxformId;

    private Integer yearId;
    private Integer year;
    /*private  String taxYear;*/



    private String cnic;
    private String nameAsPerCnic;

    private String email;
    private String mobileNo;

    private String status;

    private long dateDifference;
    private Integer currentScreen;

    private Double amount;
    private String orderId;
    private String transactionId;
    private String paymentDate;

    private Map responseMap = new HashMap<>();
    private List<TaxformDocumentsBean> taxformDocumentsBeanList = new ArrayList<>();

    public TaxformMinimalBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public TaxformMinimalBean(Integer code, Integer taxformId, String message) {
        this.code = code;
        this.taxformId = taxformId;
        this.message = message;/*
        this.amount = amount;
        this.transactionId = transactionId;*/
    }
}
