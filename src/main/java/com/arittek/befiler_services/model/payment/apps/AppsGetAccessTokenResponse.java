package com.arittek.befiler_services.model.payment.apps;

import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.generic.GenericModelAudit;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table( name = "apps_get_access_token_response")
public class AppsGetAccessTokenResponse extends GenericModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apps_get_access_token_request_id")
    private AppsGetAccessTokenRequest appsGetAccessTokenRequest;

    private String errorCode;
    private String errorDescription;

    private String name;
    private String accessToken;
    private String generatedDateTime;
}
