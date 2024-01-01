package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AssignTypeConverter  implements AttributeConverter<AssignType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AssignType attribute) {
        return attribute.getId();
    }

    @Override
    public AssignType convertToEntityAttribute(Integer dbData) {
        return AssignType.getAssignType(dbData);
    }
}
