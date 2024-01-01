package com.arittek.befiler_services.model.payment.promo_code;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.enums.PromoCodeType;
import com.arittek.befiler_services.model.enums.PromoCodeTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "promo_code")
public class PromoCode extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    private AppStatus status;

    @Convert(converter = PromoCodeTypeConverter.class)
    private PromoCodeType promoCodeType;

    private String promoCode;
    private String promoCodeDescription;
    private Timestamp activeDate;
    private Timestamp expireDate;

    private Integer allowedNoOfUse;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "promoCode")
    private List<PromoCodeAssign> promoCodeAssignList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "promoCode")
    private List<SettingPayment> settingPaymentList = new ArrayList<>();
}
