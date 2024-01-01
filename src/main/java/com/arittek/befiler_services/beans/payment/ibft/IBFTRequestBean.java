package com.arittek.befiler_services.beans.payment.ibft;

import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequestDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class IBFTRequestBean {

    private Integer id;

    private String customerName;
    private String mobileNo;
    private String emailAddress;
    private String residentialAddress;
    private String billingAddress;

    private Integer userId;
    private String userFullName;

    List<PaymentCartBean> paymentCartBeanList;
    List<IBFTRequestDocumentBean> ibftRequestDocumentBeanList;
}

