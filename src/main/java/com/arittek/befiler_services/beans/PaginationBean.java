package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by HP-PC on 09-Apr-19.
 */
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class PaginationBean implements Serializable {      // <!-- by maqsood -->
    private static final long serialVersionUID = 1L;

    private int totalElements;
    private int totalPages;
    private int pageSize;
    private int pageNumber;
}
