package com.arittek.befiler_services.model.taxform;

import com.arittek.befiler_services.model.generic.GenericModel;
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
@Table(name = "taxform_welth_statement_bank_accounts_or_investments")
public class Taxform_WelthStatement_BankAccountsOrInvestments extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    @Column(name = "form")
    private String form;

    @Column(name = "account_or_instrument_no")
    private String accountOrInstructionNo;

    @Column(name = "institution_name_or_individual_cnic")
    private String institutionNameOrInduvidualCnic;

    private String branchName;

    @Column(name = "cost")
    private Double cost;
}