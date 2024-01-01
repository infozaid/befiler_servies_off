package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import com.arittek.befiler_services.services.payment.settings.SettingPaymentServices;
import com.arittek.befiler_services.repository.payment.PaymentCartRepository;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentCartServicesImpl implements PaymentCartServices {

    private TaxformServices taxformServices;
    private PaymentCartRepository paymentCartRepository;
    private SettingPaymentServices settingPaymentServices;

    @Autowired
    public PaymentCartServicesImpl(TaxformServices taxformServices, PaymentCartRepository paymentCartRepository, SettingPaymentServices settingPaymentServices) {
        this.taxformServices = taxformServices;
        this.paymentCartRepository = paymentCartRepository;
        this.settingPaymentServices = settingPaymentServices;
    }

    @Override
    public PaymentCart saveOrUpdateToDeletedStatus(PaymentCart paymentCart) throws Exception {
        if (paymentCart != null) {
            paymentCart.setStatus(0);

            return paymentCartRepository.save(paymentCart);
        }
        return null;
    }

    @Override
    public PaymentCart saveOrUpdateToActiveStatus(PaymentCart paymentCart) throws Exception {
        if (paymentCart != null) {
            paymentCart.setStatus(1);
            paymentCart.setPaymentCustomerInfo(null);

            return paymentCartRepository.save(paymentCart);
        }
        return null;
    }

    @Override
    public PaymentCart saveOrUpdateToPaymentStatus(PaymentCart paymentCart) throws Exception {
        if (paymentCart != null) {
            paymentCart.setStatus(3);

            return paymentCartRepository.save(paymentCart);
        }
        return null;
    }

    @Override
    public List<PaymentCart> saveOrUpdateToActiveStatus(List<PaymentCart> paymentCartList) throws Exception {
        List<PaymentCart> paymentCartList1 = null;
        if (paymentCartList != null && paymentCartList.size() > 0) {
            paymentCartList1 = new ArrayList<>();
            for (PaymentCart paymentCart : paymentCartList) {
                paymentCart.setStatus(1);
                paymentCart.setPaymentCustomerInfo(null);

                paymentCartList1.add(paymentCartRepository.save(paymentCart));
            }
        }
        return paymentCartList1;
    }

    @Override
    public List<PaymentCart> saveOrUpdateToPaymentStatus(List<PaymentCart> paymentCartList) throws Exception {
        List<PaymentCart> paymentCartList1 = null;
        if (paymentCartList != null && paymentCartList.size() > 0) {
            paymentCartList1 = new ArrayList<>();
            for (PaymentCart paymentCart : paymentCartList) {
                paymentCart.setStatus(3);

                paymentCartList1.add(paymentCartRepository.save(paymentCart));
            }
        }
        return paymentCartList1;
    }

    @Override
    public PaymentCart findOneByIdAndActiveStatus(Integer paymentCartId) throws Exception {
        if (paymentCartId != null) {
            return paymentCartRepository.findOneByIdAndStatus(paymentCartId, 1);
        }
        return null;
    }

    @Override
    public PaymentCart findOneByTaxformAndUser(Taxform taxform, User user) throws Exception {
        if (taxform != null && user != null) {
            return paymentCartRepository.findOneByTaxformAndUser(taxform, user);
        }
        return null;
    }

    @Override
    public PaymentCart findOneByFbrUserAccountInfoAndUser(FbrUserAccountInfo fbrUserAccountInfo, User user) throws Exception {
        if (fbrUserAccountInfo != null && user != null) {
            return paymentCartRepository.findOneByFbrUserAccountInfoAndUser(fbrUserAccountInfo, user);
        }
        return null;
    }

    @Override
    public List<PaymentCart> saveOrUpdateToPaymentCustomerInfoRequest(List<PaymentCart> paymentCartList, PaymentCustomerInfo paymentCustomerInfo) throws Exception {
        List<PaymentCart> paymentCartList1 = null;
        if (paymentCartList != null && paymentCartList.size() > 0 && paymentCustomerInfo != null) {
            paymentCartList1 = new ArrayList<>();
            for (PaymentCart paymentCart : paymentCartList) {
                paymentCart.setStatus(1);
                paymentCart.setPaymentCustomerInfo(paymentCustomerInfo);

                paymentCartList1.add(paymentCartRepository.save(paymentCart));
            }
        }
        return paymentCartList1;
    }

    @Override
    public List<PaymentCart> findAllActivePaymentCartsByUser(User user) throws Exception {
        if (user != null) {
            return paymentCartRepository.findAllByUserAndStatus(user, 1);
        }
        return null;
    }

    @Override
    public List<PaymentCart> findAllByPaymentCustomerInfoRequestAndActiveStatus(PaymentCustomerInfo paymentCustomerInfo) throws Exception {
        if (paymentCustomerInfo != null) {
            return paymentCartRepository.findAllByPaymentCustomerInfoAndStatus(paymentCustomerInfo, 1);
        }
        return null;
    }

    @Override
    public List<PaymentCart> checkForPaymentCartByUser(User user) throws Exception {
        if (user != null) {
            List<Taxform> taxformList = taxformServices.findAllByUserAndActiveTaxformYears(user);
            if (taxformList != null && taxformList.size() > 0) {
                for (Taxform taxform : taxformList) {
                    if (taxform != null && taxform.getStatus() != null && taxform.getStatus().getId() > 0) {
                        if (taxform.getPaymentList() == null || taxform.getPaymentList().size() <= 0) {
                            /*SettingTaxformPayment settingTaxformPayment = settingTaxformPaymentServices.findOneByTaxformYearAndStatus(taxform.getTaxformYear(), AppStatus.ACTIVE);*/
                            SettingPayment settingPayment = settingPaymentServices.findPaymentByYearForForTaxform(taxform.getTaxformYear());
                            PaymentCart paymentCartForTaxform = this.findOneByTaxformAndUser(taxform, user);
                            if (paymentCartForTaxform == null) {
                                paymentCartForTaxform = new PaymentCart();
                                paymentCartForTaxform.setStatus(1);
                                paymentCartForTaxform.setProductType(ProductType.TAXFORM);
                                paymentCartForTaxform.setUser(user);
                                paymentCartForTaxform.setTaxform(taxform);
                                paymentCartForTaxform.setSettingPayment(settingPayment);

                                this.saveOrUpdateToActiveStatus(paymentCartForTaxform);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
