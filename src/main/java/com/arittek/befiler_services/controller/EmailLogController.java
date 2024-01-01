package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.EmailLogBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.email.EmailLog;
import com.arittek.befiler_services.services.EmailLogServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.Logger4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/emailLog")
public class EmailLogController {

    private final UsersServices usersServices;
    private final EmailLogServices emailLogServices;

    @Autowired
    public EmailLogController(UsersServices usersServices, EmailLogServices emailLogServices) {
        this.usersServices = usersServices;
        this.emailLogServices = emailLogServices;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<StatusBean>  getAllEmailLogs() {
        try {
            List<EmailLogBean> emailLogBeanList = new ArrayList<>();
            List<EmailLog> emailLogList = emailLogServices.findAll();
            if (emailLogList != null && emailLogList.size() > 0) {
                for (EmailLog emailLog : emailLogList) {
                    EmailLogBean emailLogBean = new EmailLogBean(emailLog);

                    emailLogBeanList.add(emailLogBean);
                }
            }
            StatusBean statusBean = new StatusBean(1, "Successfull");
            statusBean.setResponse(emailLogBeanList);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/resend", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<StatusBean>  resendEmail(@RequestBody EmailLogBean emailLogBean) {
        try {

            if (emailLogBean.getId() == null) {
                return new ResponseEntity<>(new StatusBean(0, "1 Please select an email to resend"), HttpStatus.OK);
            }

            EmailLog emailLog = emailLogServices.findOneById(emailLogBean.getId());
            if (emailLog == null) {
                return new ResponseEntity<>(new StatusBean(0, "2 Please select an email to resend"), HttpStatus.OK);
            }

            if (StringUtils.isNotEmpty(emailLog.getFilesPath()))
                EmailSender.sendEmail(emailLog.getEmailBody(), emailLog.getEmailSubject(), emailLog.getEmailAddress(), Arrays.asList(emailLog.getFilesPath().split("##")));
            else
                EmailSender.sendEmail(emailLog.getEmailBody(), emailLog.getEmailSubject(), emailLog.getEmailAddress());

            return new ResponseEntity<>(new StatusBean(1, "Successful"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }


}
