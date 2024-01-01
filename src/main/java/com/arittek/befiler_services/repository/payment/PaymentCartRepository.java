package com.arittek.befiler_services.repository.payment;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCartRepository extends JpaRepository<PaymentCart, Integer>, RevisionRepository<PaymentCart, Integer, Integer> {

    PaymentCart findOneByIdAndStatus(Integer paymentCartId, Integer status);
    PaymentCart findOneByTaxformAndUser(Taxform taxform, User user);
    PaymentCart findOneByFbrUserAccountInfoAndUser(FbrUserAccountInfo fbrUserAccountInfo, User user);

    List<PaymentCart> findAllByUserAndStatus(User user, Integer status);
    List<PaymentCart> findAllByPaymentCustomerInfoAndStatus(PaymentCustomerInfo paymentCustomerInfo, Integer status);
}
