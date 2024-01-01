package com.arittek.befiler_services.beans.payment;

import com.arittek.befiler_services.beans.DocumentsBean;
import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.payment.ibft.IBFTRequestDocumentBean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PaymentCustomerInfoBean {

    private Integer userId;

    /*Customer Info*/
    private String customer;
    private String mobileNo;
    private String emailAddress;
    private String residentialAddress;
    private String billingAddress;

    /*IPG Parameters*/
    private String returnPath;

    /*PromoCode Parameters*/
    private String promoCode;
    private Integer ibftRequestId;

    /*Payment Cart List*/
    private List<PaymentCartBean> paymentCartBeanList;

    /*IBFT Request*/
    private List<IBFTRequestDocumentBean> documentsBeanList;


}
