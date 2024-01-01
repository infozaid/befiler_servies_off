package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;

public class EmailNotificationTypeConverter implements AttributeConverter<EmailNotificationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EmailNotificationType emailNotifyPermission) {
        return emailNotifyPermission.getId();
    }

    @Override
    public EmailNotificationType convertToEntityAttribute(Integer databaseValue) {
        return EmailNotificationType.getEmailNotificationPermission(databaseValue);
    }
}
