package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class NotificationBean implements Serializable {

    Integer id;

    String notificationTitle;

    String notificationDescription;

    Base64Bean attachmentLink;

    String fileName;
    String fromUserName;

    String attachmentText;

    Integer readFlag ;

    Integer archiveFlag ;

    Integer toUser ;
    Integer userType ;

    Integer fromUser ;

    Timestamp createDate ;


}


