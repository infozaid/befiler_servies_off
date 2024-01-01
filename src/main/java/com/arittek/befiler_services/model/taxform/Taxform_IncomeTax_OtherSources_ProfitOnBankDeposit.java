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
@Table(name = "taxform_income_tax_other_sources_profit_on_bank_deposit")
public class Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "currency")
    private String currency;

    @Column(name = "profit_amount")
    private Double profitAmount;

    @Column(name = "tax_deducted")
    private Double taxDeducted;
}
