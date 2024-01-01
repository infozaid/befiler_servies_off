package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.taxform.TermsAndConditionsBean;
import com.arittek.befiler_services.model.TermsAndConditions;
import com.arittek.befiler_services.services.TermsAndConditionsServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/termsAndConditions")
public class TermsAndConditionsController {

    private TermsAndConditionsServices termsAndConditionsServices;
    @Autowired
    TermsAndConditionsController( TermsAndConditionsServices termsAndConditionsServices){
        this.termsAndConditionsServices=termsAndConditionsServices;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getTermsAndContitions() {

        TermsAndConditions termsAndConditions = termsAndConditionsServices.findByTypeAndStatus(2,1);
        if (termsAndConditions != null) {
            StatusBean status = new StatusBean(1, "Successfull");
            List<TermsAndConditionsBean> termsAndConditionsBeanList = new ArrayList<TermsAndConditionsBean>();
            TermsAndConditionsBean termsAndConditionsBean = new TermsAndConditionsBean();

            termsAndConditionsBean.setId(termsAndConditions.getId());
            termsAndConditionsBean.setTitle(termsAndConditions.getTitle());
            termsAndConditionsBean.setDescription(termsAndConditions.getDescription());

            termsAndConditionsBeanList.add(termsAndConditionsBean);
            status.setResponse(termsAndConditionsBeanList);

            return new ResponseEntity<StatusBean>(status, HttpStatus.OK);
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "No Terms and Conditions"), HttpStatus.OK);
    }
}
