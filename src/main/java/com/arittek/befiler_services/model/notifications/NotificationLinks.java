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
@Table(name = "notification_links")
public class NotificationLinks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "link")
    private String link;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;


}
