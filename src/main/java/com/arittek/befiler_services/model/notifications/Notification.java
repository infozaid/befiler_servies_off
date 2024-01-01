package com.arittek.befiler_services.model.notifications;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "notification_title")
    String notificationTitle;

    @Column(name = "notification_description")
    String notificationDescription;

    @Column(name = "notification_type")
    String notificationType;

    @Column(name = "attachment_link")
    String attachmentLink;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "attachment_text")
    String attachmentText;

    @Column(name = "link")
    String link;

    @Column(name = "read_flag")
    Integer readFlag ;

    @Column(name = "archive_flag")
    Integer archiveFlag ;

    @Column(name = "to_user")
    Integer toUser ;

    @Column(name = "from_user")
    Integer fromUser ;

    transient String toUserName ;
    transient String userType ;

    @Column(name = "create_date")
    Timestamp createDate ;

    }
