package com.arittek.befiler_services.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class StatusBean implements Serializable {

    private int code;
    private String message;

    //private InflowAndOutflowBean inflowAndOutflowBean;

    private PaginationBean pagination;   // by maqsood

    private List response = new ArrayList();
    private Map responseMap = new HashMap<>();

    public StatusBean(int i, int size, HttpStatus ok) {
    }

    public StatusBean(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
