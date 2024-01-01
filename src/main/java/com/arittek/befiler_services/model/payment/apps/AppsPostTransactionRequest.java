package com.arittek.befiler_services.model.payment.apps;

import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table( name = "apps_post_transaction_request")
public class AppsPostTransactionRequest extends GenericModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apps_get_access_token_request_id")
    private AppsGetAccessTokenRequest appsGetAccessTokenRequest;

    private String redirectUrl;
    private String merchantId;
    private String merchantName;
    private String token;
    private String proCode;
    private Double txnamt;
    private String customerMobileNo;
    private String customerEmailAddress;
    private String signature;
    private String appsVersion;
    private String txndesc;
    private String successUrl;
    private String failureUrl;
    private String basketId;
    private String orderDate;
    private String checkoutUrl;

}
