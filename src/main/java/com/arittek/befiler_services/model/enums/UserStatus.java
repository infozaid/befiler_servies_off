package com.arittek.befiler_services.model.enums;

public enum UserStatus {
    DEACTIVE(0),
    ACTIVE(1),
    DELETED(2);

    private final Integer id;

    UserStatus(Integer id) {
        this.id = id;
    }

    public static UserStatus getUserStatus(Integer id) {
        if (id == null) {
            return null;
        }

        for (UserStatus userStatus : UserStatus.values()) {
            if (id == userStatus.getId()) {
                return userStatus;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
