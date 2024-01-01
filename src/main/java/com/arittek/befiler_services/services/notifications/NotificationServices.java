package com.arittek.befiler_services.services.notifications;


import com.arittek.befiler_services.model.notifications.Notification;
import java.util.List;

public interface NotificationServices {

    boolean save(Notification notification);

    boolean update(Notification notification);

    public List<Notification> findAll();

    public Notification findOne(Integer id);

    List<Notification> findAllByReadFlagAndToUser(int readFlag, Integer userId);

    List<Notification> findAllByArchiveFlagAndToUser(int archiveFlag, Integer userId);

    List<Notification> findAllByArchiveFlagAnAndReadFlagAndToUser(int archiveFlag, int readFlag, Integer userId);

    Notification findByToUserAndNotificationType(Integer userId, String notificationType);

    List<Notification> findAllByArchiveFlag(int archiveFlag);
}
