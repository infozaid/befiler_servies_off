package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AppStatusConverter implements AttributeConverter<AppStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AppStatus appStatus) {
        return appStatus.getId();
    }

    @Override
    public AppStatus convertToEntityAttribute(Integer databaseValue) {
        return AppStatus.getAppStatus(databaseValue);
    }
}
