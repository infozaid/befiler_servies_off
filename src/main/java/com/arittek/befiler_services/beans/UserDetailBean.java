package com.arittek.befiler_services.beans;

import com.arittek.befiler_services.beans.taxform.TaxformMinimalBean;
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
public class UserDetailBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long serialNo;
	private Integer id;
	private String user;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String status;
	private String address;
	private String createDate;

	private Integer userId;
	private String cnic;
	private Integer prefix;
	private String email;
	private String captcha;
	private String smsPin;
	private String emailPin;
	private String currDate;

	private UserTypeBean userType;

	private List<TaxformMinimalBean> taxformMinimalBeans;
	private List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList;

}
