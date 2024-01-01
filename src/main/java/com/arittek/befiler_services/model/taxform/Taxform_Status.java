package com.arittek.befiler_services.model.taxform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "taxform_status")
public class Taxform_Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    /*@OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
    private List<Taxform> taxformList = new ArrayList<Taxform>();*/

    /*public List<Taxform> getTaxformList() {
        return taxformList;
    }

    public void setTaxformList(List<Taxform> taxformList) {
        this.taxformList = taxformList;
    }*/
}
