package com.arittek.befiler_services.services.payment.finja;

import com.arittek.befiler_services.model.payment.finja.FinjaGetCustomerInfoRequest;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.payment.finja.FinjaGetCustomerInfoRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinjaGetCustomerInfoRequestServicesImpl implements FinjaGetCustomerInfoRequestServices{

    private FinjaGetCustomerInfoRequestRepository finjaGetCustomerInfoRequestRepository;

    @Autowired
    public FinjaGetCustomerInfoRequestServicesImpl(FinjaGetCustomerInfoRequestRepository finjaGetCustomerInfoRequestRepository) {
        this.finjaGetCustomerInfoRequestRepository = finjaGetCustomerInfoRequestRepository;
    }

    @Override
    public FinjaGetCustomerInfoRequest findOne(Integer id) {
        if (id != null) {
            return finjaGetCustomerInfoRequestRepository.findById(id).orElse(null);
        }
        return null;
    }

    @Override
    public FinjaGetCustomerInfoRequest saveOrUpdate(FinjaGetCustomerInfoRequest finjaGetCustomerInfoRequest) throws Exception {
        if (finjaGetCustomerInfoRequest != null && finjaGetCustomerInfoRequest.getPaymentCustomerInfo() != null) {
            return finjaGetCustomerInfoRequestRepository.save(finjaGetCustomerInfoRequest);
        }
        return null;
    }

    @Override
    public List<FinjaGetCustomerInfoRequest> findAll() throws Exception {
        return (List<FinjaGetCustomerInfoRequest>) finjaGetCustomerInfoRequestRepository.findAll();
    }

    @Override
    public List<FinjaGetCustomerInfoRequest> findAllByUser(User user) throws Exception {
        if (user != null) {
            finjaGetCustomerInfoRequestRepository.findAllByCreatedBy(user.getId());
        }
        return null;
    }
}
