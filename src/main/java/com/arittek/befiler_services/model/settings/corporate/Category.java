package com.arittek.befiler_services.model.settings.corporate;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
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
@Table(name = "setting_corporate_category")
public class Category extends GenericModel {

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus status;

    @Column(name = "category")
    private String category;

    @Column(name = "discription")
    private String discription;
}
