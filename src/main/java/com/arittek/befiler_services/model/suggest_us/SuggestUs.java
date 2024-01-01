package com.arittek.befiler_services.model.suggest_us;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter()
@Setter()
@NoArgsConstructor
@Entity
@Table(name = "suggest_us")
public class SuggestUs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String fromEmail;

    @Column(name = "message")
    private String message;

    @Column(name = "mobile_no")
    private String mobileNo;
}
