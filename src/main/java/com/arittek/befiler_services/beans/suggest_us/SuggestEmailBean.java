package com.arittek.befiler_services.beans.suggest_us;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter()
@Setter()
@NoArgsConstructor
public class SuggestEmailBean {

    private String name;
    private String fromEmail;
    private String toEmail;
    private String message;
    private String mobileNo;

}
