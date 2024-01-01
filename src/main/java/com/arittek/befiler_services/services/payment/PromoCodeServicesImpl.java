package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.promo_code.PromoCodeAssign;
import com.arittek.befiler_services.repository.payment.promo_code.PromoCodeAssignRepository;
import com.arittek.befiler_services.repository.payment.PromoCodeRepository;
import com.arittek.befiler_services.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromoCodeServicesImpl implements PromoCodeServices {

    private PromoCodeRepository promoCodeRepository;
    private PromoCodeAssignRepository promoCodeAssignRepository;

    @Autowired
    public PromoCodeServicesImpl(PromoCodeRepository promoCodeRepository, PromoCodeAssignRepository promoCodeAssignRepository) {
        this.promoCodeRepository = promoCodeRepository;
        this.promoCodeAssignRepository = promoCodeAssignRepository;
    }

    @Override
    public PromoCode disablePromoCode(PromoCode promoCode) throws Exception {
        if (promoCode != null && promoCode.getStatus() != AppStatus.DE_ACTIVE) {
            promoCode.setStatus(AppStatus.DE_ACTIVE);

            return promoCodeRepository.save(promoCode);
        }
        return null;
    }

    @Override
    public PromoCode findOneActiveRecordByPromoCode(String promoCode) throws Exception {
        if (StringUtils.isNotEmpty(promoCode)) {
            return promoCodeRepository.findOneByPromoCodeAndStatusAndActiveDateLessThanEqualAndExpireDateGreaterThanEqual(promoCode, AppStatus.ACTIVE, CommonUtil.getCurrentTimestamp(), CommonUtil.getCurrentTimestamp());
        }
        return null;
    }

    @Override
    public Boolean checkIfPromoCodeIsAssignedToUser(PromoCode promoCode, User user) throws Exception {
        if (promoCode != null && user != null) {
            PromoCodeAssign promoCodeAssign = promoCodeAssignRepository.findOneByPromoCodeAndUser(promoCode, user);
            if (promoCodeAssign != null)
                return true;
            else
                return false;
        }
        return false;
    }
}
