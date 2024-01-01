package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfoDocuments;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.AssignServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoDocumentsServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.util.*;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/lawyer/ntn")
public class LawyerNTNController {

    private UsersServices usersServices;
    private AssignServices assignServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private FbrUserAccountInfoDocumentsServices fbrUserAccountInfoDocumentsServices;
    private NotificationServices notificationServices;

    @Value("${static.content.path}")
    private String staticContentPath;

    @Autowired
    public LawyerNTNController(NotificationServices notificationServices,UsersServices usersServices, AssignServices assignServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, FbrUserAccountInfoDocumentsServices fbrUserAccountInfoDocumentsServices) {
        this.notificationServices = notificationServices;
        this.usersServices = usersServices;
        this.assignServices = assignServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.fbrUserAccountInfoDocumentsServices = fbrUserAccountInfoDocumentsServices;
    }

    @RequestMapping(value = "/new",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllNTNAssignedToLawyer(@RequestBody InputBean inputBean){
        try {
            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            List<Assign> assignList = assignServices.findAllNewNTNAssignByLawyerAndActiveStatus(user);
            StatusBean statusBean = new StatusBean(1, "Successfully");
            List<AssignBean> assignBeans = new ArrayList<>();
            if (assignList != null && assignList.size() > 0) {
                for (Assign assign : assignList) {
                    AssignBean assignBean = new AssignBean();

                    assignBean.setId(assign.getId());
                    assignBean.setUserId(assign.getFbrUserAccountInfo().getUser().getId());
                    assignBean.setUserEmail(assign.getFbrUserAccountInfo().getUser().getEmailAddress());
                    /*assignBean.setUserName(assign.getFbrUserAccountInfo().getUser().getUserDetail().getFirstName());*/
                    assignBean.setUserName(assign.getFbrUserAccountInfo().getUser().getFullName());

                    assignBeans.add(assignBean);
                }
            }
            statusBean.setResponse(assignBeans);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/new/detail",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllNTNAssignedToLawyerNewDetail(@RequestBody InputBean inputBean) {
        try {

            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            Assign assign;
            if (inputBean.getAssignId() != null) {
                assign = assignServices.findOneByIdAndActiveStatus(inputBean.getAssignId());
                if (assign == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Error"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Please select properly"), HttpStatus.OK);
            }

            FbrUserAccountInfoBean fbrUserAccountInfoBean1 = new FbrUserAccountInfoBean();
            List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsListCnic = fbrUserAccountInfoDocumentsServices.findAllActiveRecordsByUserAndDocumentType(assign.getFbrUserAccountInfo().getUser(),"Cnic");
            List<FbrUserAccountInfoDocumentsBean> fbrUserAccountInfoDocumentsBeanListCnic = new ArrayList<>();
            if (fbrUserAccountInfoDocumentsListCnic != null && fbrUserAccountInfoDocumentsListCnic.size() > 0) {
                fbrUserAccountInfoBean1.setUserEmail(assign.getFbrUserAccountInfo().getUser().getEmailAddress());
                /*fbrUserAccountInfoBean1.setUserName(assign.getFbrUserAccountInfo().getUser().getUserDetail().getFirstName());*/
                fbrUserAccountInfoBean1.setUserName(assign.getFbrUserAccountInfo().getUser().getFullName());
                fbrUserAccountInfoBean1.setUserCnic(assign.getFbrUserAccountInfo().getUser().getCnicNo());
                /*fbrUserAccountInfoBean1.setUserMobileNo(assign.getFbrUserAccountInfo().getUser().getUserDetail().getMobileNo());*/
                fbrUserAccountInfoBean1.setUserMobileNo(assign.getFbrUserAccountInfo().getUser().getMobileNo());
                for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsListCnic) {
                    FbrUserAccountInfoDocumentsBean fbrUserAccountInfoDocumentsBean = new FbrUserAccountInfoDocumentsBean();

                    fbrUserAccountInfoDocumentsBean.setId(fbrUserAccountInfoDocuments.getId());
                    fbrUserAccountInfoDocumentsBean.setFilename(fbrUserAccountInfoDocuments.getDocumentDescription());

                    fbrUserAccountInfoDocumentsBean.setBase64(Base64.encodeBase64String(Base64Util.readBytesFromFile(staticContentPath + fbrUserAccountInfoDocuments.getDocumentName())));
                    if (fbrUserAccountInfoDocuments.getDocumentFormat() != null) {
                        fbrUserAccountInfoDocumentsBean.setFiletype(fbrUserAccountInfoDocuments.getDocumentFormat());
                    }
                    fbrUserAccountInfoDocumentsBeanListCnic.add(fbrUserAccountInfoDocumentsBean);
                }
                fbrUserAccountInfoBean1.setCnicList(fbrUserAccountInfoDocumentsBeanListCnic);
            }
            List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
            fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean1);

            StatusBean statusBean = new StatusBean(1, "Fbr data found");
            statusBean.setResponse(fbrUserAccountInfoBeanList);

            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/new/update",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllNTNAssignedToLawyerNewUpdate(@RequestBody InputBean inputBean){
        try {

            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            Assign assign;
            if (inputBean.getAssignId() != null) {
                assign = assignServices.findOneByIdAndActiveStatus(inputBean.getAssignId());
                if (assign == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Error"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Please select properly"), HttpStatus.OK);
            }

            FbrUserAccountInfo fbrUserAccountInfo = assign.getFbrUserAccountInfo();
            if (fbrUserAccountInfo == null) {
                return new ResponseEntity<>(new StatusBean(0, "Error"), HttpStatus.OK);
            }

            fbrUserAccountInfoServices.updateFbrUserAccountInfoToLawyerOpenStatus(fbrUserAccountInfo);

            return new ResponseEntity<>(new StatusBean(1, "Successfull"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/open",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllOpenNTNAssignedToLawyer(@RequestBody InputBean inputBean){
        try {

            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            List<Assign> assignList = assignServices.findAllOpenNTNAssignByLawyerAndActiveStatus(user);
            StatusBean statusBean = new StatusBean(1, "Successfully");
            List<AssignBean> assignBeans = new ArrayList<>();
            if (assignList != null && assignList.size() > 0) {
                for (Assign assign : assignList) {
                    AssignBean assignBean = new AssignBean();

                    assignBean.setId(assign.getId());
                    assignBean.setUserId(assign.getFbrUserAccountInfo().getUser().getId());
                    assignBean.setUserEmail(assign.getFbrUserAccountInfo().getUser().getEmailAddress());
                    /*assignBean.setUserName(assign.getFbrUserAccountInfo().getUser().getUserDetail().getFirstName());*/
                    assignBean.setUserName(assign.getFbrUserAccountInfo().getUser().getFullName());

                    assignBeans.add(assignBean);
                }
            }
            statusBean.setResponse(assignBeans);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/open/detail",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getOpenNTNAssingToLawyer(@RequestBody InputBean inputBean) {
        try {

            User user;
            if (inputBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            Assign assign;
            if (inputBean.getAssignId() != null) {
                assign = assignServices.findOneByIdAndActiveStatus(inputBean.getAssignId());
                if (assign == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Error"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Please select properly"), HttpStatus.OK);
            }

            FbrUserAccountInfoBean fbrUserAccountInfoBean1 = new FbrUserAccountInfoBean();
            List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsListCnic = fbrUserAccountInfoDocumentsServices.findAllActiveRecordsByUserAndDocumentType(assign.getFbrUserAccountInfo().getUser(),"Cnic");
            List<FbrUserAccountInfoDocumentsBean> fbrUserAccountInfoDocumentsBeanListCnic = new ArrayList<>();
            if (fbrUserAccountInfoDocumentsListCnic != null && fbrUserAccountInfoDocumentsListCnic.size() > 0) {
                fbrUserAccountInfoBean1.setUserEmail(assign.getFbrUserAccountInfo().getUser().getEmailAddress());
                /*fbrUserAccountInfoBean1.setUserName(assign.getFbrUserAccountInfo().getUser().getUserDetail().getFirstName());*/
                fbrUserAccountInfoBean1.setUserName(assign.getFbrUserAccountInfo().getUser().getFullName());
                fbrUserAccountInfoBean1.setUserCnic(assign.getFbrUserAccountInfo().getUser().getCnicNo());
                fbrUserAccountInfoBean1.setUserMobileNo(assign.getFbrUserAccountInfo().getUser().getMobileNo());
                for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsListCnic) {
                    FbrUserAccountInfoDocumentsBean fbrUserAccountInfoDocumentsBean = new FbrUserAccountInfoDocumentsBean();

                    fbrUserAccountInfoDocumentsBean.setId(fbrUserAccountInfoDocuments.getId());
                    fbrUserAccountInfoDocumentsBean.setFilename(fbrUserAccountInfoDocuments.getDocumentDescription());

                    fbrUserAccountInfoDocumentsBean.setBase64(Base64.encodeBase64String(Base64Util.readBytesFromFile(staticContentPath + fbrUserAccountInfoDocuments.getDocumentName())));
                    if (fbrUserAccountInfoDocuments.getDocumentFormat() != null) {
                        fbrUserAccountInfoDocumentsBean.setFiletype(fbrUserAccountInfoDocuments.getDocumentFormat());
                    }
                    fbrUserAccountInfoDocumentsBeanListCnic.add(fbrUserAccountInfoDocumentsBean);
                }
                fbrUserAccountInfoBean1.setCnicList(fbrUserAccountInfoDocumentsBeanListCnic);
            }
            List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
            fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean1);

            StatusBean statusBean = new StatusBean(1, "Fbr data found");
            statusBean.setResponse(fbrUserAccountInfoBeanList);

            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/open/update",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<Status> updateOpenNTNAssignedToLawyer(@RequestBody FbrUserAccountInfoBean fbrUserAccountInfoBean){
        try {

            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrNtnNumber())) {return new ResponseEntity<>(new Status(0,"Incomplete Data :: NTN Number"), HttpStatus.OK);}
            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrUsername())) {return new ResponseEntity<>(new Status(0,"Incomplete Data :: Registration Number"), HttpStatus.OK);}
            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPassword())) {return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Password"), HttpStatus.OK);}
            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPin())) {return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Pin"), HttpStatus.OK);}

            User user;
            if (fbrUserAccountInfoBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(fbrUserAccountInfoBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0,"Session Expired"), HttpStatus.OK);
            }

            Assign assign;
            if (fbrUserAccountInfoBean.getAssignId() != null) {
                assign = assignServices.findOneByIdAndActiveStatus(fbrUserAccountInfoBean.getAssignId());
                if (assign == null) {
                    return new ResponseEntity<>(new Status(0,"Error"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0,"Please select properly"), HttpStatus.OK);
            }

            FbrUserAccountInfo fbrUserAccountInfo = assign.getFbrUserAccountInfo();
            if (fbrUserAccountInfo == null) {
                return new ResponseEntity<>(new Status(0, "Error"), HttpStatus.OK);
            }
            fbrUserAccountInfo.setFbrNtnNumber(fbrUserAccountInfoBean.getFbrNtnNumber());
            fbrUserAccountInfo.setFbrUsername(fbrUserAccountInfoBean.getFbrUsername());
            fbrUserAccountInfo.setFbrPassword(fbrUserAccountInfoBean.getFbrPassword());
            fbrUserAccountInfo.setFbrPin(fbrUserAccountInfoBean.getFbrPin());

            fbrUserAccountInfoServices.updateFbrUserAccountInfoToLawyerCloseStatus(fbrUserAccountInfo);

            // sent email and notifications
            EmailSender.sendEmail(EmailUtil.emailUponSuccessfulSubmissionOfReturnNTN(fbrUserAccountInfo.getUser().getFullName()),"Your NTN has been successfully created!",fbrUserAccountInfo.getUser().getEmailAddress());
            Notification notification = new Notification();
            notification.setArchiveFlag(0);
            notification.setReadFlag(0);
            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
            notification.setToUser(fbrUserAccountInfo.getUser().getId());
            notification.setNotificationTitle("Your NTN has been created!");
            notification.setNotificationDescription("Your NTN has been successfully created" );
            notificationServices.save(notification);


            return new ResponseEntity<>(new Status(1, "Successfull"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Exception"), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/close",produces = "application/json",method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getAllCloseNTNAssignedToLawyer(@RequestBody InputBean inputBean){
        try {

            User user;
            if (inputBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired"), HttpStatus.OK);
            }

            List<Assign> assignList = assignServices.findAllCloseNTNAssignByLawyerAndActiveStatus(user);
            StatusBean statusBean = new StatusBean(1, "Successfully");
            List<AssignBean> assignBeans = new ArrayList<>();
            if (assignList != null && assignList.size() > 0) {
                for (Assign assign : assignList) {
                    AssignBean assignBean = new AssignBean();

                    assignBean.setId(assign.getId());
                    assignBean.setUserId(assign.getFbrUserAccountInfo().getUser().getId());
                    assignBean.setUserEmail(assign.getFbrUserAccountInfo().getUser().getEmailAddress());
                    assignBean.setUserName(assign.getFbrUserAccountInfo().getUser().getFullName());

                    assignBeans.add(assignBean);
                }
            }
            statusBean.setResponse(assignBeans);
            return new ResponseEntity<>(statusBean, HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
        }
    }
}
