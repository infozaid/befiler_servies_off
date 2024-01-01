package com.arittek.befiler_services.model.expense_module;

import com.arittek.befiler_services.model.enums.EmTransactionTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "em_transaction")
public class EmTransaction extends GenericModel {

    private Double amount;
    private Double taxDeducted;
    private Date date;
    private String description;

    @Convert(converter = EmTransactionTypeConverter.class)
    @Column(name = "em_transaction_type")
    private com.arittek.befiler_services.model.enums.EmTransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "em_transaction_category_id")
    private EmTransactionCategory transactionCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "em_transaction_account_id")
    private EmTransactionAccount transactionAccount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "transaction")
    private List<EmDocuments> documentsList = new ArrayList<>();

}
