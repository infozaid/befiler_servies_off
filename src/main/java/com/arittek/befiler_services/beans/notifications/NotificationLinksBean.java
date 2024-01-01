package com.arittek.befiler_services.beans.notifications;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class NotificationLinksBean {
    private Integer id;
    private String link;
    private String notificationType;
    private String description;
    private Integer status;
}
