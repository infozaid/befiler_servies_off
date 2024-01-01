package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FbrUserAccountInfoStatusConverter implements AttributeConverter<FbrUserAccountInfoStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FbrUserAccountInfoStatus fbrUserAccountInfoStatus) {
        return fbrUserAccountInfoStatus.getId();
    }

    @Override
    public FbrUserAccountInfoStatus convertToEntityAttribute(Integer databaseValue) {
        return FbrUserAccountInfoStatus.getFbrUserAccountInfoStatus(databaseValue);
    }
}
