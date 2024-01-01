package com.arittek.befiler_services.repository.payment.settings;

import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.SettingPaymentStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingPaymentRepository extends JpaRepository<SettingPayment, Integer>, RevisionRepository<SettingPayment, Integer, Integer> {

    SettingPayment findOneByProductTypeAndSettingPaymentStatus(ProductType productType, SettingPaymentStatus settingPaymentStatus);
    SettingPayment findOneByProductTypeAndSettingPaymentStatusAndTaxformYear(ProductType productType, SettingPaymentStatus settingPaymentStatus, TaxformYears taxformYear);

    SettingPayment findOneByPromoCodeAndProductTypeAndSettingPaymentStatus(PromoCode promoCode, ProductType productType, SettingPaymentStatus settingPaymentStatus);
    SettingPayment findOneByPromoCodeAndProductTypeAndSettingPaymentStatusAndTaxformYear(PromoCode promoCode, ProductType productType, SettingPaymentStatus settingPaymentStatus, TaxformYears taxformYear);
}
