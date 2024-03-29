package com.arittek.befiler_services.beans.finja_external_registration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Getter()
@Setter()
@NoArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Data{
    private City[] city_list;
    private Country[] country_list;
}
