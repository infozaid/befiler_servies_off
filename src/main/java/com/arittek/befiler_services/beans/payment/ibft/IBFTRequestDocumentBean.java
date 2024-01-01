package com.arittek.befiler_services.beans.payment.ibft;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class IBFTRequestDocumentBean {
    private Integer id;

    private String filetype;
    private String filename;
    private String base64;

}
