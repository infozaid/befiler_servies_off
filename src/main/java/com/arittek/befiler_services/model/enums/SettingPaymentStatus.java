package com.arittek.befiler_services.model.enums;

public enum SettingPaymentStatus {
    PROMO_CODE(0),
    ACTIVE(1),
    DELETED(2);

    private final Integer id;

    SettingPaymentStatus(Integer id) {
        this.id = id;
    }

    public static SettingPaymentStatus getSettingPaymentStatus(Integer id) {
        if (id == null) {
            return null;
        }

        for (SettingPaymentStatus settingPaymentStatus : SettingPaymentStatus.values()) {
            if (id == settingPaymentStatus.getId()) {
                return settingPaymentStatus;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
