package com.arittek.befiler_services.services;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.repository.TaxformYearsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxformYearsServicesImpl implements TaxformYearsServices {

    private TaxformYearsRepository taxformYearsRepository;

    @Autowired
    public TaxformYearsServicesImpl(TaxformYearsRepository taxformYearsRepository) {
        this.taxformYearsRepository = taxformYearsRepository;
    }

    @Override
    public TaxformYears findOneByIdAndActiveStatus(Integer yearId) throws Exception {
        if (yearId != null) {
            return taxformYearsRepository.findOneByIdAndStatus(yearId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public TaxformYears findOneByIdAndStatus(Integer yearId, AppStatus status) throws Exception {
        if (yearId != null && status != null) {
            return taxformYearsRepository.findOneByIdAndStatus(yearId, status);
        }
        return null;
    }

    @Override
    public TaxformYears findOneByIdAndStatusNotIn(Integer yearId, AppStatus status) throws Exception {
        if (yearId != null && status != null) {
            return taxformYearsRepository.findOneByIdAndStatusNotIn(yearId, status);
        }
        return null;
    }

    @Override
    public TaxformYears findOnePreviousYear(TaxformYears taxformYears) throws Exception {
        if (taxformYears != null) {
            Integer previousYearInteger = Integer.parseInt("2018")-1 ;
            if (previousYearInteger != null) {
                return taxformYearsRepository.findOneByYear(previousYearInteger);
            }
        }
        return null;
    }

    @Override
    public TaxformYears findOneByYearAndStatusNotIn(Integer year, AppStatus status) throws Exception {
        if (year != null && status != null) {
            return taxformYearsRepository.findOneByYearAndStatusNotIn(year, status);
        }
        return null;
    }

    @Override
    public TaxformYears save(TaxformYears taxformYears) throws Exception {
        if(taxformYears != null){
            return taxformYearsRepository.save(taxformYears);
        }
        return null;
    }

    @Override
    public TaxformYears update(TaxformYears taxformYears) throws Exception {
        if (taxformYears != null && taxformYears.getId() != null) {
            return taxformYearsRepository.save(taxformYears);
        }
        return null;
    }


    @Override
    public List<TaxformYears> findAllActiveYears() throws Exception {
        return taxformYearsRepository.findAllByStatusOrderByYearDesc(AppStatus.ACTIVE);
    }

    @Override
    public List<TaxformYears> findAllByStatus(AppStatus appStatus) throws Exception {
        if (appStatus != null) {
            return taxformYearsRepository.findAllByStatus(appStatus);
        }
        return null;
    }

    @Override
    public List<TaxformYears> findAllByStatusNotIn(AppStatus appStatus) throws Exception {
        if (appStatus != null) {
            return taxformYearsRepository.findAllByStatusNotIn(appStatus);
        }
        return null;
    }
}
