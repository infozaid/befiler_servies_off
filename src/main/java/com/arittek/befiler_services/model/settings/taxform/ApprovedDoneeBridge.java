package com.arittek.befiler_services.model.settings.taxform;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "setting_taxform_approved_donee_bridge")
public class ApprovedDoneeBridge extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "taxform_year_id")
    private TaxformYears taxformYear;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "setting_taxform_approved_donee_id")
    private ApprovedDonee approvedDonee;
}
