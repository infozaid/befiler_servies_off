package com.arittek.befiler_services.repository.notifications;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.notifications.EmailNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmailNotificationRepository extends CrudRepository<EmailNotification, Integer> {

    public List<EmailNotification> findAllByStatus(AppStatus status);
}



