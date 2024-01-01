package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SettingPaymentStatusConverter implements AttributeConverter<SettingPaymentStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SettingPaymentStatus settingPaymentStatus) {
        return settingPaymentStatus.getId();
    }

    @Override
    public SettingPaymentStatus convertToEntityAttribute(Integer databaseValue) {
        return SettingPaymentStatus.getSettingPaymentStatus(databaseValue);
    }
}
