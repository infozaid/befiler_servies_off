package com.arittek.befiler_services.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DocumentsBean {

    private Integer id;

    private String documentName;
    private String documentBase64;
    private String documentURL;
    private String documentFormat;
    private String documentDescription;

}
