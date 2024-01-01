package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModelAudit;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "taxform_income_tax_salary")
public class Taxform_IncomeTax_Salary extends GenericModelAudit {

    @Id
    @Column(name="taxform_id", unique=true, nullable=false)
    @GeneratedValue(generator="gen")
    @GenericGenerator(name="gen", strategy="foreign", parameters=@Parameter(name="property", value="taxform"))
    private Integer taxformId;

    @Column(name = "basic_salary")
    private Double basicSalary;

    @Column(name = "tada")
    private Double tada;

    @Column(name = "medical_allowance")
    private Double medicalAllowance;

    @Column(name = "provident_fund_by_employeer")
    private Double providentFundByEmployeer;

    @Column(name = "other_allowance")
    private Double otherAllowance;

    @Column(name = "company_vehicle_cost")
    private Double companyVehicleCost;

    @Column(name = "company_vehicle_received_after_july")
    private Boolean companyVehicleReceivedAfterJuly;

    @Column(name = "company_vehicle_received_date")
    private Date companyVehicleReceivedDate;

    @Column(name = "provident_gratuity_fund_received")
    private Double providentOrGratuityFundReceived;

    @Column(name = "salary_tax_borne_by_employeer_check")
    private Boolean salaryTaxBorneByEmployeerCheck;

    @Column(name = "salary_tax_withheld_by_employeer")
    private Double salaryTaxWithheldByEmployeer;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Taxform taxform;

}
