package com.arittek.befiler_services.services.payment.settings;

import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.SettingPaymentStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import com.arittek.befiler_services.repository.payment.settings.SettingPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingPaymentServicesImpl implements SettingPaymentServices {

    private SettingPaymentRepository settingPaymentRepository;

    @Autowired
    public SettingPaymentServicesImpl(SettingPaymentRepository settingPaymentRepository) {
        this.settingPaymentRepository = settingPaymentRepository;
    }

    @Override
    public SettingPayment findPaymentForNTNRegistration() throws Exception {
        SettingPayment settingPayment = settingPaymentRepository.findOneByProductTypeAndSettingPaymentStatus(ProductType.NTN, SettingPaymentStatus.ACTIVE);
        return settingPayment;
    }

    @Override
    public SettingPayment findPaymentByYearForForTaxform(TaxformYears taxformYears) throws Exception {
        if (taxformYears != null) {
            SettingPayment settingPayment = settingPaymentRepository.findOneByProductTypeAndSettingPaymentStatusAndTaxformYear(ProductType.TAXFORM, SettingPaymentStatus.ACTIVE, taxformYears);
            return settingPayment;
        }
        return null;
    }

    @Override
    public SettingPayment findPaymentByPromoCodeForNTNRegistration(PromoCode promoCode) throws Exception {
        if (promoCode != null) {
            SettingPayment settingPayment = settingPaymentRepository.findOneByPromoCodeAndProductTypeAndSettingPaymentStatus(promoCode, ProductType.NTN, SettingPaymentStatus.PROMO_CODE);
            return settingPayment;
        }
        return null;
    }

    @Override
    public SettingPayment findPaymentByPromoCodeAndYearForTaxform(PromoCode promoCode, TaxformYears taxformYears) throws Exception {
        if (promoCode != null && taxformYears != null) {
            SettingPayment settingPayment = settingPaymentRepository.findOneByPromoCodeAndProductTypeAndSettingPaymentStatusAndTaxformYear(promoCode, ProductType.TAXFORM, SettingPaymentStatus.PROMO_CODE, taxformYears);
            return settingPayment;
        }
        return null;
    }
}
