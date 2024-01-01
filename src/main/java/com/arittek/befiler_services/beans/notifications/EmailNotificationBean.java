package com.arittek.befiler_services.beans.notifications;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;


@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EmailNotificationBean implements Serializable {

    private Integer id;
    private String email;
    private Integer status;
//    private List<EmailNotificationBean> paymentNotificationBean;




}

