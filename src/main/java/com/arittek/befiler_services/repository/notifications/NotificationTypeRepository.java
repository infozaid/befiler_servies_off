package com.arittek.befiler_services.repository.notifications;


import com.arittek.befiler_services.model.notifications.NotificationType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationTypeRepository extends CrudRepository<NotificationType, Integer> {

    List<NotificationType> findAllByStatus(Integer status);
    List<NotificationType> findAllByTypeAndStatus(String notificationType, Integer status);
    NotificationType findByType(String notificationType);

}