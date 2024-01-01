package com.arittek.befiler_services.controller.setting.taxform;

import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.beans.taxform.settings.ApprovedDoneeBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDoneeBridge;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.services.TaxformYearsServices;
import com.arittek.befiler_services.services.setting.taxform.ApprovedDoneeServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/taxform/approvedDonee")
public class ApprovedDoneeController {

    private TaxformYearsServices taxformYearsServices;
    private ApprovedDoneeServices approvedDoneeServices;

    @Autowired
    public ApprovedDoneeController(TaxformYearsServices taxformYearsServices, ApprovedDoneeServices approvedDoneeServices) {
        this.taxformYearsServices = taxformYearsServices;
        this.approvedDoneeServices = approvedDoneeServices;
    }

    /*@RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllApprovedDonees() throws Exception {
        try {
            List<ApprovedDonee> approvedDoneeList = approvedDoneeServices.findAllByActiveStatus();
            List<ApprovedDoneeBean> approvedDoneeBeanList = new ArrayList<>();
            if (approvedDoneeList.size() > 0) {
                for (ApprovedDonee approvedDonee : approvedDoneeList) {

                    ApprovedDoneeBean approvedDoneeBean = new ApprovedDoneeBean();
                    approvedDoneeBean.setDoneeId(approvedDonee.getId());
                    approvedDoneeBean.setDoneeName(approvedDonee.getDoneeName());
                    approvedDoneeBeanList.add(approvedDoneeBean);
                }
                StatusBean statusBean = new StatusBean(1, "successfully");
                statusBean.setResponse(approvedDoneeBeanList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
    }*/

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getAllApprovedDoneesByTaxformYear(@RequestParam(value="yearId", required = true) Integer yearId) throws Exception {
        try {

            TaxformYears taxformYear = taxformYearsServices.findOneByIdAndActiveStatus(yearId);
            if (taxformYear == null) {
                return new ResponseEntity<>(new StatusBean(0, "Year not found"), HttpStatus.OK);
            }

            List<ApprovedDoneeBridge> approvedDoneeBridgeList = approvedDoneeServices.findAllByActiveRecordsByTaxformYear(taxformYear);
            List<ApprovedDoneeBean> approvedDoneeBeanList = new ArrayList<>();
            if (approvedDoneeBridgeList.size() > 0) {
                for (ApprovedDoneeBridge approvedDoneeBridge : approvedDoneeBridgeList) {
                    if (approvedDoneeBridge != null && approvedDoneeBridge.getApprovedDonee() != null) {
                        ApprovedDonee approvedDonee = approvedDoneeBridge.getApprovedDonee();

                        ApprovedDoneeBean approvedDoneeBean = new ApprovedDoneeBean();
                        approvedDoneeBean.setDoneeId(approvedDonee.getId());
                        approvedDoneeBean.setDoneeName(approvedDonee.getDoneeName());
                        approvedDoneeBeanList.add(approvedDoneeBean);
                    }
                }
                StatusBean statusBean = new StatusBean(1, "successfully");
                statusBean.setResponse(approvedDoneeBeanList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "No record found"), HttpStatus.OK);
    }
}
