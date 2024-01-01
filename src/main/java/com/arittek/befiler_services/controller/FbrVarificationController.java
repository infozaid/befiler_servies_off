package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.fbr.FbrActiveTaxpayerRepository;
import com.arittek.befiler_services.services.FbrVarificationsServices;
import com.arittek.befiler_services.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/fbr/verification")
public class FbrVarificationController {

    @Autowired
    FbrActiveTaxpayerRepository taxpayerRepository ;
    FbrVarificationsServices fbrVarificationsServices=new FbrVarificationsServices();

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<NtnCaptureCodeBean> getFbrRegistration(HttpServletRequest request) throws Exception {
        NtnCaptureCodeBean bean=new NtnCaptureCodeBean();
        try {
            String macAddress = MacAddress.getMac(request.getRemoteAddr());
            System.out.println("MAC::::::::::::::"+macAddress);

            bean = fbrVarificationsServices.getNtnCaptureCodeFromFbrWebSite(1,macAddress);  //one just for ignoring null value it will be remove when user send its usrId
            bean.setCaputreUrl(bean.getCaputreUrl());
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(bean, HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> getTaxFormMinimal(@RequestBody FbrInputBean fbrInputBean, HttpServletRequest request) {
        MyPrint.println("Inputs-----------------"+fbrInputBean.getSearchBy()+" --"+fbrInputBean.getParm1()+"---"+fbrInputBean.getCaptchaCode());
        FbrBean fbrBean = new FbrBean();

        String macAddress = MacAddress.getMac(request.getRemoteAddr());
        try {
            fbrBean.setCode(0);
            fbrBean.setMessage("Invalid Parameters");
        /*....CHECK INPUTS ARE VALID OR NOT... */
        /*    if (!isValidParameter(fbrInputBean.getSearchBy(), fbrInputBean.getParm1(),fbrInputBean.getCaptchaCode())) {
                MyPrint.println("GIVEN PARAMATERS ARE INVALID........");
                fbrBean.setCode(0);
                fbrBean.setMessage("Invalid Parameters");
                return new ResponseEntity<FbrBean>(fbrBean,HttpStatus.OK);
            }*/
            /*....CONDITION SEARCH FOR  BY  NTN/FTN.....*/
            if (fbrInputBean.getSearchBy().equals("1") ) {
                CaptchaBean captchaBean = new CaptchaBean();
                System.out.println("NTN/FTN:::::::::::::::::::::::::::"+captchaBean.getName());
                captchaBean = fbrVarificationsServices.getFbrByNntOrFtn(fbrInputBean.getSearchBy(), fbrInputBean.getParm1(),fbrInputBean.getCaptchaCode(),macAddress,taxpayerRepository);
                System.out.println("name:::::::::::::::::::::::::::"+captchaBean.getName());
                return new ResponseEntity<CaptchaBean>(captchaBean, HttpStatus.OK);
            }
            else if (fbrInputBean.getSearchBy().equals("3")) {
                CaptchaBean captchaBean = fbrVarificationsServices.getFbrByNntOrFtn(fbrInputBean.getSearchBy(), fbrInputBean.getParm1(),fbrInputBean.getCaptchaCode(),macAddress,taxpayerRepository);
                return new ResponseEntity<CaptchaBean>(captchaBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new FbrBean(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new FbrBean(), HttpStatus.OK);
    }

}