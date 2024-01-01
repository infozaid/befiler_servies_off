package com.arittek.befiler_services.model.settings.taxform;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "setting_taxform_approved_donee")
public class ApprovedDonee extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "taxform_year_id")
    private TaxformYears taxformYear;

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus status;

    @Column(name = "donee_name")
    private String doneeName;

    @Column(name = "donee_description")
    private String doneeDescription;
}
