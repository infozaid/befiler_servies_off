package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.payment.PaymentRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServicesImpl implements PaymentServices {

    private PaymentRepository paymentRepository;

    @Autowired
    PaymentServicesImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    @Override
    public Payment save(Payment payment) throws Exception {
        if (payment != null) {
            return paymentRepository.save(payment);
        }
        return null;
    }

    @Override
    public Payment findOneById(Integer id) throws Exception {
        if (id != null)
            return paymentRepository.findById(id).get();
        return null;
    }

    @Override
    public Payment checkForTaxformPayment(Taxform taxform) throws Exception {
        if (taxform != null && taxform.getPaymentList() != null) {
            if (taxform.getPaymentList().size() == 1) {
                return taxform.getPaymentList().get(0);
            } else {
                //TODO send email for system bug
            }
        }
        return null;
    }

    @Override
    public Payment checkForFbrUserAccountInfoPayment(FbrUserAccountInfo fbrUserAccountInfo) throws Exception {
        if (fbrUserAccountInfo != null && fbrUserAccountInfo.getPaymentList() != null && fbrUserAccountInfo.getPaymentList().size() > 0) {
            if (fbrUserAccountInfo.getPaymentList().size() == 1) {
                return fbrUserAccountInfo.getPaymentList().get(0);
            } else {
                //TODO send email for system bug
            }
        }
        return null;
    }

    @Override
    public List<Payment> findAllPayments() throws Exception {
        return (List<Payment>) paymentRepository.findAll();
    }

    @Override
    public List<Payment> findAllPaymentsDesc() throws Exception {
        return paymentRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<Payment> findAllByUser(User user) throws Exception {
        if (user != null) {
            return  paymentRepository.findAllByUser(user);
        }
        return null;
    }

    /*@Override*/
    /*public List<Payment> findAllByUserRoleAndStatus(User user) throws Exception {
        if (user != null && user.getUserType() != null) {
            UserType userType = user.getUserType();

            if (userType.getId() == 1 || userType.getId() == 2) {
                return paymentRepository.findAllByUser(user);
            } else if (userType.getId() == 3 || userType.getId() == 5) {

                *//*List<Taxform_Status> taxformStatusList = new ArrayList<>();
                taxformStatusList.add(taxformStatusRepository.findOne(5));
                taxformStatusList.add(taxformStatusRepository.findOne(6));
                taxformStatusList.add(taxformStatusRepository.findOne(7));

                List<Taxform> taxformList = findAllByStatusIn(taxformStatusList);*//*

                //TODO uncomment this code afte authoassign -- done
                List<Assign> taxformAssignList = taxformAssignServices.findAllByUserAndAppStatus(user, appStatusServices.findOneByActiveStatus());
                List<Taxform> taxformList = new ArrayList<>();
                if (taxformAssignList != null) {
                    for (Assign taxformAssign : taxformAssignList) {
                        if (taxformAssign != null && taxformAssign.getTaxform() != null) {
                            taxformList.add(taxformAssign.getTaxform());
                        }
                    }
                }
                return taxformList;
            } else if (userType.getId() == 4 || userType.getId() == 6) {
                return (List<Taxform>) taxformRepository.findAll();
            }
        }
        return null;
    }*/
}
