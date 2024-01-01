package com.arittek.befiler_services.services.payment.settings;

import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;

public interface SettingPaymentServices {

    SettingPayment findPaymentForNTNRegistration() throws Exception;
    SettingPayment findPaymentByYearForForTaxform(TaxformYears taxformYears) throws Exception;

    SettingPayment findPaymentByPromoCodeForNTNRegistration(PromoCode promoCode) throws Exception;
    SettingPayment findPaymentByPromoCodeAndYearForTaxform(PromoCode promoCode, TaxformYears taxformYears) throws Exception;
}
