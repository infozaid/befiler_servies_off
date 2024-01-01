package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PaymentMethod paymentMethod) {
        return paymentMethod.getId();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(Integer databaseValue) {
        return PaymentMethod.getPaymentMethod(databaseValue);
    }
}
