package com.arittek.befiler_services.model.ntn;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import com.arittek.befiler_services.model.user.User;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Audited
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "fbr_user_account_info_documents")
public class FbrUserAccountInfoDocuments extends GenericModel {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus appStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fbr_user_account_info_id")
    private FbrUserAccountInfo fbrUserAccountInfo;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_format")
    private String documentFormat;

    @Column(name = "document_description")
    private String documentDescription;

}
