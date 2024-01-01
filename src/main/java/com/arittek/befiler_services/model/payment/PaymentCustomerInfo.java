package com.arittek.befiler_services.model.payment;

import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.enums.PaymentMethodConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.payment.apps.AppsGetAccessTokenRequest;
import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.payment.ipg.IpgRegistrationRequest;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaIndex;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaMARequest;
import com.arittek.befiler_services.model.payment.easypaisa.EasypaisaOTCRequest;
import com.arittek.befiler_services.model.payment.keenu.KeenuRequest;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "payment_customer_info")
public class PaymentCustomerInfo extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_number")
    private String mobileNo;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "residential_address")
    private String residentialAddress;

    @Column(name = "billing_address")
    private String billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    private String orderId;

    /*One To One*/

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private IpgRegistrationRequest ipgRegistrationRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private KeenuRequest keenuRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private AppsGetAccessTokenRequest appsGetAccessTokenRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private EasypaisaIndex easypaisaIndex;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private EasypaisaOTCRequest easypaisaOTCRequest;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "paymentCustomerInfo")
    private EasypaisaMARequest easypaisaMARequest;
}
