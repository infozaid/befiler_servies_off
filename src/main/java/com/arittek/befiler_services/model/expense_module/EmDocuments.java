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
@Table(name = "em_documents")
public class EmDocuments extends GenericModel {

    private String documentName;
    private String documentFormat;
    private String documentDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "em_transaction_id")
    private EmTransaction transaction;

    @Convert(converter = EmTransactionTypeConverter.class)
    @Column(name = "em_transaction_type")
    private EmTransactionType transactionType;

}
