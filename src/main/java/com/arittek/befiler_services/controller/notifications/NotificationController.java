package com.arittek.befiler_services.controller.notifications;

import com.arittek.befiler_services.beans.Base64Bean;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.UserLoginBean;
import com.arittek.befiler_services.beans.notifications.NotificationBean;
import com.arittek.befiler_services.beans.notifications.NotificationInfo;
import com.arittek.befiler_services.beans.userModule.RoleBean;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import com.arittek.befiler_services.services.user.role_permission.RoleService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(value = "/notification")
public class NotificationController {


    private RoleService roleService;
    private UsersServices usersServices;
    private NotificationServices notificationServices;

    @Value("${befiler.url}")
    private String serverUrl;

    @Value("${static.content.notification.path}")
    private String staticContentNotificationPath;

    @Autowired
    public NotificationController(RoleService roleService, UsersServices usersServices, NotificationServices notificationServices) {
        this.roleService = roleService;
        this.usersServices = usersServices;
        this.notificationServices = notificationServices;
    }

    @RequestMapping(value = "/insert", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> insertNotification(@RequestBody NotificationBean notificationBean) throws IOException {
        try {
            Notification notification = new Notification();
            if (notificationBean.getAttachmentLink() != null) {

                Base64Bean base64Bean = notificationBean.getAttachmentLink();
                String fileName = base64Bean.getFilename();
                String fileUrl = staticContentNotificationPath + fileName;
                byte[] imageByte = Base64.decodeBase64(base64Bean.getBase64());
                BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                imageOutFile.write(imageByte);
                imageOutFile.close();
                notification.setAttachmentLink("notification/" + fileName);
                notification.setFileName(fileName);
            }
            notification.setArchiveFlag(0);
            notification.setReadFlag(0);
            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
            if (notificationBean.getToUser() != null) {
                notification.setToUser(notificationBean.getToUser());
            }
            if (notificationBean.getFromUser() != null) {
                notification.setFromUser(notificationBean.getFromUser());
            }
            if (notificationBean.getAttachmentText() != null) {
                notification.setAttachmentText(notificationBean.getAttachmentText());
            }
            if (notificationBean.getNotificationDescription() != null) {
                notification.setNotificationDescription(notificationBean.getNotificationDescription());
            }
            if (notificationBean.getNotificationTitle() != null) {
                notification.setNotificationTitle(notificationBean.getNotificationTitle());
            }

            boolean status = notificationServices.save(notification);
            if (status) {
                return new ResponseEntity<>(new StatusBean(1, "Notification is inserted successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(0, "Notification is inserted successfully"), HttpStatus.OK);

    }

    // create notification assign to role

    @RequestMapping(value = "/assignByRoleID", produces = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> insertNotificationForRole(@RequestBody NotificationBean notificationBean) {

        if (notificationBean != null && notificationBean.getToRole() != null) {
            Role role = roleService.findOne(notificationBean.getToRole());
            if (role != null) {
                Set<User> userList = (Set<User>) role.getUserList();
                try {
                    for (User user : userList) {
                        User user1 = usersServices.findOneByIdAndStatus(user.getId(), UserStatus.ACTIVE);
                        if (user1 != null) {
                            Notification notification = new Notification();
                            if (notificationBean.getAttachmentLink() != null) {
                                Base64Bean base64Bean = notificationBean.getAttachmentLink();
                                String fileName = base64Bean.getFilename();
                                String fileUrl = staticContentNotificationPath + fileName;
                                byte[] imageByte = Base64.decodeBase64(base64Bean.getBase64());
                                BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                                imageOutFile.write(imageByte);
                                imageOutFile.close();
                                notification.setAttachmentLink("notification/" + fileName);
                                notification.setFileName(fileName);
                            }
                                notification.setArchiveFlag(0);
                                notification.setReadFlag(0);
                                notification.setCreateDate(CommonUtil.getCurrentTimestamp());

                                notification.setToUser(user1.getId());

                                if (notificationBean.getFromUser() != null) {
                                    notification.setFromUser(notificationBean.getFromUser());
                                }
                                if (notificationBean.getAttachmentText() != null) {
                                    notification.setAttachmentText(notificationBean.getAttachmentText());
                                }
                                if (notificationBean.getNotificationDescription() != null) {
                                    notification.setNotificationDescription(notificationBean.getNotificationDescription());
                                }
                                if (notificationBean.getNotificationTitle() != null) {
                                    notification.setNotificationTitle(notificationBean.getNotificationTitle());
                                }


                            boolean status = notificationServices.save(notification);
//                            System.out.println("Status : "+status+ "\t User: "+user1.getEmailAddress());
                            /*
                            if (status) {
                                return new ResponseEntity<>(new StatusBean(1, "Notification is inserted successfully"), HttpStatus.OK);
                            } else {
                                return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
                            }
*/
                        } // user
                    }

                    return new ResponseEntity<>(new StatusBean(1, "Notification is inserted successfully"), HttpStatus.OK);

                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
                }
            }   // for each list of users

        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Invalid Role!"), HttpStatus.OK);
    }




    @RequestMapping(value = "update", produces = "application/json", consumes = "application/json", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<StatusBean> updateNotification(@RequestBody Notification notification) {
        Notification updateNotification = notificationServices.findOne(notification.getId());
        boolean status = false;
        if (updateNotification != null) {
            if (notification.getReadFlag() != null) {
                updateNotification.setReadFlag(notification.getReadFlag());
            }
            if (notification.getArchiveFlag() != null) {
                updateNotification.setArchiveFlag(notification.getArchiveFlag());
            }
            if (notification.getAttachmentLink() != null) {

                updateNotification.setAttachmentLink(serverUrl + notification.getAttachmentLink());
            }
            if (notification.getAttachmentText() != null) {
                updateNotification.setAttachmentLink(notification.getAttachmentText());
            }
            if (notification.getToUser() != null) {
                updateNotification.setToUser(notification.getToUser());
            }
            if (notification.getFromUser() != null) {
                updateNotification.setFromUser(notification.getFromUser());
            }
            if (notification.getCreateDate() != null) {
                updateNotification.setCreateDate(notification.getCreateDate());
            }
            status = notificationServices.update(updateNotification);
        }
        if (status) {
            return new ResponseEntity<>(new StatusBean(1, "Notification is update successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
        }

    }

    @RequestMapping(value = "updateAllNotification", produces = "application/json", consumes = "application/json", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<StatusBean> updateAllNotification(@RequestBody List<Notification> notificationArrayList) {
        ArrayList<StatusBean> statusBeanArrayList = new ArrayList<>();
        for (Notification notification : notificationArrayList) {
            boolean status = false;
            Notification updateNotification = notificationServices.findOne(notification.getId());
            if (notification.getReadFlag() != null) {
                updateNotification.setReadFlag(notification.getReadFlag());
            }
            if (notification.getArchiveFlag() != null) {
                updateNotification.setArchiveFlag(notification.getArchiveFlag());
            }
            if (notification.getAttachmentLink() != null) {
                updateNotification.setAttachmentLink(serverUrl + notification.getAttachmentLink());
            }
            if (notification.getAttachmentText() != null) {
                updateNotification.setAttachmentLink(notification.getAttachmentText());
            }
            if (notification.getToUser() != null) {
                updateNotification.setToUser(notification.getToUser());
            }
            if (notification.getFromUser() != null) {
                updateNotification.setFromUser(notification.getFromUser());
            }
            if (notification.getCreateDate() != null) {
                updateNotification.setCreateDate(notification.getCreateDate());
            }
            status = notificationServices.update(updateNotification);
            if (status) {
                statusBeanArrayList.add(new StatusBean(0, "success"));
            } else {
                statusBeanArrayList.add(new StatusBean(1, "failure"));
            }
        }
        StatusBean statusBean = new StatusBean(0, "updated records");
        statusBean.setResponse(statusBeanArrayList);


        return new ResponseEntity<>(statusBean, HttpStatus.OK);

    }

    @RequestMapping(value = "getNotificationByUserID", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<Notification>> getNotificationByUserID(@RequestParam("userID") Integer userID) {
        List<Notification> allByReadFlagAndToUser = new LinkedList<>();
        List<Notification> responseNotifications = new LinkedList<>();
        try {
            allByReadFlagAndToUser = notificationServices.findAllByArchiveFlagAndToUser(0, userID);
            for (Notification notification : allByReadFlagAndToUser) {
                notification.setAttachmentLink(serverUrl + notification.getAttachmentLink());
                if (notification.getFromUser() != null) {
                    User fromUser = usersServices.findOneByIdAndStatus(notification.getFromUser(), UserStatus.ACTIVE);
                    /*if (fromUser != null && fromUser.getUserType() != null && fromUser.getUserType().getType() != null && !fromUser.getUserType().getType().isEmpty()) {
                        notification.setUserType(fromUser.getUserType().getType());
                    }*/
                    if (fromUser != null && fromUser.getRoles() != null && fromUser.getRoles().size() > 0) {
                        Iterator iterator = fromUser.getRoles().iterator();
                        notification.setUserType(((Role)iterator.next()).getDisplay_name());
                    } else {
                        notification.setUserType("Befiler");
                    }
                }
                System.out.println("Create Date :::  " + notification.getCreateDate());
                responseNotifications.add(notification);
            }
        } catch (Exception e) {

        }
        return new ResponseEntity<>(responseNotifications, HttpStatus.OK);
    }

   /* //

    @RequestMapping(value = "getNotificationByRoleID", produces = "application/json", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<List<Notification>> getNotificationByRoleID(@RequestParam("roleID") int roleID) {
        List<Notification> allByReadFlagAndToUser = new LinkedList<>();
        List<Notification> responseNotifications = new LinkedList<>();

        Role role = roleService.findOne(roleID);
        if (role != null) {
            List<User> userList = (List<User>) role.getUserList();
            for (User user : userList) {
                try {
                    allByReadFlagAndToUser = notificationServices.findAllByArchiveFlagAndToUser(0, user.getId());
                    for (Notification notification : allByReadFlagAndToUser) {
                        notification.setAttachmentLink(serverUrl + notification.getAttachmentLink());
                        if (notification.getFromUser() != null) {
                            User fromUser = usersServices.findOneByIdAndStatus(notification.getFromUser(), UserStatus.ACTIVE);
                            if (fromUser != null && fromUser.getRoles() != null && fromUser.getRoles().size() > 0) {
                                Iterator iterator = fromUser.getRoles().iterator();
                                notification.setUserType(((Role) iterator.next()).getDisplay_name());
                            }
                        }
                        responseNotifications.add(notification);
                    }
                } catch (Exception e) {
                }
            }
        }

        return new ResponseEntity<>(responseNotifications, HttpStatus.OK);
    }
*/

        @RequestMapping(value = "getAllNotifications", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
        ResponseEntity<List<Notification>> getAllNotifications() {
        ArrayList<Notification> notificationBeans = new ArrayList<>();

        List<Notification> allByReadFlagAndToUser = notificationServices.findAllByArchiveFlag(0);

        for (Notification notification : allByReadFlagAndToUser) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(notification.getToUser(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(notification.getToUser(), UserStatus.ACTIVE);
                if (user != null) {
                    notification.setToUserName(user.getFullName());
                    if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
                        Iterator iterator = user.getRoles().iterator();
                        notification.setUserType(((Role)iterator.next()).getDisplay_name());
                    }
                    /*notification.setUserType(user.getUserType().getType());*/
                    notification.setAttachmentLink(serverUrl + notification.getAttachmentLink());
                }
                notificationBeans.add(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(notificationBeans, HttpStatus.OK);
    }




    /*@RequestMapping(value = "/getUserTypeList", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<UserTypeBean>> getUserTypeList() throws Exception {
        List<UserTypeBean> userTypeBeans = new ArrayList<>();

        List<UserType> userTypeList = (List<UserType>) userTypeRepository.findAll();
        System.out.println("userTypeList: " + userTypeList.size());
        userTypeList.remove(usersServices.findUserTypeById(2));

        for (UserType userType : userTypeList) {
            UserTypeBean userTypeBean = new UserTypeBean();
            userTypeBean.setId(userType.getId());
            userTypeBean.setTypeDescription(userType.getTypeDescription());
            userTypeBeans.add(userTypeBean);
        }
        System.out.println("UserTypeBean: " + userTypeBeans.size());

        return new ResponseEntity<>(userTypeBeans, HttpStatus.OK);
    }*/

    /*@RequestMapping(value = "getUserList", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<UserLoginBean>> getUserList(@RequestParam("userType") int userType) throws Exception {
        List<UserLoginBean> userList = new ArrayList<>();

        List<UserType> userTypeList = new ArrayList<>();
        if (userType == 1) {
            userTypeList.add(usersServices.findUserTypeById(1));
            userTypeList.add(usersServices.findUserTypeById(2));
        } else {
            userTypeList.add(usersServices.findUserTypeById(userType));
        }

        List<UserStatus> userStatusList = new ArrayList<>();
        *//*userStatusList.add(usersServices.findUserStatusById(0));
        userStatusList.add(usersServices.findUserStatusById(1));*//*
        userStatusList.add(UserStatus.DEACTIVE);
        userStatusList.add(UserStatus.ACTIVE);

        List<User> userList1 = usersServices.findAllByStatusInAndUserTypeIn(userStatusList, userTypeList);
        if (userList1.size() > 0) {
            for (User user : userList1) {
                UserLoginBean bean = new UserLoginBean();
                bean.setId(user.getId());
                bean.setStatus(user.getStatus().toString());
                bean.setCnic(user.getCnicNo());
                bean.setUserName(user.getUserDetail().getFirstName());
                userList.add(bean);
            }
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }*/

    @RequestMapping(value = "/getRoles", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StatusBean> getRoles() throws Exception {
        try {

            ArrayList<Role> roleList = (ArrayList) roleService.findAllByActive();
            if (roleList != null) {
                ArrayList<RoleBean> roleBeanList = new ArrayList<>();
                for (Role role : roleList) {
                    RoleBean roleBean = new RoleBean();
                    roleBean.setId(role.getId());
                    if (role.getName() != null) {
                        roleBean.setName(role.getName());
                    }
                    if (role.getDescription() != null) {
                        roleBean.setDescription(role.getDescription());
                    }
                    roleBeanList.add(roleBean);
                }
                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(roleBeanList);

                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getUsersByRoleId", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<StatusBean> getUserList(@RequestParam("roleId") Integer roleId) throws Exception {
        try {
            Role role = roleService.findOne(roleId);
            if (role == null) {
                return new ResponseEntity<>(new StatusBean(0, "Can't find role"), HttpStatus.OK);
            }

            Set<User> userList = role.getUserList();
            if (userList != null && userList.size() > 0) {
                List<UserLoginBean> userLoginBeanList = new ArrayList<>();
                for (User user : userList) {
                    if (user.getStatus() != null && (user.getStatus() == UserStatus.ACTIVE || user.getStatus() == UserStatus.DEACTIVE)) {
                        UserLoginBean userLoginBean = new UserLoginBean();
                        userLoginBean.setId(user.getId());
                        userLoginBean.setStatus(user.getStatus().toString());
                        userLoginBean.setCnic(user.getCnicNo());
                        userLoginBean.setUserName(user.getFullName());
                        userLoginBeanList.add(userLoginBean);
                    }
                }
                StatusBean statusBean = new StatusBean(1, "Successfull");
                statusBean.setResponse(userLoginBeanList);
                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @RequestMapping(value = "getNotificationInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<NotificationInfo> getNotificationInfo(@RequestParam("userID") Integer userID) {
        NotificationInfo notificationInfo = new NotificationInfo();
        try {
            List<Notification> allByReadFlagAndToUser = notificationServices.findAllByArchiveFlagAnAndReadFlagAndToUser(0, 0, userID);
            System.out.println("userID: " + userID + " <> Size: " + allByReadFlagAndToUser.size());
            if (allByReadFlagAndToUser != null) {
                notificationInfo.setCounter(allByReadFlagAndToUser.size());
                if (allByReadFlagAndToUser.size() > 3) {
                    notificationInfo.setNotificationList(allByReadFlagAndToUser.subList(0, 3));
                } else {
                    notificationInfo.setNotificationList(allByReadFlagAndToUser);
                }
            }
            List<Notification> notificationList = notificationInfo.getNotificationList();
            for (Notification notification : notificationList) {
                notification.setAttachmentLink(serverUrl + notification.getAttachmentLink());

            }
            notificationInfo.setNotificationList(notificationList);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        }
        return new ResponseEntity<>(notificationInfo, HttpStatus.OK);
    }

}
