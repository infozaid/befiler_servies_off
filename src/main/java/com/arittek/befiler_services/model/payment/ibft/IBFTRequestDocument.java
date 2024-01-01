package com.arittek.befiler_services.model.payment.ibft;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "ibft_request_document")
public class IBFTRequestDocument extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "status")
    private AppStatus status;

    private String documentDescription;
    private String documentFormat;
    private String documentName;
    private String documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ibft_request_id")
    private IBFTRequest ibftRequest;
}
