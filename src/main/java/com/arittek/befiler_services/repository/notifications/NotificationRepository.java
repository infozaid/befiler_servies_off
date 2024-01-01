package com.arittek.befiler_services.repository.notifications;

import com.arittek.befiler_services.model.notifications.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {

//    List<Notification> findAllByReadFlagAndToUser(int readFlag, int userId);
    List<Notification> findAllByReadFlagAndToUser(int readFlag, Integer userId);
    List<Notification> findAllByArchiveFlagAndToUserOrderByCreateDateDesc(int archiveFlag, Integer userId);
    List<Notification> findAllByArchiveFlagAndReadFlagAndToUser(int archiveFlag, int readFlag, Integer userId);
    List<Notification> findAllByArchiveFlag(int archiveFlag);
    List<Notification> findAllByToUser(Integer userId);
    Notification findByToUserAndNotificationType(Integer userId, String notificationType);



}
