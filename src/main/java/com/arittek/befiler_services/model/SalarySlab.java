package com.arittek.befiler_services.model;

import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter()
@Setter()
@NoArgsConstructor
@Table(name = "salary_slab")
public class SalarySlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "serial_number")
    private Integer serialNumber;

    @Column(name = "fixed")
    private Integer fixed;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "lower_limit")
    private Integer lowerLimit;

    @Column(name = "upper_limit")
    private Integer upperLimit;

    @Column(name = "curr_date")
    private Timestamp currentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizer_id")
    private User authorizer;

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private com.arittek.befiler_services.model.enums.AppStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_year_id")
    private TaxformYears taxformYear;
}
