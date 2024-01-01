package com.arittek.befiler_services.beans;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
public class FbrUserRegistrationPageBean implements Serializable {
    private int userId;
    HtmlPage pageRegistration ;
    HtmlPage htmlPage1 ;
}
