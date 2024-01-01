package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_income_tax_property")
public class Taxform_IncomeTax_Property extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "rent_received_from_your_property")
    private Double rentReceivedFromYourProperty;

    @Column(name = "do_you_deduct_any_tax")
    private Boolean doYouDeductAnyTax;

    @Column(name = "property_tax")
    private Double propertyTax;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;

}
