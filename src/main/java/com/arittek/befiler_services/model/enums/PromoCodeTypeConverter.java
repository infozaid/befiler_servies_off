package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PromoCodeTypeConverter implements AttributeConverter<PromoCodeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PromoCodeType promoCodeType) {
        return promoCodeType.getId();
    }

    @Override
    public PromoCodeType convertToEntityAttribute(Integer databaseValue) {
        return PromoCodeType.getPromoCodeType(databaseValue);
    }

}
