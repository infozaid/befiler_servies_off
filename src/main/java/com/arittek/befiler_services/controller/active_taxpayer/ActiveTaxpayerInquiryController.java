package com.arittek.befiler_services.controller.active_taxpayer;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.active_taxpayer.ActiveTaxpayerInquiryBean;
import com.arittek.befiler_services.model.active_taxpayer.ActiveTaxpayerInquiry;
import com.arittek.befiler_services.services.active_taxpayer.ActiveTaxpayerInquiryService;
import com.arittek.befiler_services.services.active_taxpayer.ActiveTaxpayerInquiryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/income/taxpayer")
public class ActiveTaxpayerInquiryController {

    private ActiveTaxpayerInquiryService activeTaxpayerInquiryService;
    private ActiveTaxpayerInquiryServiceImpl activeEnquiryTaxpayerIncomeServiceImpl;

    @Autowired
    ActiveTaxpayerInquiryController(ActiveTaxpayerInquiryServiceImpl activeEnquiryTaxpayerIncomeServiceImpl, ActiveTaxpayerInquiryService activeTaxpayerInquiryService) {
        this.activeEnquiryTaxpayerIncomeServiceImpl = activeEnquiryTaxpayerIncomeServiceImpl;
        this.activeTaxpayerInquiryService = activeTaxpayerInquiryService;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    ResponseEntity<StatusBean> getCaptchaFromFbr() throws Exception {
        Map<String, Object> captchaMap = activeEnquiryTaxpayerIncomeServiceImpl.getCaptcha();
        if (captchaMap.isEmpty() == true) {
            return new ResponseEntity<>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
        StatusBean statusBean = new StatusBean(1, "Successful");
        statusBean.setResponseMap(captchaMap);
        return new ResponseEntity<>(statusBean, HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public
    @ResponseBody
    ResponseEntity<StatusBean> getATLFromFbr(@RequestBody Map<String, Object> inputMap) throws Exception {

        if (inputMap.isEmpty() == true) {
            return new ResponseEntity<>(new StatusBean(0, "Please Provide registrationNo, printCaptcha & webCookie"), HttpStatus.OK);
        }

        if (inputMap.get("registrationNo") == null || inputMap.get("printCaptcha") == null || inputMap.get("webCookie") == null || inputMap.get("webCookie").equals("")) {
            return new ResponseEntity<>(new StatusBean(0, "Please Provide registrationNo, printCaptcha & webCookie"), HttpStatus.OK);
        }
        List<ActiveTaxpayerInquiryBean> activeTaxpayerInquiryBeanList = new ArrayList<>();

        ActiveTaxpayerInquiryBean activeTaxpayerInquiryBean = activeEnquiryTaxpayerIncomeServiceImpl.getATL(inputMap);
        if (activeTaxpayerInquiryBean == null) {
            return new ResponseEntity<>(new StatusBean(0, "Need to refresh printCaptcha & webCookie"), HttpStatus.OK);
        } else if (activeTaxpayerInquiryBean.getRegistrationNo() != null && activeTaxpayerInquiryBean.getName() != null && activeTaxpayerInquiryBean.getFilingStatus() != null) {
            ActiveTaxpayerInquiry activeEnquiryTaxpayerIncome = new ActiveTaxpayerInquiry();
            activeEnquiryTaxpayerIncome.setRegistrationStatus(1);
            activeEnquiryTaxpayerIncome.setRegistrationNo(activeTaxpayerInquiryBean.getRegistrationNo());
            activeEnquiryTaxpayerIncome.setName(activeTaxpayerInquiryBean.getName());
            activeEnquiryTaxpayerIncome.setBusinessName(activeTaxpayerInquiryBean.getBusinessName());
            activeEnquiryTaxpayerIncome.setFilingStatus(activeTaxpayerInquiryBean.getFilingStatus());
            activeTaxpayerInquiryService.create(activeEnquiryTaxpayerIncome);
        } else if (activeTaxpayerInquiryBean.getNoRecord() != null) {
            ActiveTaxpayerInquiry activeEnquiryTaxpayerIncome = new ActiveTaxpayerInquiry();
            activeEnquiryTaxpayerIncome.setRegistrationStatus(0);
            activeEnquiryTaxpayerIncome.setRegistrationNo(inputMap.get("registrationNo").toString());
            activeTaxpayerInquiryService.create(activeEnquiryTaxpayerIncome);
        } else {
            return new ResponseEntity<>(new StatusBean(0, "Need to refresh printCaptcha & webCookie"), HttpStatus.OK);
        }
        activeTaxpayerInquiryBeanList.add(activeTaxpayerInquiryBean);
        StatusBean statusBean = new StatusBean(1, "Successful");
        statusBean.setResponse(activeTaxpayerInquiryBeanList);
        return new ResponseEntity<>(statusBean, HttpStatus.OK);
    }

}
