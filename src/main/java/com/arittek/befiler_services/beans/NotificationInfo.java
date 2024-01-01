package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.model.notifications.Notification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class NotificationInfo {
    private List<Notification> notificationList;
    private Integer counter;

}
