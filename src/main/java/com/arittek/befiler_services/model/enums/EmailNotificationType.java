package com.arittek.befiler_services.model.enums;


public enum EmailNotificationType {

    PAYMENT_NOTIFICATION(1),
    SIGN_UP_NOTIFICATION(2);

    private final Integer id;

    EmailNotificationType(Integer id) {
        this.id = id;
    }

    public static EmailNotificationType getEmailNotificationPermission(Integer id) {
        if (id == null) {
            return null;
        }

        for (EmailNotificationType emailNotifyPermission : EmailNotificationType.values()) {
            if (id == emailNotifyPermission.getId()) {
                return emailNotifyPermission;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }

}
