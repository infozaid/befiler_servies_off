package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserStatus userStatus) {
        return userStatus.getId();
    }

    @Override
    public UserStatus convertToEntityAttribute(Integer databaseValue) {
        return UserStatus.getUserStatus(databaseValue);
    }
}
