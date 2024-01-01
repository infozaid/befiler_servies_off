package com.arittek.befiler_services.model.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProductTypeConverter implements AttributeConverter<ProductType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProductType productType) {
        return productType.getId();
    }

    @Override
    public ProductType convertToEntityAttribute(Integer databaseValue) {
        return ProductType.getProductType(databaseValue);
    }
}
