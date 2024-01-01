package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.DynamicNotification;

import java.util.List;

public interface DynamicNotificationServices {

    boolean save(DynamicNotification dynamicNotification);

    boolean update(DynamicNotification dynamicNotification);

    public List<DynamicNotification> findAll();

    public DynamicNotification findOne(Integer id);

    List<DynamicNotification> findAllByNotificationTypeAndStatus(String NotificationType, Integer status);

    List<DynamicNotification> findAllByStatus(Integer status);

    DynamicNotification findByNotificationTypeAndStatus(String NotificationType, Integer status);

}
