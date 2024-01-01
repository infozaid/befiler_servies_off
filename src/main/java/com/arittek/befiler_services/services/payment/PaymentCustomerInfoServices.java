package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.user.User;

public interface PaymentCustomerInfoServices {

    PaymentCustomerInfo findOneById(Integer paymentCustomerInfoId)throws Exception;
    PaymentCustomerInfo findOneByOrderId(String orderId)throws Exception;

    PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, IBFTRequest ibftRequest, PromoCode promoCode) throws Exception;
    PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, PaymentCustomerInfoBean paymentCustomerInfoBean) throws Exception;
    PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, PaymentCustomerInfoBean paymentCustomerInfoBean, PromoCode promoCode) throws Exception;
    PaymentCustomerInfo update(PaymentCustomerInfo paymentCustomerInfo) throws Exception;
}
