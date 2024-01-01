package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class EmTransactionTypeConverter implements AttributeConverter<EmTransactionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EmTransactionType emTransactionType) {
        if (emTransactionType == null)
            return null;
	return emTransactionType.getId();
    }

    @Override
    public EmTransactionType convertToEntityAttribute(Integer databaseValue) {
	return EmTransactionType.getEmDocumentType(databaseValue);
    }
}
