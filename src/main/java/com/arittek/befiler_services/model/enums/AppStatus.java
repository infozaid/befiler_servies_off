package com.arittek.befiler_services.model.enums;

public enum AppStatus {
    DE_ACTIVE(0),
    ACTIVE(1),
    DELETED(2);

    private final Integer id;

    private AppStatus(Integer id) { this.id = id; }

    public static AppStatus getAppStatus(Integer id) {
        if (id == null) {
            return null;
        }

        for (AppStatus appStatus : AppStatus.values()) {
            if (id == appStatus.getId()) {
                return appStatus;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
