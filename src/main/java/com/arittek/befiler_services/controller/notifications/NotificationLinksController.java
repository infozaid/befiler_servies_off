package com.arittek.befiler_services.controller.notifications;


import com.arittek.befiler_services.beans.notifications.NotificationLinksBean;
import com.arittek.befiler_services.model.notifications.NotificationLinks;
import com.arittek.befiler_services.services.notifications.NotificationLinksService;
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
@RequestMapping(value = "/notificationLinks")
public class NotificationLinksController {


    NotificationLinksService notificationLinksService;

    @Autowired
    public NotificationLinksController(NotificationLinksService notificationLinksService) {
        this.notificationLinksService = notificationLinksService;
    }


    @RequestMapping(value = "getNotificationLinksList", produces = "application/json", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<NotificationLinksBean>> getUserTypeList() throws Exception {
        List<NotificationLinksBean> notificationLinksBeanArrayList = new ArrayList<>();

        List<NotificationLinks> notificationLinksList = (List<NotificationLinks>) notificationLinksService.findAllByStatus(1);

        for (NotificationLinks notificationLinks : notificationLinksList) {
            NotificationLinksBean notificationTypeBean = new NotificationLinksBean();

            notificationTypeBean.setId(notificationLinks.getId());

            if (notificationLinks.getLink() != null) {
                notificationTypeBean.setLink(notificationLinks.getLink());
            }
            if (notificationLinks.getNotificationType() != null) {
                notificationTypeBean.setNotificationType(notificationLinks.getNotificationType());
            }
            if (notificationLinks.getStatus() != null) {
                notificationTypeBean.setStatus(notificationLinks.getStatus());
            }
            notificationLinksBeanArrayList.add(notificationTypeBean);
        }
        return new ResponseEntity<>(notificationLinksBeanArrayList, HttpStatus.OK);
    }
}
