package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.PaymentBean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.enums.*;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.AssignServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.TaxformStatusServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServicesImp;
import com.arittek.befiler_services.services.payment.PaymentServices;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/assign")
public class AssignController {

    private UsersServices usersServices;
    private AssignServices assignServices;
    private TaxformServices taxformServices;
    private PaymentServices paymentServices;
    private TaxformStatusServices taxformStatusServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;

    @Autowired
    public AssignController(UsersServices usersServices, AssignServices assignServices, TaxformServices taxformServices, PaymentServices paymentServices, TaxformStatusServices taxformStatusServices, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.usersServices = usersServices;
        this.assignServices = assignServices;
        this.taxformServices = taxformServices;
        this.paymentServices = paymentServices;
        this.taxformStatusServices = taxformStatusServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/reAssign", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> reAssign(@RequestBody PaymentBean paymentBean) {
        if (paymentBean != null) {
            try {
                User user = usersServices.getUserFromToken();
                if (user == null)
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);


                if (paymentBean.getLawyerId() == null)
                    return new ResponseEntity<>(new StatusBean(0, "Lawyer is not selected"), HttpStatus.OK);
                User lawyer = usersServices.findOneActiveLawyerRecordById(paymentBean.getLawyerId());
                if (lawyer == null)
                    return new ResponseEntity<>(new StatusBean(0, "Lawyer is not found"), HttpStatus.OK);

                if (paymentBean.getId() == null)
                    return new ResponseEntity<>(new StatusBean(0, "Record is not selected"), HttpStatus.OK);
                Payment payment = paymentServices.findOneById(paymentBean.getId());
                if (payment == null)
                    return new ResponseEntity<>(new StatusBean(0, "Record is not found"), HttpStatus.OK);

                if (payment.getProductType() == ProductType.TAXFORM) {
                    if (payment.getTaxform() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                        Taxform taxform = payment.getTaxform();
                        Assign assign = assignServices.checkIfTaxformIsAssignedToLawyer(payment.getTaxform());
                        if (assign != null) {
                            assign.setLawyer(lawyer);

                            assignServices.update(assign);
                            return new ResponseEntity<>(new StatusBean(1, "Taxform assigned successfully"), HttpStatus.OK);
                        } else {
                            Assign taxformAssign = new Assign();
                            taxformAssign.setAssignType(AssignType.TAXFORM);
                            taxformAssign.setTaxform(taxform);
                            taxformAssign.setAppStatus(AppStatus.ACTIVE);
                            taxformAssign.setLawyer(lawyer);
                            assignServices.create(taxformAssign);

                            taxform.setStatus(taxformStatusServices.findOneByLawyerNewStatus());
                            taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

                            taxformServices.updateTaxform(taxform);

                            return new ResponseEntity<>(new StatusBean(1, "Taxform assigned successfully"), HttpStatus.OK);
                        }
                    }
                } else if (payment.getProductType() == ProductType.NTN) {
                    if (payment.getFbrUserAccountInfo() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                        FbrUserAccountInfo fbrUserAccountInfo = payment.getFbrUserAccountInfo();
                        Assign assign = assignServices.checkIfFbrUserAccountInfoIsAssignedToLawyer(payment.getFbrUserAccountInfo());
                        if (assign != null) {
                            assign.setLawyer(lawyer);

                            assignServices.update(assign);
                            return new ResponseEntity<>(new StatusBean(1, "NTN assigned successfully"), HttpStatus.OK);
                        } else {
                            Assign ntnAssign = new Assign();
                            ntnAssign.setAssignType(AssignType.NTN);
                            ntnAssign.setFbrUserAccountInfo(fbrUserAccountInfo);
                            ntnAssign.setAppStatus(AppStatus.ACTIVE);
                            ntnAssign.setLawyer(lawyer);
                            assignServices.create(ntnAssign);

                            fbrUserAccountInfoServices.updateFbrUserAccountInfoToLawyerAssignStatus(fbrUserAccountInfo);
                            return new ResponseEntity<>(new StatusBean(1, "NTN assigned successfully"), HttpStatus.OK);
                        }
                    }
                }
                return new ResponseEntity<>(new StatusBean(0, "Can't assign"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/multi", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> multiAssign(@RequestBody PaymentBean paymentBean) {
        if (paymentBean != null) {
            try {
                User user = usersServices.getUserFromToken();
                if (user == null)
                    return new ResponseEntity<>(new StatusBean(0, "Session expired"), HttpStatus.OK);

                if (paymentBean.getLawyerId() == null)
                    return new ResponseEntity<>(new StatusBean(0, "Lawyer is not selected"), HttpStatus.OK);
                User lawyer = usersServices.findOneActiveLawyerRecordById(paymentBean.getLawyerId());
                if (lawyer == null)
                    return new ResponseEntity<>(new StatusBean(0, "Lawyer is not found"), HttpStatus.OK);

                if (paymentBean.getIds().isEmpty())
                    return new ResponseEntity<>(new StatusBean(0, "Record is not selected"), HttpStatus.OK);

                List<PaymentBean> paymentIds = paymentBean.getIds();

                if (paymentIds == null)
                    return new ResponseEntity<>(new StatusBean(0, "Record is not found"), HttpStatus.OK);

                int taxforms = 0;
                int ntns = 0;

                for (PaymentBean paymentBean1 : paymentIds) {

                    Payment payment = paymentServices.findOneById(paymentBean1.getId());
                    if (payment == null)
                        return new ResponseEntity<>(new StatusBean(0, "Record is not found"), HttpStatus.OK);

                    if (payment.getProductType() == ProductType.TAXFORM) {
                        if (payment.getTaxform() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                            Taxform taxform = payment.getTaxform();
                            Assign assign = assignServices.checkIfTaxformIsAssignedToLawyer(payment.getTaxform());
                            if (assign != null) {
                                assign.setLawyer(lawyer);

                                assignServices.update(assign);
                                taxforms++;
                            } else {
                                Assign taxformAssign = new Assign();
                                taxformAssign.setAssignType(AssignType.TAXFORM);
                                taxformAssign.setTaxform(taxform);
                                taxformAssign.setAppStatus(AppStatus.ACTIVE);
                                taxformAssign.setLawyer(lawyer);
                                assignServices.create(taxformAssign);

                                taxform.setStatus(taxformStatusServices.findOneByLawyerNewStatus());
                                taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

                                taxformServices.updateTaxform(taxform);
                                taxforms++;
                            }
                        }
                    } else if (payment.getProductType() == ProductType.NTN) {
                        if (payment.getFbrUserAccountInfo() != null && payment.getSettingPayment() != null && payment.getPaymentCustomerInfo() != null) {
                            FbrUserAccountInfo fbrUserAccountInfo = payment.getFbrUserAccountInfo();
                            Assign assign = assignServices.checkIfFbrUserAccountInfoIsAssignedToLawyer(payment.getFbrUserAccountInfo());
                            if (assign != null) {
                                assign.setLawyer(lawyer);
                                assignServices.update(assign);
                                ntns++;
                            } else {
                                Assign ntnAssign = new Assign();
                                ntnAssign.setAssignType(AssignType.NTN);
                                ntnAssign.setFbrUserAccountInfo(fbrUserAccountInfo);
                                ntnAssign.setAppStatus(AppStatus.ACTIVE);
                                ntnAssign.setLawyer(lawyer);
                                assignServices.create(ntnAssign);

                                fbrUserAccountInfoServices.updateFbrUserAccountInfoToLawyerAssignStatus(fbrUserAccountInfo);
                                ntns++;
                            }
                        }
                    }

                }
                return new ResponseEntity<>(new StatusBean(1, taxforms + " Taxform assigned successfully and " + ntns + " NTN assigned successfully"), HttpStatus.OK);

            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }
}
