package com.arittek.befiler_services.controller.suggest_us;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.suggest_us.SuggestEmailBean;
import com.arittek.befiler_services.model.suggest_us.SuggestUs;
import com.arittek.befiler_services.repository.suggest_us.SuggestUsRepository;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/suggestUs")
public class SuggestEmailController {

    private SuggestUsRepository suggestUsRepository;
    @Autowired
    SuggestEmailController(SuggestUsRepository suggestUsRepository){
        this.suggestUsRepository = suggestUsRepository;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> send(@RequestBody SuggestEmailBean senderBean) {

        if (senderBean != null) {

            if (senderBean.getFromEmail() == null && senderBean.getFromEmail().equals("") || senderBean.getToEmail() == null && senderBean.getToEmail().equals("")) {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Email!"), HttpStatus.OK);
            }
            if (senderBean.getName() == null && senderBean.getName().equals("")) {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Name!"), HttpStatus.OK);
            }
            if (senderBean.getMessage() == null && senderBean.getMessage().equals("")) {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Message!"), HttpStatus.OK);
            }

            try {

                SuggestUs suggestUs = new SuggestUs();
                suggestUs.setName(senderBean.getName());
                suggestUs.setMobileNo(senderBean.getMobileNo());
                suggestUs.setMessage(senderBean.getMessage());
                suggestUs.setFromEmail(senderBean.getFromEmail());
                suggestUsRepository.save(suggestUs);

                boolean bool = EmailSender.sendEmail(EmailUtil.sendEmailToBoss(senderBean.getName(),senderBean.getFromEmail(), senderBean.getMobileNo(), senderBean.getMessage()), "Suggest Us Email", senderBean.getToEmail());//senderBean.getEmail()

                if (bool) {
                    System.out.println("suggest us email send successfully");
                    StatusBean bean = new StatusBean(1, "Successfully.");
                    return new ResponseEntity<StatusBean>(bean, HttpStatus.OK);
                } else {
                    System.out.println("suggest us email sending failed!");
                    return new ResponseEntity<StatusBean>(new StatusBean(0, "Email sending Failed!"), HttpStatus.OK);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Incomplete Data!"), HttpStatus.OK);

    }

}
