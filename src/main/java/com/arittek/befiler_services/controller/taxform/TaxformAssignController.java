package com.arittek.befiler_services.controller.taxform;

import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.taxform.TaxformAssignBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.AssignServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/taxform/assign")
public class TaxformAssignController {

    private UsersServices usersServices;
    private AssignServices assignServices;
    private TaxformServices taxformServices;

    @Autowired
    public TaxformAssignController(UsersServices usersServices, AssignServices assignServices, TaxformServices taxformServices) {
        this.usersServices = usersServices;
        this.assignServices = assignServices;
        this.taxformServices = taxformServices;
    }

    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> save(@RequestBody TaxformAssignBean taxformAssignBean) throws Exception {
        try {
            if (taxformAssignBean != null &&
                    taxformAssignBean.getUserId() != null &&
                    taxformAssignBean.getTaxformId() != null &&
                    taxformAssignBean.getAuthorizerId() != null) {

                User user = usersServices.findOneByIdAndStatus(taxformAssignBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformAssignBean.getTaxformId());
                User authorizer = usersServices.findOneByIdAndStatus(taxformAssignBean.getAuthorizerId(), UserStatus.ACTIVE);

                if (user != null && taxform != null && authorizer != null) {

                    Assign alreadyAssigned = assignServices.findByLawyerAndTaxformAndAppStatus(user, taxform, AppStatus.ACTIVE);

                    if (alreadyAssigned == null) {

                        Assign taxformAssign = new Assign();

                        taxformAssign.setLawyer(user);
                        taxformAssign.setTaxform(taxform);
                        taxformAssign.setAppStatus(AppStatus.ACTIVE);

                        Assign persistTaxformAssign = assignServices.create(taxformAssign);
                        if (persistTaxformAssign == null) {
                            throw new Exception("Cann't Assign");
                        }

                        return new ResponseEntity<>(new Status(1, "Save successfully"), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new Status(0, "already send!"), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " + e);
            return new ResponseEntity<>(new Status(0, "Exception"), HttpStatus.OK);
        }
    }


}
