package com.arittek.befiler_services.services.setting.taxform;

import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDoneeBridge;
import com.arittek.befiler_services.model.taxform.TaxformYears;

import java.util.List;

public interface ApprovedDoneeServices {

    ApprovedDonee findOneByIdAndActiveStatus(Integer approvedDoneeId) throws Exception;

    List<ApprovedDonee> findAllByActiveStatus() throws Exception;

    List<ApprovedDoneeBridge> findAllByActiveRecordsByTaxformYear(TaxformYears taxformYear) throws Exception;

}
