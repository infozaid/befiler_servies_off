package com.arittek.befiler_services.services.notifications;

import com.arittek.befiler_services.model.notifications.NotificationType;
import com.arittek.befiler_services.repository.notifications.NotificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationTypeServiceImpl implements NotificationTypeService {


    @Autowired
    NotificationTypeRepository notificationTypeRepository;


    @Override
    public boolean save(NotificationType notificationType) {
        if (notificationTypeRepository.findByType(notificationType.getType()) != null) {
            notificationTypeRepository.save(notificationType);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(NotificationType notificationType) {
        if (notificationType.getType() != null) {
            notificationTypeRepository.save(notificationType);
            return true;
        }
        return false;
    }

    @Override
    public List<NotificationType> findAll() {
        return (List<NotificationType>) notificationTypeRepository.findAll();
    }

    @Override
    public NotificationType findOne(Integer id) {
        return notificationTypeRepository.findById(id).orElse(null);
    }

    @Override
    public NotificationType findByType(String notificationType) {
        return notificationTypeRepository.findByType(notificationType);
    }

    @Override
    public List<NotificationType> findAllByStatus(Integer status) {
        return notificationTypeRepository.findAllByStatus(status);
    }

    @Override
    public List<NotificationType> findAllByTypeAndStatus(String notificationType, Integer status) {
        return notificationTypeRepository.findAllByTypeAndStatus(notificationType, status);
    }


}
