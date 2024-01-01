package com.arittek.befiler_services.beans;


import java.util.ArrayList;

public class CaptchaBean {

    private String registrationNo;
    private String referenceNo;
    private String strn;
    private String name;
    private String category;
    private String address;
    private String registeredOn;
    private String taxOffice;
    private String registrationStatus;
    private String filerStatus;

    private ArrayList<CaptchaBean2> captchaBean2;

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getStrn() {
        return strn;
    }

    public void setStrn(String strn) {
        this.strn = strn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public ArrayList<CaptchaBean2> getCaptchaBean2() {
        return captchaBean2;
    }

    public void setCaptchaBean2(ArrayList<CaptchaBean2> captchaBean2) {
        this.captchaBean2 = captchaBean2;
    }

    public String getFilerStatus() {
        return filerStatus;
    }

    public void setFilerStatus(String filerStatus) {
        this.filerStatus = filerStatus;
    }
}
