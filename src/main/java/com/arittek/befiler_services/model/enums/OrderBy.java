package com.arittek.befiler_services.model.enums;

public enum OrderBy {
    ID("id"),
    FIRST_NAME("firstName"),
    MOBILE_NO("mobileNo"),
    STATUS("status"),
    CNIC("cnic"),
    EMAIL("email"),
    CURR_DATE("currDate");

    private String OrderByCode;
    private OrderBy(String orderBy) {
	this.OrderByCode = orderBy;
    }
    public String getOrderByCode() {
	return this.OrderByCode;
    }
}
