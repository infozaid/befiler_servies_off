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
@Table(name = "taxform_welth_statement")
public class Taxform_WelthStatement extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@org.hibernate.annotations.Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "opening_wealth")
    private Double openingWealth;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;


}
