package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class FbrBean implements Serializable {
    private String ntnOrFtn;
    private String category;
    private String name;
    private String cnicOrPpOrRegOrIncNo;
    private String houseOrFlatOrPlotNo;
    private String streetOrLane;
    private String blockOrSectorOrRoad;
    private String city;
    private String principalBusinessActivity;
    private String businessNature;
    private String registeredFor;
    private String incomeTaxOffice;
    private int code;
    private String message;


    List<FbrBusinessOrBranchesBean> fbrBusinessOrBranchesBeanList = new ArrayList<FbrBusinessOrBranchesBean>();

    @Override
    public String toString() {
        return "FbrBean{" +
                "ntnOrFtn='" + ntnOrFtn + '\'' +
                ", category='" + category + '\'' +
                ", name='" + name + '\'' +
                ", cnicOrPpOrRegOrIncNo='" + cnicOrPpOrRegOrIncNo + '\'' +
                ", houseOrFlatOrPlotNo='" + houseOrFlatOrPlotNo + '\'' +
                ", streetOrLane='" + streetOrLane + '\'' +
                ", blockOrSectorOrRoad='" + blockOrSectorOrRoad + '\'' +
                ", city='" + city + '\'' +
                ", principalBusinessActivity='" + principalBusinessActivity + '\'' +
                ", businessNature='" + businessNature + '\'' +
                ", registeredFor='" + registeredFor + '\'' +
                ", incomeTaxOffice='" + incomeTaxOffice + '\'' +
                ", fbrBusinessOrBranchesBeanList=" + fbrBusinessOrBranchesBeanList +
                '}';
    }
}

