package com.arittek.befiler_services.beans;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FbrBusinessOrBranchesBean implements Serializable {
    private String serialNo;
    private String businessOrBranchName;
    private String businessOrBranchAddress;


    @Override
    public String toString() {
        return "FbrBusinessOrBranchesBean{" +
                "serialNo='" + serialNo + '\'' +
                ", businessOrBranchName='" + businessOrBranchName + '\'' +
                ", businessOrBranchAddress='" + businessOrBranchAddress + '\'' +
                '}';
    }
}

