package com.arittek.befiler_services.beans.userModule;

import java.sql.Timestamp;

public class GenericBean {
    private Integer id;


    private String name;

    private Integer authorizer;

    private Timestamp authDate;

    private Integer inputter;

    private Timestamp createDate;

    private Integer isActive;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(Integer authorizer) {
        this.authorizer = authorizer;
    }

    public Timestamp getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Timestamp authDate) {
        this.authDate = authDate;
    }

    public Integer getInputter() {
        return inputter;
    }

    public void setInputter(Integer inputter) {
        this.inputter = inputter;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
