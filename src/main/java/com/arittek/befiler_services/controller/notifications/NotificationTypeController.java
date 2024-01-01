package com.arittek.befiler_services.controller.notifications;


import com.arittek.befiler_services.beans.notifications.NotificationTypeBean;
import com.arittek.befiler_services.model.notifications.NotificationType;
import com.arittek.befiler_services.services.notifications.NotificationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/notificationType")
public class NotificationTypeController {

    NotificationTypeService notificationTypeService;


    @Autowired
    public NotificationTypeController(NotificationTypeService notificationTypeService) {
        this.notificationTypeService = notificationTypeService;
    }


    @RequestMapping(value = "getNotificationTypeList", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<NotificationTypeBean>> getUserTypeList() throws Exception {
        List<NotificationTypeBean> notificationTypeBeanList = new ArrayList<>();

        List<NotificationType> notificationTypeList = (List<NotificationType>) notificationTypeService.findAll();

        for (NotificationType notificationType : notificationTypeList) {
            NotificationTypeBean notificationTypeBean = new NotificationTypeBean();

            notificationTypeBean.setId(notificationType.getId());

            if (notificationType.getType() != null) {
                notificationTypeBean.setType(notificationType.getType());
            }
            if (notificationType.getStatus() != null) {
                notificationTypeBean.setStatus(notificationType.getStatus());
            }
            notificationTypeBeanList.add(notificationTypeBean);
        }
        return new ResponseEntity<>(notificationTypeBeanList, HttpStatus.OK);
    }
}
