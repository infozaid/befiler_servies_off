package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.DynamicNotification;
import com.arittek.befiler_services.repository.notifications.DynamicNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicNotificationServicesImpl implements DynamicNotificationServices {

    @Autowired
    DynamicNotificationRepository dynamicNotificationRepository;

    @Override
    public boolean save(DynamicNotification dynamicNotification) {
        if (dynamicNotification.getNotificationTitle() != null) {
            dynamicNotificationRepository.save(dynamicNotification);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(DynamicNotification dynamicNotification) {
        if (dynamicNotification.getNotificationTitle() != null) {
            dynamicNotificationRepository.save(dynamicNotification);
            return true;
        }
        return false;
    }

    @Override
    public List<DynamicNotification> findAll() {
        return (List<DynamicNotification>) dynamicNotificationRepository.findAll();
    }

    @Override
    public DynamicNotification findOne(Integer id) {
        return dynamicNotificationRepository.findById(id).orElse(null);
    }

    @Override
    public List<DynamicNotification> findAllByNotificationTypeAndStatus(String NotificationType, Integer status) {
        return dynamicNotificationRepository.findAllByNotificationTypeAndStatus(NotificationType, status);
    }

    @Override
    public List<DynamicNotification> findAllByStatus(Integer status) {
        return dynamicNotificationRepository.findAllByStatus(status);
    }

    @Override
    public DynamicNotification findByNotificationTypeAndStatus(String NotificationType, Integer status) {
        return dynamicNotificationRepository.findByNotificationTypeAndStatus(NotificationType,status);
    }
}
