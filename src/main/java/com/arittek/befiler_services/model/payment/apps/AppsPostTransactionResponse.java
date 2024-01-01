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
@Table( name = "apps_post_transaction_response")
public class AppsPostTransactionResponse extends GenericModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apps_get_access_token_request_id")
    private AppsGetAccessTokenRequest appsGetAccessTokenRequest;

    private String errorCode;
    private String errorMessage;
    private String transactionId;
    private String basketId;
    private String orderDate;
    private String rdvMessageKey;
    private String responseKey;
}
