package com.arittek.befiler_services.model.enums;

public enum FbrUserAccountInfoStatus {
    REGISTERED(1),
    NOT_REGISTERED(2),
    ACCOUNTANT(3),
    LAWYER_ASSIGN(4),
    LAWYER_OPEN(5),
    LAWYER_CLOSE(6);

    private final Integer id;

    FbrUserAccountInfoStatus(Integer id) {
        this.id = id;
    }

    public static FbrUserAccountInfoStatus getFbrUserAccountInfoStatus(Integer id) {
        if (id == null) {
            return null;
        }

        for (FbrUserAccountInfoStatus fbrUserAccountInfoStatus : FbrUserAccountInfoStatus.values()) {
            if (id == fbrUserAccountInfoStatus.getId()) {
                return fbrUserAccountInfoStatus;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
