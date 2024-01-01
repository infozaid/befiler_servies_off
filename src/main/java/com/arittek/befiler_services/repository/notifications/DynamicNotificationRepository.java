package com.arittek.befiler_services.repository.notifications;

import com.arittek.befiler_services.model.notifications.DynamicNotification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DynamicNotificationRepository extends CrudRepository<DynamicNotification, Integer> {

    List<DynamicNotification> findAllByNotificationTypeAndStatus(String NotificationType, Integer status);
    List<DynamicNotification> findAllByStatus(Integer status);
    DynamicNotification findByNotificationTypeAndStatus(String NotificationType, Integer status);

}
