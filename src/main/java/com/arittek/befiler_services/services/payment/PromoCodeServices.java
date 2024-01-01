package com.arittek.befiler_services.services.payment;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;

public interface PromoCodeServices {

    PromoCode disablePromoCode(PromoCode promoCode) throws Exception;

    PromoCode findOneActiveRecordByPromoCode(String promoCode) throws Exception;

    Boolean checkIfPromoCodeIsAssignedToUser(PromoCode promoCode, User user) throws Exception;
}
