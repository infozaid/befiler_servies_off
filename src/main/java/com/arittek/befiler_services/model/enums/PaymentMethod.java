package com.arittek.befiler_services.model.enums;

public enum PaymentMethod {
    UBL(1),
    SIMSIM(2),
    PROMO_CODE(3),
    KEENU(4),
    APPS(5),
    EASYPAISA_REDIRECT(6),
    EASYPAISA_OTC(7),
    EASYPAISA_MA(8);

    private final Integer id;

    PaymentMethod(Integer id) {
        this.id = id;
    }

    public static PaymentMethod getPaymentMethod(Integer id) {
        if (id == null) {
            return null;
        }

        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (id == paymentMethod.getId()) {
                return paymentMethod;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
