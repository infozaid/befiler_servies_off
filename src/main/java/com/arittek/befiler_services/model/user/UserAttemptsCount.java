package com.arittek.befiler_services.model.user;

import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.AppStatusConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter()
@Setter()
@NoArgsConstructor
@Table(name = "um_user_attempts_count")
public class UserAttemptsCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorizer_id")
    private User authorizer;


    @Convert(converter = AppStatusConverter.class)
    @Column(name = "app_status")
    private AppStatus status;

    @Column(name = "login_attempts_count")
    private Integer loginAttemptsCount;

    @Column(name = "payment_attempts_count")
    private Integer paymentAttemptsCount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "curr_date")
    private Timestamp currDate;
}
