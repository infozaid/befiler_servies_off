package com.arittek.befiler_services.services.notifications;


import com.arittek.befiler_services.model.notifications.NotificationLinks;
import com.arittek.befiler_services.repository.notifications.NotificationLinksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationLinksServiceImpl implements NotificationLinksService {

    @Autowired
    NotificationLinksRepository notificationLinksRepository;

    @Override
    public List<NotificationLinks> findAllByStatus(Integer status) {
        return notificationLinksRepository.findAllByStatus(status);
    }
}
