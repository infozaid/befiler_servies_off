package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.user.UsersServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/payment/fbr")
public class FbrPaymentController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;

    @Autowired
    FbrPaymentController(UsersServices usersServices, TaxformServices taxformServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
    }

    /*@RequestMapping(value = "/active",produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getByYear(@RequestBody InputBean inputBean)throws Exception{
        if(inputBean != null && inputBean.getUserId() != null && inputBean.getTaxformId() != null){
            User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
            Taxform taxform = taxformServices.findOne(inputBean.getTaxformId());

            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0,"Session expired."), HttpStatus.OK);
            }

            SettingFbrPayment settingFbrPayment = settingFbrPaymentServices.findOneByStatus(AppStatus.ACTIVE);
            if (settingFbrPayment == null) {
                return new ResponseEntity<>(new StatusBean(0,"Payment is not defined for this year. Please contact support team."),HttpStatus.OK);
            }

            PaymentBean paymentBean = new PaymentBean();
            paymentBean.setId(settingFbrPayment.getId());
            paymentBean.setUserId(settingFbrPayment.getUser().getId());
            paymentBean.setStatus(settingFbrPayment.getStatus().getId());
            paymentBean.setAmount(settingFbrPayment.getAmount());
            paymentBean.setCurrentDate(CommonUtil.changeTimestampToString(settingFbrPayment.getCurrentDate()));

            List<PaymentBean> paymentBeanList = new ArrayList<>();
            paymentBeanList.add(paymentBean);
            StatusBean statusBean = new StatusBean(1, "Successfully");
            statusBean.setResponse(paymentBeanList);

            return new ResponseEntity<>(statusBean,HttpStatus.OK);
        }
        return new ResponseEntity<>(new StatusBean(0,"incomplete data."),HttpStatus.OK);
    }*/
}
