package com.arittek.befiler_services.model.user;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.settings.corporate.Category;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "corporate")
public class Corporate extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus status;

    @Column(name = "corporate_info")
    private String corporateInfo;

    @Column(name = "corporate_logo")
    private String corporateLogo;

    @Column(name = "corporate_name")
    private String corporateName;

    @Column(name = "ntn_cnic")
    private String ntnCnic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category corporateCategory;

    @Column(name = "corporate_landline_no")
    private String corporateLandLineNo;

    @Column(name = "corporate_contact_no")
    private String corporateContactNo;

    @Column(name = "web_address")
    private String webAddress;

    @Column(name = "corporate_address")
    private String corporateAddress;

    @Column(name = "benificiary_name")
    private String benificiaryName;

    @Column(name = "person_contact_no")
    private String personContactNo;

    @Column(name = "person_landline_no")
    private String personLandLineNo;

    @Column(name = "designation")
    private String designation;

    @Column(name = "email_address")
    private String emailAddress;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "corporate")
    private List<CorporateEmployee> corporateEmployeeList;

}
