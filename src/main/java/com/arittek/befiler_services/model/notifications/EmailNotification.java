package com.arittek.befiler_services.model.notifications;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.enums.EmTransactionTypeConverter;
import com.arittek.befiler_services.model.generic.GenericModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@Table(name = "email_notification")
public class EmailNotification extends GenericModel {

    @Column(name = "emails")
    private String email;

    @Convert(converter = AppStatusConverter.class)
    private AppStatus status;
}

