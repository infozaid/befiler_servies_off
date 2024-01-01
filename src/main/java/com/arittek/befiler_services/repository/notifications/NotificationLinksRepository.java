package com.arittek.befiler_services.repository.notifications;

import com.arittek.befiler_services.model.notifications.NotificationLinks;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationLinksRepository extends CrudRepository<NotificationLinks, Integer> {

    List<NotificationLinks> findAllByStatus(Integer status);

}
