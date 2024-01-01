package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "taxform_income_tax_other_sources_other_inflow")
public class TaxForm_IncomeTax_OtherSources_OtherInflow extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "type")
    private String type;

    @Column(name = "memo_description")
    private String memoDescription;

    @Column(name = "amount")
     private Double amount;
}
