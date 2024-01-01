package com.arittek.befiler_services.model.notifications;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "dynamic_notification")
public class DynamicNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "notification_title")
    String notificationTitle;

    @Column(name = "notification_description")
    String notificationDescription;

    @Column(name = "notificationType")
    String notificationType;

    @Column(name = "link")
    String link;

    @Column(name = "status")
    Integer status;

}
