package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.taxform.TaxformAssignBean;
import com.arittek.befiler_services.beans.taxform.TaxformBeanForList;
import com.arittek.befiler_services.beans.taxform.TaxformDocumentsBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.TaxformDocuments;
import com.arittek.befiler_services.model.taxform.Taxform_Status;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.*;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/lawyer")
public class LawyerController {

    private UsersServices usersServices;
    private AssignServices assignServices;
    private TaxformStatusServices statusServices;
    private TaxformServices taxformServices;
    private TaxformDocumentsServices taxformDocumentsServices;
    private NotificationServices notificationServices;

    @Value("${static.content.taxform.path}")
    private String staticContentTaxformPath;

    @Autowired
    LawyerController(UsersServices usersServices, AssignServices assignServices, TaxformStatusServices statusServices, TaxformServices taxformServices, TaxformDocumentsServices taxformDocumentsServices, NotificationServices notificationServices) {
        this.usersServices=usersServices;
        this.assignServices = assignServices;
        this.statusServices = statusServices;
        this.taxformServices = taxformServices;
        this.taxformDocumentsServices = taxformDocumentsServices;
        this.notificationServices = notificationServices;
    }

    @RequestMapping(produces = "application/json",method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StatusBean> getLawyers() {
        try {
            /*List<User> userList = usersServices.findAllByUserTypeAndStatus(usersServices.findUserTypeById(3), usersServices.findUserStatusById(1));*/
            /*List<User> userList = usersServices.findAllByUserTypeAndStatus(usersServices.findUserTypeById(3), UserStatus.ACTIVE);*/
            List<User> lawyers = usersServices.findAllActiveLawyers();
            List<LawyerBean> lawyerBeanList = new ArrayList<>();

            if (lawyers != null) {
                for(User lawyer : lawyers){
                    if(lawyer != null/* && user.getUserDetail() != null*/) {
                        /*UserDetail userDetail = user.getUserDetail();*/

                        LawyerBean lawyerBean = new LawyerBean();
                        /*lawyerBean.setFirstName(userDetail.getFirstName());
                        lawyerBean.setLastName(userDetail.getLastName());
                        lawyerBean.setAddress(userDetail.getAddress());
                        lawyerBean.setMobileNo(userDetail.getMobileNo());*/
                        lawyerBean.setFirstName(lawyer.getFullName());
                        lawyerBean.setAddress(lawyer.getAddress());
                        lawyerBean.setMobileNo(lawyer.getMobileNo());

                        List<Assign> taxformAssignList = assignServices.findAllByLawyerAndAppStatus(lawyer, AppStatus.ACTIVE);

                        List<TaxformAssignBean> taxformAssignBeanList = new ArrayList<>();
                        for (Assign taxformAssign : taxformAssignList) {
                            if (taxformAssign != null && taxformAssign.getTaxform() != null && taxformAssign.getLawyer() != null) {
                                TaxformAssignBean taxformAssignBean = new TaxformAssignBean();

                                /*if (taxformAssign.getAuthorizer() != null) {
                                    taxformAssignBean.setAuthorizerId(taxformAssign.getAuthorizer().getId());
                                    taxformAssignBean.setAuthorizerEmail(taxformAssign.getAuthorizer().getEmailAddress());
                                }*/

                                taxformAssignBean.setUserId(taxformAssign.getLawyer().getId());
                                taxformAssignBean.setUserEmail(taxformAssign.getLawyer().getEmailAddress());

                                taxformAssignBean.setTaxformId(taxformAssign.getTaxform().getId());
                                taxformAssignBean.setTaxformYear(taxformAssign.getTaxform().getTaxformYear().getYear() + "");

                                taxformAssignBean.setCurrentDate(CommonUtil.changeTimestampToString(taxformAssign.getLastModifiedDate()));

                                taxformAssignBeanList.add(taxformAssignBean);
                            }
                        }
                        lawyerBean.setTaxformAssignBeanList(taxformAssignBeanList);
                        lawyerBeanList.add(lawyerBean);
                    }
                    StatusBean status = new StatusBean(1, "Succeccfull");
                    status.setResponse(lawyerBeanList);
                    return new ResponseEntity<>(status, HttpStatus.OK);

                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(0,"Record not Found!"), HttpStatus.OK);
    }

    /*NEW LAWYER*/

    @RequestMapping(value = "/new",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> newLawyer(@RequestBody InputBean inputBean){
        if(inputBean != null){
            if(inputBean.getUserId() != null) {
                try {
                    /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(),userStatusRepository.findOne(1));*/
                    User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                    Taxform_Status taxformStatus = statusServices.findOneByLawyerNewStatus();
                    if(user != null && taxformStatus != null) {
                        StatusBean statusBean = new StatusBean(1, "Successfully");
                        List<TaxformBeanForList> taxformBeanForListList = new ArrayList<>();
                        List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                        for (Taxform taxform : taxformList) {
                            if (taxform != null && taxform.getStatus().getId() == taxformStatus.getId()) {
                                TaxformBeanForList taxformBeanForList = new TaxformBeanForList();

                                taxformBeanForList.setId(taxform.getId());
                                taxformBeanForList.setTaxformYear(taxform.getTaxformYear().getYear() + "");
                                taxformBeanForList.setUserName(taxform.getNameAsPerCnic());
                                taxformBeanForList.setUserCnic(taxform.getCnic());
                                taxformBeanForList.setUserEmail(taxform.getEmail());

                                taxformBeanForListList.add(taxformBeanForList);
                            }
                        }
                        statusBean.setResponse(taxformBeanForListList);
                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                    Logger4j.getLogger().error("Exception : " , e);
                    return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(new StatusBean(0,""), HttpStatus.OK);
    }


    @RequestMapping(value = "/new/update",produces = "application/json",method = RequestMethod.POST)
    public Status updateNew(@RequestBody InputBean inputBean){
        if (inputBean != null && inputBean.getTaxformId() != null && inputBean.getUserId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(inputBean.getTaxformId());
                Taxform_Status taxform_status = statusServices.findOneByLawyerPendingStatus();
                String status = "";
                if (user != null && taxform != null && taxform_status != null) {
                    taxform.setStatus(taxform_status);
                /*status = CommonUtil.sendMail(taxform);
                MyPrint.println(":::if:::::::::::::::::sending email :::::::::::"+status);*/
                    taxformServices.updateTaxform(taxform);
                    return new Status(1, " update");
                } else {
                    MyPrint.println(":::::::ELSE:::::::::::SENDING EMAIL::::::::::::");
                /*MyPrint.println(":::::::else:::::::::::::sending email :::::::::::"+status);*/
                    return new Status(0, " not update");
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                e.printStackTrace();
            }
        }
        return new Status(0, " not update");
    }

    /*PENDING*/

    @RequestMapping(value = "/pending",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> pendingLawyer(@RequestBody InputBean inputBean){
        if(inputBean != null){
            if(inputBean.getUserId() != null){
                try {
                    /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(),userStatusRepository.findOne(1));*/
                    User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                    Taxform_Status taxformStatus = statusServices.findOneByLawyerPendingStatus();
                    if(user != null && taxformStatus != null) {
                        StatusBean statusBean = new StatusBean(1, "Successful");
                        List<TaxformBeanForList> taxformBeanForListList = new ArrayList<>();
                        List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                        for (Taxform taxform : taxformList) {
                            if (taxform != null && taxform.getStatus().getId() == taxformStatus.getId()) {
                                TaxformBeanForList taxformBeanForList = new TaxformBeanForList();

                                taxformBeanForList.setId(taxform.getId());
                                taxformBeanForList.setTaxformYear(taxform.getTaxformYear().getYear() + "");
                                taxformBeanForList.setUserName(taxform.getNameAsPerCnic());
                                taxformBeanForList.setUserCnic(taxform.getCnic());
                                taxformBeanForList.setUserEmail(taxform.getEmail());

                                taxformBeanForListList.add(taxformBeanForList);
                            }
                        }
                        statusBean.setResponse(taxformBeanForListList);
                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    }
                } catch (Exception e) {
                    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                    Logger4j.getLogger().error("Exception : " , e);
                    return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(new StatusBean(0,""), HttpStatus.OK);
    }


    @RequestMapping(value = "/pending/update",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> updatePending(@RequestBody InputBean inputBean){
        try {

            if (inputBean.getTaxformDocumentsBeanList() == null || inputBean.getTaxformDocumentsBeanList().size() <= 0) {
                return new ResponseEntity<>(new StatusBean(0, "Please upload documents"), HttpStatus.OK);
            }

            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Session Expired"), HttpStatus.OK);
            }

            Taxform taxform;
            if (inputBean.getTaxformId() != null) {
                taxform = taxformServices.findOne(inputBean.getTaxformId());
                if (taxform == null) {
                    return new ResponseEntity<>(new StatusBean(0, "Incomplete Data :: Tax Form ID"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data :: Tax Form ID"), HttpStatus.OK);
            }

            Taxform_Status taxformStatus = statusServices.findOneByLawyerCompleteStatus();
            if (taxformStatus == null) {
                return new ResponseEntity<>(new StatusBean(0, "Can't update Tax Form status."), HttpStatus.OK);
            }

            taxform.setStatus(taxformStatus);
            taxform = taxformServices.updateTaxform(taxform);

            CommonUtil.sendMailForTaxformStatus(taxform);

            List<String> filePaths = new ArrayList<>();
            for (TaxformDocumentsBean taxformDocumentsBean : inputBean.getTaxformDocumentsBeanList()) {

                if (taxformDocumentsBean.getFilename() != null && taxformDocumentsBean.getFiletype() != null && taxformDocumentsBean.getBase64() != null) {
                    TaxformDocuments taxformDocuments = new TaxformDocuments();

                    taxformDocuments.setTaxform(taxform);
                    taxformDocuments.setStatus(AppStatus.ACTIVE);

                    taxformDocuments.setDocumentType("TaxForm");
                    taxformDocuments.setDocumentFormat(taxformDocumentsBean.getFiletype());
                    taxformDocuments.setDocumentDescription(taxformDocumentsBean.getFilename());

                    taxformDocuments = taxformDocumentsServices.save(taxformDocuments);

                    if (taxformDocuments != null && taxformDocuments.getId() != null) {

                        String fileFormat = FilenameUtils.getExtension(taxformDocuments.getDocumentDescription());
                        String fileName = taxform.getUser().getId() + "_" + taxformDocuments.getId() + "." + fileFormat;
                        String fileUrl= staticContentTaxformPath + fileName;
                        byte[] imageByte= Base64.decodeBase64(taxformDocumentsBean.getBase64());
                        BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                        imageOutFile.write(imageByte);
                        imageOutFile.close();

                        taxformDocuments.setDocumentName(fileName);

                        taxformDocumentsServices.save(taxformDocuments);
                        filePaths.add(fileUrl);
                    }
                }
            }
            EmailSender.sendEmail(
                    EmailUtil.emailUponSuccessfulSubmissionOfReturn(taxform.getUser(), taxform),
                    "Your case has been filed!",
                    taxform.getUser().getEmailAddress(),
                    filePaths);

            Notification notification = new Notification();
            notification.setArchiveFlag(0);
            notification.setReadFlag(0);
            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
            notification.setToUser(taxform.getUser().getId());
            notification.setNotificationTitle("Your case has been filed!");
            notification.setNotificationDescription("Your tax return for Tax Year ["+taxform.getTaxformYear().getYear()+"] has been successfully filed. A copy of your of Income Tax Return and Wealth Statement are attached for your record.");

            notificationServices.save(notification);

            return new ResponseEntity<>(new StatusBean(1, "Record updated successfully"), HttpStatus.OK);

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

        /*COMPLETE*/

    @RequestMapping(value = "/complete",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> completeLawyer(@RequestBody InputBean inputBean){
        if(inputBean != null){
            if(inputBean.getUserId() != null) {
                try {
                    /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(),userStatusRepository.findOne(1));*/
                    User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                    Taxform_Status taxformStatus = statusServices.findOneByLawyerCompleteStatus();
                    if(user != null && taxformStatus != null) {
                        StatusBean statusBean = new StatusBean(1, "Successfully");
                        List<TaxformBeanForList> taxformBeanForListList = new ArrayList<>();
                        List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                        for (Taxform taxform : taxformList) {
                            if (taxform != null && taxform.getStatus().getId() == taxformStatus.getId()) {
                                TaxformBeanForList taxformBeanForList = new TaxformBeanForList();

                                taxformBeanForList.setId(taxform.getId());
                                taxformBeanForList.setTaxformYear(taxform.getTaxformYear().getYear() + "");
                                taxformBeanForList.setUserName(taxform.getNameAsPerCnic());
                                taxformBeanForList.setUserCnic(taxform.getCnic());
                                taxformBeanForList.setUserEmail(taxform.getEmail());

                                taxformBeanForListList.add(taxformBeanForList);
                            }
                        }
                        statusBean.setResponse(taxformBeanForListList);
                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    }

                } catch (Exception e) {
                    Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                    Logger4j.getLogger().error("Exception : " , e);
                    return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
                }

            }
        }

        return new ResponseEntity<>(new StatusBean(0,""), HttpStatus.OK);
    }

    /*// ----------------------------------------- create lawyer ---------------------------
    @RequestMapping(produces = "application/json" , method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody LawyerAssignBean lawyerBean)throws Exception{
        if(lawyerBean != null){
            User authorizer = usersServices.findOne(lawyerBean.getAuthorizer());
            Taxform taxform = taxformServices.findOne(lawyerBean.getTaxformId());
            if(authorizer != null && taxform != null){
                LawyerAssign lawyerAssign  = new LawyerAssign();
                lawyerAssign.setLawyer(authorizer);
                lawyerAssign.setTaxform(taxform);
                lawyerAssign.setAppStatus(appStatusServices.findOne(1));
                lawyerAssign.setCurrentDate(CommonUtil.getCurrentTimestamp());
                lawyerServices.create(lawyerAssign);
                return new ResponseEntity<>(new Status(1,"successfully save data"),HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0,"Incomplete data!"),HttpStatus.OK);
    }*/


    /*@RequestMapping(value = "/assignTaxforms", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> get() throws Exception {


        List<Taxform> getListOfTaxform = taxformServices.findAllByStatus(taxform_status_repository.findOne(4)); // list of incomplete taxform
        List<User> lawyers = usersServices.findAllUserByUserTypeId(3); // list of lawyer
        List<LawyerAssign> lawyerList1 = null, lawyerList2 = null;

        for (Taxform taxform : getListOfTaxform) {
            LawyerAssign lawyerAssign = lawyerServices.findByTaxform(taxform); // check for table
            if (lawyerAssign == null) {
                for (User user : lawyers) {
                    lawyerList1 = lawyerServices.findAllByLawyerAndStatus(user, appStatusServices.findOne(1)); // check in lawyer assign
                    for (User user1 : lawyers) {
                        if (user != user1) {
                            LawyerAssign lawyerAssign3 = lawyerServices.findByTaxform(taxform); // check for table
                            if (lawyerList1.size() < 2) { // lawyer taxform limit
                                if (lawyerAssign3 == null) {
                                    lawyerList2 = lawyerServices.findAllByLawyerAndStatus(user1, appStatusServices.findOne(1)); // check in lawyer assign
                                    if (lawyerList1.size() < lawyerList2.size() || lawyerList1.size() == lawyerList2.size()) {
                                        LawyerAssign lawyerAssign1 = new LawyerAssign();
                                        lawyerAssign1.setTaxform(taxform);  // taxform
                                        lawyerAssign1.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                        lawyerAssign1.setAppStatus(appStatusServices.findOne(1));
                                        lawyerAssign1.setLawyer(user);        // user
                                        lawyerServices.create(lawyerAssign1);
                                    }
                                }
                            }
                        }
                    }
                }
            }//unassigned
        } // total taxform
        return new ResponseEntity<>(new Status(1, "successfully taxform assigned to lawyers"), HttpStatus.OK);
    }*/

} // class

