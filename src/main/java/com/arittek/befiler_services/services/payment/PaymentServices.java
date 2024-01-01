package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;

import java.util.List;

public interface PaymentServices {

    Payment save(Payment payment) throws Exception;

    Payment findOneById(Integer id) throws Exception;
    Payment checkForTaxformPayment(Taxform taxform) throws Exception;
    Payment checkForFbrUserAccountInfoPayment(FbrUserAccountInfo fbrUserAccountInfo) throws Exception;

    List<Payment> findAllPayments() throws Exception;
    List<Payment> findAllPaymentsDesc() throws Exception;
    List<Payment> findAllByUser(User user) throws Exception;
    /*List<Payment> findAllByUserRoleAndStatus(User user) throws Exception;*/

}
