package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.repository.user.UserRepository;
import com.arittek.befiler_services.repository.notifications.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServicesImpl implements NotificationServices {


    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public boolean save(Notification notification) {
        if (notification.getToUser() != null) {
            if (userRepository.findById(notification.getToUser()) != null) {
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Notification notification) {
        if (notification.getToUser() != null) {
            if (userRepository.findById(notification.getToUser()) != null) {
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Notification> findAll() {
        return (List<Notification>) notificationRepository.findAll();
    }

    @Override
    public Notification findOne(Integer id) {
        return notificationRepository.findById(id).get();
    }

    @Override
    public List<Notification> findAllByReadFlagAndToUser(int readFlag, Integer userId) {

        return notificationRepository.findAllByReadFlagAndToUser(readFlag, userId);
    }

    @Override
    public List<Notification> findAllByArchiveFlagAndToUser(int archiveFlag, Integer userId) {
//        Pageable pageable = new PageRequest(start,end);
//        List<Notification> notificationList = notificationRepository.findAllByArchiveFlagAndToUserOrderByCreateDateDesc(archiveFlag, userId, pageable);
//        return notificationList;
        return notificationRepository.findAllByArchiveFlagAndToUserOrderByCreateDateDesc(archiveFlag, userId);
    }

    @Override
    public List<Notification> findAllByArchiveFlagAnAndReadFlagAndToUser(int archiveFlag, int readFlag, Integer userId) {
        return notificationRepository.findAllByArchiveFlagAndReadFlagAndToUser(archiveFlag, readFlag, userId);
    }

    @Override
    public Notification findByToUserAndNotificationType(Integer userId, String notificationType) {
        return notificationRepository.findByToUserAndNotificationType(userId,notificationType);
    }

    @Override
    public List<Notification> findAllByArchiveFlag(int archiveFlag) {
        return notificationRepository.findAllByArchiveFlag(archiveFlag);

    }


}
