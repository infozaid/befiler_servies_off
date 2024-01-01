package com.arittek.befiler_services.fbr;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class FbrActiveTaxpayerBean {
    private String registrationNo;
    private String name;
    private String businessName;
}
