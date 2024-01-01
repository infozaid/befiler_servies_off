package com.arittek.befiler_services.beans.notifications;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class NotificationTypeBean {

    Integer id;

    String type;

    String description;

    Integer status;
}
