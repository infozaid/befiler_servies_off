package com.arittek.befiler_services.beans.active_taxpayer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActiveTaxpayerInquiryBean {

    String registrationNo;

    String InvalidCaptcha;
    String NoRecord;

    String name;
    String businessName;
    String filingStatus;
}
