package com.arittek.befiler_services.model.enums;

public enum PromoCodeType {
    GENERIC(1),
    SPECIFIC(2),
    SINGLE_USE(3),
    CASH(4);

    private final Integer id;

    PromoCodeType(Integer id) { this.id = id; }

    public static PromoCodeType getPromoCodeType(Integer id) {
        if (id == null) {
            return null;
        }

        for (PromoCodeType promoCodeType : PromoCodeType.values()) {
            if (id == promoCodeType.getId()) {
                return promoCodeType;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }
}
