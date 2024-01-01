package com.arittek.befiler_services.model.payment;

import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.ProductTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "payment")
public class Payment extends GenericModel {

    private Integer status;

    @Convert(converter = ProductTypeConverter.class)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_customer_info_id")
    private PaymentCustomerInfo paymentCustomerInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_id")
    private Taxform taxform;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_taxform_payment_id")
    private SettingTaxformPayment settingTaxformPayment;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fbr_user_account_info_id")
    private FbrUserAccountInfo fbrUserAccountInfo;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_fbr_payment_id")
    private SettingFbrPayment settingFbrPayment;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_payment_id")
    private SettingPayment settingPayment;
}
