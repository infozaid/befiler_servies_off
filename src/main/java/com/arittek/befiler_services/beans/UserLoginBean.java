package com.arittek.befiler_services.beans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserLoginBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String role;
	private String userName;
	private String cnic;
	private String password;
	private String email;
	private String status;
}
