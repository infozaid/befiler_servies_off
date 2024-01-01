package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.SalarySlab;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.SalarySlabRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalarySlabServicesImpl implements SalarySlabServices {

    SalarySlabRepository salarySlabRepository;

    @Autowired
    public SalarySlabServicesImpl(SalarySlabRepository salarySlabRepository) {
        this.salarySlabRepository = salarySlabRepository;
    }


    @Override
    public SalarySlab findOneActiveSalarySlab(Integer salarySlabId) throws Exception {
        if (salarySlabId != null) {
            return salarySlabRepository.findOneByIdAndStatus(salarySlabId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public SalarySlab delete(SalarySlab salarySlab, User authorizer) throws Exception {
        if (salarySlab != null && authorizer != null) {
            salarySlab.setStatus(AppStatus.DELETED);
            salarySlab.setAuthorizer(authorizer);
            salarySlab.setCurrentDate(CommonUtil.getCurrentTimestamp());

            return salarySlabRepository.save(salarySlab);
        }
        return null;
    }

    @Override
    public SalarySlab saveOrUpdate(SalarySlab salarySlab) throws Exception {
        if (salarySlab != null) {
            return salarySlabRepository.save(salarySlab);
        }
        return null;
    }

    @Override
    public List<SalarySlab> findAllByTaxformYearAndStatus(TaxformYears taxformYears, AppStatus status) throws Exception {
        if (taxformYears != null && status != null) {
            return salarySlabRepository.findAllByTaxformYearAndStatus(taxformYears, status);
        }
        return null;
    }
}
