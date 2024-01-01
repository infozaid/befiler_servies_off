package com.arittek.befiler_services.beans.payment.ipg;

import com.arittek.befiler_services.beans.PaymentCartBean;
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
public class IPGRequestBean implements Serializable {

    private Integer registrationId;

    private Integer userId;
    private Integer taxformId;

    private String transactionId;


    private String language;
    private String version;

    private String currency;
    private String amount;

    private String orderId;
    private String orderName;
    private String orderInfo;

    private String returnPath;

    private String customer;
    private String cnicNo;
    private String mobileNo;
    private String emailAddress;
    private String dateOfBirth;
    private String residentialAddress;
    private String billingAddress;
    private String shippingAddress;

    private List<PaymentCartBean> paymentCartBeanList;
}
