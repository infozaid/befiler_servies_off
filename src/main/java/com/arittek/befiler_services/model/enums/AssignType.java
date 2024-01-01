package com.arittek.befiler_services.model.enums;

public enum AssignType {
    TAXFORM(1),
    NTN(2);

    private final Integer id;

    AssignType(Integer id) {
        this.id = id;
    }

    public static AssignType getAssignType(Integer id) {
        if (id == null) {
            return null;
        }

        for (AssignType assignType : AssignType.values()) {
            if (id == assignType.getId()) {
                return assignType;
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
