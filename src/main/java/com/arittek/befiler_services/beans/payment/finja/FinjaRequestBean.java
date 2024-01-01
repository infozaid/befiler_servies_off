package com.arittek.befiler_services.beans.payment.finja;

import com.arittek.befiler_services.beans.PaymentCartBean;
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
public class FinjaRequestBean {

    private Integer registrationId;
    private Integer userId;

    private String otp;

    private String customer;
    private String mobileNo;
    private String emailAddress;
    private String residentialAddress;
    private String billingAddress;
    private String shippingAddress;

    private List<PaymentCartBean> paymentCartBeanList;
}
