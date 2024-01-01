package com.arittek.befiler_services.config.converters;

import javax.persistence.Converter;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Converter
public class StringCryptoConverter extends AbstractCryptoConverter<String> {

    public StringCryptoConverter() {
        this(new CipherInitializer());
    }

    public StringCryptoConverter(CipherInitializer cipherInitializer) {
        super(cipherInitializer);
    }

    @Override
    boolean isNotNullOrEmpty(String attribute) {
        return isNotEmpty(attribute);
    }

    @Override
    String stringToEntityAttribute(String dbData) {
        return dbData;
    }

    @Override
    String entityAttributeToString(String attribute) {
        return attribute;
    }
}
