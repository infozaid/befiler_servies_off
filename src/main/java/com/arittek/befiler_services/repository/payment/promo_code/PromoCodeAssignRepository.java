package com.arittek.befiler_services.repository.payment.promo_code;

import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.payment.promo_code.PromoCodeAssign;
import com.arittek.befiler_services.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeAssignRepository extends JpaRepository<PromoCodeAssign, Integer>, RevisionRepository<PromoCodeAssign, Integer, Integer> {

    PromoCodeAssign findOneByPromoCodeAndUser(PromoCode promoCode, User user);
}
