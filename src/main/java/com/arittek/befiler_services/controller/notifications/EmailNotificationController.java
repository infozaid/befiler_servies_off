package com.arittek.befiler_services.controller.notifications;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.notifications.EmailNotificationBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.notifications.EmailNotification;
import com.arittek.befiler_services.services.notifications.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/email/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EmailNotificationController {


    EmailNotificationService emailNotificationService;

    @Autowired
    public EmailNotificationController(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @RequestMapping(value = "/getEmailNotification", produces = "application/json", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<StatusBean> getEmailNotification() {
        List<EmailNotification> emailNotificationList = emailNotificationService.getAllActiveRecords();
        if (emailNotificationList == null) {
            StatusBean statusBean = new StatusBean(0, "No Record found! Please add new records");
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        }
        List<EmailNotificationBean> emailNotificationBeanList = new ArrayList<>();
        for (EmailNotification emailNotification : emailNotificationList) {
            EmailNotificationBean emailNotificationBean = new EmailNotificationBean();
            emailNotificationBean.setId(emailNotification.getId());
            emailNotificationBean.setEmail(emailNotification.getEmail());
            emailNotificationBean.setStatus(emailNotification.getStatus().getId());
            emailNotificationBeanList.add(emailNotificationBean);
        }
        StatusBean statusBean = new StatusBean(1, "Successful");
        statusBean.setResponse(emailNotificationBeanList);
        return new ResponseEntity<>(statusBean, HttpStatus.OK);
    }

    @RequestMapping(value = "/getEmailList", produces = "application/json", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<StatusBean> getAllEmails() {

        String[] emailNotificationList = emailNotificationService.getAllActiveEmails();
        if (emailNotificationList == null) {
            StatusBean statusBean = new StatusBean(0, "No Record found! Please add new Emails");
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        }
        StatusBean statusBean = new StatusBean(1, "Successful");
        statusBean.setResponse(Arrays.asList(emailNotificationList));
        return new ResponseEntity<>(statusBean, HttpStatus.OK);
    }

    /*@RequestMapping(value = "/insert", produces = "application/json", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<StatusBean> insertPaymentNotification(@RequestBody List<EmailNotificationBean> paymentNotificationBeanList) {

        if (paymentNotificationBeanList != null) {
            for (EmailNotificationBean paymentNotificationBean : paymentNotificationBeanList) {
                EmailNotification paymentNotification = new EmailNotification();
                paymentNotification.setId(paymentNotificationBean.getId());
                paymentNotification.setEmail(paymentNotificationBean.getEmail());
                paymentNotification.setStatus(AppStatus.ACTIVE);
                emailNotificationService.save(paymentNotification);
            }
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Payment Notifications inserted successfully"), HttpStatus.OK);
    }*/


    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<StatusBean> saveEmailNotification(@RequestBody EmailNotificationBean emailNotificationBean) {

        if (emailNotificationBean != null) {
            if (!org.apache.commons.lang.StringUtils.isNotEmpty(emailNotificationBean.getEmail())) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "Please insert email"), HttpStatus.OK);
            }
            EmailNotification emailNotification = new EmailNotification();
            emailNotification.setEmail(emailNotificationBean.getEmail());
            emailNotification.setStatus(AppStatus.ACTIVE);
            emailNotificationService.save(emailNotification);
            return new ResponseEntity<StatusBean>(new StatusBean(1, "Record inserted successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<StatusBean>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
    }


    /*@RequestMapping(value = "/update", produces = "application/json", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<StatusBean> updatePaymentNotification(@RequestBody List<EmailNotificationBean> paymentNotificationBeanList) {


        if (paymentNotificationBeanList != null) {
            for (EmailNotificationBean paymentNotificationBean : paymentNotificationBeanList) {
                EmailNotification paymentNotification = new EmailNotification();
                paymentNotification.setId(paymentNotificationBean.getId());
                paymentNotification.setEmail(paymentNotificationBean.getEmail());
                paymentNotification.setStatus(AppStatus.ACTIVE);
                emailNotificationService.update(paymentNotification);
            }
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Payment Notifications updated successfully"), HttpStatus.OK);
    }*/

    @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<StatusBean> updateEmailNotification(@RequestBody EmailNotificationBean emailNotificationBean) {
        if (emailNotificationBean != null) {
            if (emailNotificationBean.getId() == null) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "failed! Please try again with record id"), HttpStatus.OK);
            }
            if (!org.apache.commons.lang.StringUtils.isNotEmpty(emailNotificationBean.getEmail())) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "Please insert email"), HttpStatus.OK);
            }
            EmailNotification emailNotification = emailNotificationService.findOne(emailNotificationBean.getId());
            if (emailNotification == null) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "Record not found!"), HttpStatus.OK);
            }
            emailNotification.setEmail(emailNotificationBean.getEmail());
            emailNotification.setStatus(AppStatus.ACTIVE);
            emailNotificationService.update(emailNotification);
            return new ResponseEntity<StatusBean>(new StatusBean(1, "Record updated successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<StatusBean>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/remove", produces = "application/json", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseEntity<StatusBean> removeEmailNotification(@RequestBody EmailNotificationBean emailNotificationBean) {
        if (emailNotificationBean != null) {
            if (emailNotificationBean.getId() == null) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "failed! Please try again with record id"), HttpStatus.OK);
            }
            EmailNotification emailNotification = emailNotificationService.findOne(emailNotificationBean.getId());
            if (emailNotification == null) {
                return new ResponseEntity<StatusBean>(new StatusBean(0, "Record not found!"), HttpStatus.OK);
            }
            emailNotification.setStatus(AppStatus.DE_ACTIVE);
            emailNotificationService.update(emailNotification);
            return new ResponseEntity<StatusBean>(new StatusBean(1, "Record deleted successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<StatusBean>(new StatusBean(0, "Something went wrong"), HttpStatus.OK);
        }

    }

    @ResponseBody
    public ResponseEntity<StatusBean> get(){

        return new ResponseEntity<StatusBean>(new StatusBean(0,""), HttpStatus.OK);
    }

}

