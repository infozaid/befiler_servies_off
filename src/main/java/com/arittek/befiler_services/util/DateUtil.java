package com.arittek.befiler_services.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String dateFormate = "MM/dd/yyyy";
	public static final SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);

	public static String dateToString(Date date) {
		if (date != null) {
			String formatedDate = sdf.format(date);

			return formatedDate;
		}
		return null;
	}

	public static Date stringToDate(String date) {
		Date parsedDate = null;
		if (date != null && !date.equals("")) {
			try {
				parsedDate = sdf.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
				Logger4j.getLogger().error("Exception : ",e);
			}
		}
		return parsedDate;
	}

	public static Double stringToDouble(String doubleValue) {
		Double test = 0.0;
		if (doubleValue != null && !doubleValue.equals("") && isDouble(doubleValue)) {
			test = Double.parseDouble(doubleValue);
		}
		return test;
	}

	static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Logger4j.getLogger().error("Exception : ",e);
			return false;
		}
	}

}
