package com.arittek.befiler_services.model.payment.settings;

import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.ProductTypeConverter;
import com.arittek.befiler_services.model.enums.SettingPaymentStatus;
import com.arittek.befiler_services.model.enums.SettingPaymentStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "setting_payment")
public class SettingPayment extends GenericModel {

    @Convert(converter = ProductTypeConverter.class)
    private ProductType productType;

    @Convert(converter = SettingPaymentStatusConverter.class)
    private SettingPaymentStatus settingPaymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxform_year_id")
    private TaxformYears taxformYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @Column(name = "amount")
    private Double amount;
}
