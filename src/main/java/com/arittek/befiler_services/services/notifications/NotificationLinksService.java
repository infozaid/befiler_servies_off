package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.NotificationLinks;

import java.util.List;

public interface NotificationLinksService {

    List<NotificationLinks> findAllByStatus(Integer status);

}
