package com.arittek.befiler_services.model.expense_module;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.enums.EmTransactionType;
import com.arittek.befiler_services.model.enums.EmTransactionTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "em_transaction_category")
public class EmTransactionCategory extends GenericModel {

    private String category;
    private String description;

    @Convert(converter = AppStatusConverter.class)
    private AppStatus status;

    private Boolean showTaxDeducted;

    @Convert(converter = EmTransactionTypeConverter.class)
    @Column(name = "em_transaction_type")
    private EmTransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "em_transaction_category_id")
    private EmTransactionCategory transactionCategory;



}
