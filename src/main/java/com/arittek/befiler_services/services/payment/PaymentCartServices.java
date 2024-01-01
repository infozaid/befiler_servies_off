package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface PaymentCartServices {

    PaymentCart saveOrUpdateToDeletedStatus(PaymentCart paymentCart) throws Exception;
    PaymentCart saveOrUpdateToActiveStatus(PaymentCart paymentCart) throws Exception;
    PaymentCart saveOrUpdateToPaymentStatus(PaymentCart paymentCart) throws Exception;

    PaymentCart findOneByIdAndActiveStatus(Integer paymentCartId) throws Exception;
    PaymentCart findOneByTaxformAndUser(Taxform taxform, User user) throws Exception;
    PaymentCart findOneByFbrUserAccountInfoAndUser(FbrUserAccountInfo fbrUserAccountInfo, User user) throws Exception;

    List<PaymentCart> saveOrUpdateToActiveStatus(List<PaymentCart> paymentCartList) throws Exception;
    List<PaymentCart> saveOrUpdateToPaymentStatus(List<PaymentCart> paymentCartList) throws Exception;

    List<PaymentCart> saveOrUpdateToPaymentCustomerInfoRequest(List<PaymentCart> paymentCartList, PaymentCustomerInfo paymentCustomerInfo) throws Exception;

    List<PaymentCart> findAllActivePaymentCartsByUser(User user) throws Exception;
    List<PaymentCart> findAllByPaymentCustomerInfoRequestAndActiveStatus(PaymentCustomerInfo paymentCustomerInfo) throws Exception;

    List<PaymentCart> checkForPaymentCartByUser(User user) throws Exception;
}
