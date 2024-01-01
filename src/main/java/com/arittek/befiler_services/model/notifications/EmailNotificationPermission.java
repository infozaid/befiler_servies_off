package com.arittek.befiler_services.model.notifications;

import com.arittek.befiler_services.model.enums.EmailNotificationTypeConverter;
import com.arittek.befiler_services.model.enums.EmailNotificationType;
import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "email_notification_permission")
public class EmailNotificationPermission extends GenericModel{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_notification_id")
    private EmailNotification emailNotification;

    @Convert(converter = EmailNotificationTypeConverter.class)
    private EmailNotificationType emailNotificationType;


}
