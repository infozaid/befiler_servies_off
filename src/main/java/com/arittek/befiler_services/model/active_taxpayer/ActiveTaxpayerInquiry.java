package com.arittek.befiler_services.model.active_taxpayer;

import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Audited
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "active_enquiry_taxpayer_income")
public class ActiveTaxpayerInquiry extends GenericModel {

    @Column(name = "registration_status")
    private Integer registrationStatus;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "name")
    private String name;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "filing_name")
    private String filingStatus;


}
