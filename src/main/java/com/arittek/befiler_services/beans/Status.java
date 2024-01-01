package com.arittek.befiler_services.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter()
@Setter()
@NoArgsConstructor
public class Status implements Serializable {

	private int code;
	private String message;

	public Status(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
