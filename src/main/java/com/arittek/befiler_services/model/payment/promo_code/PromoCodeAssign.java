package com.arittek.befiler_services.model.payment.promo_code;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "promo_code_assign")
public class PromoCodeAssign extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
