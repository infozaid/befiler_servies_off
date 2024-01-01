package com.arittek.befiler_services.controller.notifications;

import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.notifications.NotificationBean;
import com.arittek.befiler_services.model.notifications.DynamicNotification;
import com.arittek.befiler_services.services.notifications.DynamicNotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/dynamicNotification")
public class DynamicNotificationController {


    private DynamicNotificationServices dynamicNotificationServices;

    @Autowired
    public DynamicNotificationController(DynamicNotificationServices dynamicNotificationServices) {
        this.dynamicNotificationServices = dynamicNotificationServices;
    }

    @RequestMapping(value = "insert", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> insertNotification(@RequestBody NotificationBean notificationBean) throws IOException {
        try {

            DynamicNotification dynamicNotification = new DynamicNotification();

            if (notificationBean.getNotificationTitle() == null || notificationBean.getNotificationDescription() == null || notificationBean.getNotificationType() == null ||
                    notificationBean.getNotificationType().isEmpty() || notificationBean.getNotificationTitle().isEmpty() || notificationBean.getNotificationDescription().isEmpty()) {
                return new ResponseEntity<>(new StatusBean(0, "Please fill all required fields!"), HttpStatus.OK);
            }
            if (notificationBean.getNotificationTitle() != null) {
                dynamicNotification.setNotificationTitle(notificationBean.getNotificationTitle());
            }
            if (notificationBean.getNotificationDescription() != null) {
                dynamicNotification.setNotificationDescription(notificationBean.getNotificationDescription());
            }
            if (notificationBean.getLink() != null) {
                dynamicNotification.setLink(notificationBean.getLink());
            }
            if (notificationBean.getNotificationType() != null) {
                dynamicNotification.setNotificationType(notificationBean.getNotificationType());
            }
            dynamicNotification.setStatus(1);

            boolean status = dynamicNotificationServices.save(dynamicNotification);
            if (status) {
                return new ResponseEntity<>(new StatusBean(1, "Dynamic Notification is inserted successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(0, "Notification is inserted successfully"), HttpStatus.OK);

    }


    @RequestMapping(value = "delete", produces = "application/json", consumes = "application/json", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<StatusBean> deletNotification(@RequestBody NotificationBean notificationBean) throws IOException {
        try {

            if (notificationBean.getId() == null) {
                return new ResponseEntity<>(new StatusBean(0, "Dynamic notification not found!!"), HttpStatus.OK);
            }
            DynamicNotification updateNotification = dynamicNotificationServices.findOne(notificationBean.getId());
            boolean status = false;
            if (updateNotification != null) {
                updateNotification.setStatus(0);
                status = dynamicNotificationServices.update(updateNotification);
            }
            if (status) {
                return new ResponseEntity<>(new StatusBean(1, "Dynamic Notification is update successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(0, "Notification is inserted successfully"), HttpStatus.OK);

    }

    @RequestMapping(value = "update", produces = "application/json", consumes = "application/json", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<StatusBean> updateNotification(@RequestBody NotificationBean notificationBean) {
        if (notificationBean.getId() == null) {
            return new ResponseEntity<>(new StatusBean(0, "Dynamic notification not found!!"), HttpStatus.OK);
        }
        DynamicNotification updateNotification = dynamicNotificationServices.findOne(notificationBean.getId());
        boolean status = false;
        if (updateNotification != null) {
            if (notificationBean.getNotificationTitle() != null) {
                updateNotification.setNotificationTitle(notificationBean.getNotificationTitle());
            }
            if (notificationBean.getNotificationDescription() != null) {
                updateNotification.setNotificationDescription(notificationBean.getNotificationDescription());
            }
            if (notificationBean.getNotificationType() != null) {
                updateNotification.setNotificationType(notificationBean.getNotificationType());
            }

            if (notificationBean.getLink() != null) {
                updateNotification.setLink(notificationBean.getLink());
            }
            if (notificationBean.getStatus() != null) {
                updateNotification.setStatus(notificationBean.getStatus());
            }

            status = dynamicNotificationServices.update(updateNotification);
        }
        if (status) {
            return new ResponseEntity<>(new StatusBean(1, "Dynamic Notification is update successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new StatusBean(0, "Error occurred!"), HttpStatus.OK);
        }

    }

    @RequestMapping(value = "view", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<NotificationBean>> getDynamicNotificationList() throws Exception {
        List<NotificationBean> notificationTypeBeanList = new ArrayList<>();

        List<DynamicNotification> dynamicNotificationList = (List<DynamicNotification>) dynamicNotificationServices.findAll();

        for (DynamicNotification dynamicNotification : dynamicNotificationList) {
            NotificationBean notificationTypeBean = new NotificationBean();

            notificationTypeBean.setId(dynamicNotification.getId());

            if (dynamicNotification.getNotificationType() != null) {
                notificationTypeBean.setNotificationType(dynamicNotification.getNotificationType());
            }
            if (dynamicNotification.getLink() != null) {
                notificationTypeBean.setLink(dynamicNotification.getLink());
            }
            if (dynamicNotification.getNotificationTitle() != null) {
                notificationTypeBean.setNotificationTitle(dynamicNotification.getNotificationTitle());
            }
            if (dynamicNotification.getStatus() != null) {
                notificationTypeBean.setStatus(dynamicNotification.getStatus());
            }
            if (dynamicNotification.getNotificationDescription() != null) {
                notificationTypeBean.setNotificationDescription(dynamicNotification.getNotificationDescription());
            }
            notificationTypeBeanList.add(notificationTypeBean);
        }
        return new ResponseEntity<>(notificationTypeBeanList, HttpStatus.OK);
    }

    @RequestMapping(value = "view/{id}", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<NotificationBean> getDynamicNotificationByID(@PathVariable(value = "id") Integer id) throws Exception {

        DynamicNotification dynamicNotification = dynamicNotificationServices.findOne(id);

        NotificationBean notificationTypeBean = new NotificationBean();

        notificationTypeBean.setId(dynamicNotification.getId());

        if (dynamicNotification.getNotificationType() != null) {
            notificationTypeBean.setNotificationType(dynamicNotification.getNotificationType());
        }
        if (dynamicNotification.getNotificationTitle() != null) {
            notificationTypeBean.setNotificationTitle(dynamicNotification.getNotificationTitle());
        }

        if (dynamicNotification.getLink() != null) {
            notificationTypeBean.setLink(dynamicNotification.getLink());
        }
        if (dynamicNotification.getStatus() != null) {
            notificationTypeBean.setStatus(dynamicNotification.getStatus());
        }
        if (dynamicNotification.getNotificationDescription() != null) {
            notificationTypeBean.setNotificationDescription(dynamicNotification.getNotificationDescription());
        }
        return new ResponseEntity<>(notificationTypeBean, HttpStatus.OK);
    }

}
