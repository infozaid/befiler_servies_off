package com.arittek.befiler_services.services.setting.taxform;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDoneeBridge;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.repository.setting.taxform.ApprovedDoneeBridgeRepository;
import com.arittek.befiler_services.repository.setting.taxform.ApprovedDoneeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovedDoneeServicesImpl implements ApprovedDoneeServices {

    private ApprovedDoneeRepository approvedDoneeRepository;
    private ApprovedDoneeBridgeRepository approvedDoneeBridgeRepository;

    @Autowired
    public ApprovedDoneeServicesImpl(ApprovedDoneeRepository approvedDoneeRepository, ApprovedDoneeBridgeRepository approvedDoneeBridgeRepository) {
        this.approvedDoneeRepository = approvedDoneeRepository;
        this.approvedDoneeBridgeRepository = approvedDoneeBridgeRepository;
    }

    @Override
    public ApprovedDonee findOneByIdAndActiveStatus(Integer approvedDoneeId) throws Exception {
        if (approvedDoneeId != null) {
            return approvedDoneeRepository.findOneByIdAndStatus(approvedDoneeId, AppStatus.ACTIVE);
        }
        return null;
    }

    @Override
    public List<ApprovedDonee> findAllByActiveStatus() throws Exception {
        return approvedDoneeRepository.findAllByStatus(AppStatus.ACTIVE);
    }

    @Override
    public List<ApprovedDoneeBridge> findAllByActiveRecordsByTaxformYear(TaxformYears taxformYear) throws Exception {
        if (taxformYear != null) {
            return approvedDoneeBridgeRepository.findAllByTaxformYearAndStatus(taxformYear, AppStatus.ACTIVE);
        }
        return null;
    }
}
