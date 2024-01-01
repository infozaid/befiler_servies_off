package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.beans.taxform.TaxformDocumentsBean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class InputBean implements Serializable {

    private Integer userId;
    private Integer assignId;
    private Integer taxformId;
    private Integer authorizerId;
    private Integer taxformStatusId;

    private String promoCode;

    private List<TaxformDocumentsBean> taxformDocumentsBeanList;
}
