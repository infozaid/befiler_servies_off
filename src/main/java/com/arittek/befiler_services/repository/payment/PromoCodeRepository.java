package com.arittek.befiler_services.repository.payment;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer>, RevisionRepository<PromoCode, Integer, Integer> {

    PromoCode findOneByPromoCodeAndStatusAndActiveDateLessThanEqualAndExpireDateGreaterThanEqual(String promoCode, AppStatus status, Timestamp activeDate, Timestamp expireDate);
}
