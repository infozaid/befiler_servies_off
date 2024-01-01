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
public class BankAccountsBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String bankName;
	private String branchName;
	private String accountNo;
	private String accountTitle;
	private String branchAddress;
	private Integer Status;
}
