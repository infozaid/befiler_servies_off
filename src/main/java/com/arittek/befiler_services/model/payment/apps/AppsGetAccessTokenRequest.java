package com.arittek.befiler_services.model.payment.apps;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.keenu.KeenuResponse;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table( name = "apps_get_access_token_request")
public class AppsGetAccessTokenRequest extends GenericModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_customer_info_id")
    private PaymentCustomerInfo paymentCustomerInfo;

    private String merchantId;
    private String securedKey;
    private String url;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "appsGetAccessTokenRequest")
    private AppsGetAccessTokenResponse appsGetAccessTokenResponse;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "appsGetAccessTokenRequest")
    private AppsPostTransactionRequest appsPostTransactionRequest;

}
