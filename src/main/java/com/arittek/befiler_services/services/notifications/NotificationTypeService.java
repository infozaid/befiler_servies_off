package com.arittek.befiler_services.services.notifications;


import com.arittek.befiler_services.model.notifications.NotificationType;

import java.util.List;

public interface NotificationTypeService {

    boolean save(NotificationType notificationType);

    boolean update(NotificationType notificationType);

    public List<NotificationType> findAll();

    public NotificationType findOne(Integer id);

    NotificationType findByType(String notificationType);

    List<NotificationType> findAllByStatus(Integer status);

    List<NotificationType> findAllByTypeAndStatus(String notificationType, Integer status);
}
