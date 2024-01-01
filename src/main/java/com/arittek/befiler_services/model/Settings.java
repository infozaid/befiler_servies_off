package com.arittek.befiler_services.model;

import com.arittek.befiler_services.model.enums.AppStatusConverter;
import com.arittek.befiler_services.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter()
@Setter()
@NoArgsConstructor
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private com.arittek.befiler_services.model.enums.AppStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizer_id")
    private User authorizer;

    @Column(name = "days_to_sent_marketing")
    private Integer daysToSentMarketing;

    @Column(name = "max_login_attempts")
    private Integer maxLoginAttempts;

    @Column(name = "max_paymet_attempts")
    private Integer maxPaymentAttempts;

    @Column(name = "curr_date")
    private Timestamp currentDate;
}
