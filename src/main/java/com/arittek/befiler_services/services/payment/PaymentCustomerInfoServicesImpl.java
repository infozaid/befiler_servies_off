package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.model.enums.PaymentMethod;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.payment.PaymentCustomerInfoRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentCustomerInfoServicesImpl implements PaymentCustomerInfoServices {

    private PaymentCustomerInfoRepository paymentCustomerInfoRepository;

    @Autowired
    PaymentCustomerInfoServicesImpl(PaymentCustomerInfoRepository paymentCustomerInfoRepository) {
        this.paymentCustomerInfoRepository = paymentCustomerInfoRepository;
    }

    @Override
    public PaymentCustomerInfo findOneById(Integer paymentCustomerInfoId) throws Exception {
        if (paymentCustomerInfoId != null) {
            return paymentCustomerInfoRepository.findById(paymentCustomerInfoId).orElse(null);
        }
        return null;
    }

    @Override
    public PaymentCustomerInfo findOneByOrderId(String orderId) throws Exception {
        if (StringUtils.isNotEmpty(orderId)) {
            return paymentCustomerInfoRepository.findOneByOrderId(orderId);
        }
        return null;
    }

    @Override
    public PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, IBFTRequest ibftRequest, PromoCode promoCode) throws Exception {
        if (user != null && paymentMethod != null && ibftRequest != null && promoCode != null) {
            PaymentCustomerInfo paymentCustomerInfo = new PaymentCustomerInfo();
            paymentCustomerInfo.setUser(user);

            paymentCustomerInfo.setCustomerName(ibftRequest.getCustomerName());
            paymentCustomerInfo.setMobileNo(ibftRequest.getMobileNo());
            paymentCustomerInfo.setEmailAddress(ibftRequest.getEmailAddress());
            paymentCustomerInfo.setResidentialAddress(ibftRequest.getResidentialAddress());
            paymentCustomerInfo.setBillingAddress(ibftRequest.getBillingAddress());

            paymentCustomerInfo.setPaymentMethod(paymentMethod);

            paymentCustomerInfo.setPromoCode(promoCode);

            return paymentCustomerInfoRepository.save(paymentCustomerInfo);
        }
        return null;
    }

    @Override
    public PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, PaymentCustomerInfoBean paymentCustomerInfoBean) throws Exception {
        if (paymentMethod != null && paymentCustomerInfoBean != null) {
            PaymentCustomerInfo paymentCustomerInfo = new PaymentCustomerInfo();
            paymentCustomerInfo.setUser(user);

            paymentCustomerInfo.setCustomerName(paymentCustomerInfoBean.getCustomer());
            paymentCustomerInfo.setMobileNo(paymentCustomerInfoBean.getMobileNo());
            paymentCustomerInfo.setEmailAddress(paymentCustomerInfoBean.getEmailAddress());
            paymentCustomerInfo.setResidentialAddress(paymentCustomerInfoBean.getResidentialAddress());
            paymentCustomerInfo.setBillingAddress(paymentCustomerInfoBean.getBillingAddress());

            paymentCustomerInfo.setPaymentMethod(paymentMethod);

            return paymentCustomerInfoRepository.save(paymentCustomerInfo);
        }
        return null;
    }

    @Override
    public PaymentCustomerInfo save(User user, PaymentMethod paymentMethod, PaymentCustomerInfoBean paymentCustomerInfoBean, PromoCode promoCode) throws Exception {
        if (paymentMethod != null && paymentCustomerInfoBean != null && promoCode != null) {
            PaymentCustomerInfo paymentCustomerInfo = new PaymentCustomerInfo();
            paymentCustomerInfo.setUser(user);

            paymentCustomerInfo.setCustomerName(paymentCustomerInfoBean.getCustomer());
            paymentCustomerInfo.setMobileNo(paymentCustomerInfoBean.getMobileNo());
            paymentCustomerInfo.setEmailAddress(paymentCustomerInfoBean.getEmailAddress());
            paymentCustomerInfo.setResidentialAddress(paymentCustomerInfoBean.getResidentialAddress());
            paymentCustomerInfo.setBillingAddress(paymentCustomerInfoBean.getBillingAddress());

            paymentCustomerInfo.setPaymentMethod(paymentMethod);

            paymentCustomerInfo.setPromoCode(promoCode);

            return paymentCustomerInfoRepository.save(paymentCustomerInfo);
        }
        return null;
    }

    @Override
    public PaymentCustomerInfo update(PaymentCustomerInfo paymentCustomerInfo) throws Exception {
        if (paymentCustomerInfo != null && paymentCustomerInfo.getId() != null) {
            return paymentCustomerInfoRepository.save(paymentCustomerInfo);
        }
        return null;
    }
}
