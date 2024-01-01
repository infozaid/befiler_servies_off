package com.arittek.befiler_services.util;

import com.arittek.befiler_services.beans.taxform.TaxformCalculationValuesBean;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.util.email.EmailSender;


import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class CommonUtil {


    public static String passwordCredentials(String inputPassword) {

        if (inputPassword.length() < 8) {
            return "Password is short. Password should have at least 8 characters in length";
        }
        boolean hasUppercase = !inputPassword.equals(inputPassword.toLowerCase());

        boolean hasLowercase = !inputPassword.equals(inputPassword.toUpperCase());

        boolean hasNumber = inputPassword.matches(".*\\d.*");

        if (!hasUppercase) {
            return "Password should have an Uppercase";
        }
        else if (!hasLowercase) {
            return "Password should have a Lowercase ";
        }
        else if (!hasNumber) {
            return "Password should have a Number";
        }else{
            return inputPassword;
        }

    }

    public static Double findSmallestNumberBetween3(Double first, Double second, Double third) {
        double[] numbers = {first, second, third};
        double smallest = numbers[0];
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] < smallest) {
                smallest = numbers[i];
            }
        }
        return smallest;
    }

    public static Double findSmallestNumberBetween2(Double second, Double third) {
        double[] numbers = {second, third};
        double smallest = numbers[0];
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] < smallest) {
                smallest = numbers[i];
            }
        }
        return smallest;
    }

    public static Boolean stringContainsOnlyNumbers(String text) {
        return text.matches("[0-9]+");
    }

    public static Long daysBetweenTwoDates(Date firstDate, Date secondDate) {
        long difference = (firstDate.getTime() - secondDate.getTime()) / 86400000;
        return Math.abs(difference);
    }

    public static Date parseDate(String date) throws Exception {
        return new SimpleDateFormat("MM/dd/yyyy").parse(date);
    }

    public static Double dashPercentOfValue(Double percentage, Double value) throws Exception {
        if (percentage != null && value != null) {
            return (value / 100) * percentage;
        }
        return 0.0;
    }

    public static Double getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(Integer year, Double salary) {
        if (year != null && salary != null) {
            if (year == 2019) {
                if (salary <= 400000) {
                    return 0.0;
                } else if (salary > 400000 && salary <= 800000) {
                    return 1000.0;
                } else if (salary > 800000 && salary <= 1200000) {
                    return 2000.0;
                } else if (salary > 1200000 && salary <= 2500000) {
                    Double tax = 0 + ((salary - 1200000) / 100) * 5;
                    if (Double.compare(tax, 2000.0) < 0)
                        return 2000.0;
                    else
                        return tax;
                } else if (salary > 2500000 && salary <= 4000000) {
                    return 65000 + ((salary - 2500000) / 100) * 15;
                } else if (salary > 4000000 && salary <= 8000000) {
                    return 290000 + ((salary - 4000000) / 100) * 20;
                } else if (salary > 8000000) {
                    return 1090000 + ((salary - 8000000) / 100) * 25;
                } else
                    return 0.0;
            } else {
                if (salary <= 400000) {
                    return 0.0;
                } else if (salary > 400000 && salary <= 500000) {
                    return 0.0 + ((salary - 400000) / 100) * 2;
                } else if (salary > 500000 && salary <= 750000) {
                    return 2000 + ((salary - 500000) / 100) * 5;
                } else if (salary > 750000 && salary <= 1400000) {
                    return 14500 + ((salary - 750000) / 100) * 10;
                } else if (salary > 1400000 && salary <= 1500000) {
                    return 79500 + ((salary - 1400000) / 100) * 12.5;
                } else if (salary > 1500000 && salary <= 1800000) {
                    return 92000 + ((salary - 1500000) / 100) * 15;
                } else if (salary > 1800000 && salary <= 2500000) {
                    return 137000 + ((salary - 1800000) / 100) * 17.5;
                } else if (salary > 2500000 && salary <= 3000000) {
                    return 259500 + ((salary - 2500000) / 100) * 20;
                } else if (salary > 3000000 && salary <= 3500000) {
                    return 359500 + ((salary - 3000000) / 100) * 22.5;
                } else if (salary > 3500000 && salary <= 4000000) {
                    return 472000 + ((salary - 3500000) / 100) * 25;
                } else if (salary > 4000000 && salary <= 7000000) {
                    return 597000 + ((salary - 4000000) / 100) * 27.5;
                } else if (salary > 7000000) {
                    return 1422000 + ((salary - 7000000) / 100) * 30;
                } else
                    return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    public static Double getTaxRateFromProfitOnBankDeposit(Taxform taxform, Double bankProfit) throws Exception {
        if (taxform != null && taxform.getTaxformYear() != null && taxform.getTaxformYear().getYear() != null && bankProfit != null) {
            Integer year = taxform.getTaxformYear().getYear();
            if (year == 2018 || year == 2019) {
                if (bankProfit <= 5000000) {
                    return CommonUtil.dashPercentOfValue(10.0, bankProfit);
                } else if (bankProfit > 5000000 && bankProfit <= 25000000) {
                    return CommonUtil.dashPercentOfValue(12.5, bankProfit);
                } else if (bankProfit > 25000000) {
                    return CommonUtil.dashPercentOfValue(15.0, bankProfit);
                } else
                    return CommonUtil.dashPercentOfValue(10.0, bankProfit);
            } else {
                if (bankProfit <= 25000000) {
                    return CommonUtil.dashPercentOfValue(10.0, bankProfit);
                } else if (bankProfit > 25000000 && bankProfit <= 50000000) {
                    return 2500000 + (((bankProfit - 25000000) / 100) * 12.5);
                } else if (bankProfit > 50000000) {
                    return 5625000 + (((bankProfit - 50000000) / 100) * 15.0);
                } else
                    return CommonUtil.dashPercentOfValue(10.0, bankProfit);
            }
        }
        return CommonUtil.dashPercentOfValue(0.0, bankProfit);
    }

    public static Double getTaxRateFromSalaryTaxSlabSalaryIncomeLess50(Double salary) {
        if (salary != null) {
            if (salary <= 400000) {
                return 0.0;
            } else if (salary > 400000 && salary <= 500000) {
                return 0.0 + ((salary - 400000) / 100) * 7;
            } else if (salary > 500000 && salary <= 750000) {
                return 7000 + ((salary - 500000) / 100) * 10;
            } else if (salary > 750000 && salary <= 1500000) {
                return 32000 + ((salary - 750000) / 100) * 15;
            } else if (salary > 1500000 && salary <= 2500000) {
                return 144500 + ((salary - 1500000) / 100) * 20;
            } else if (salary > 2500000 && salary <= 4000000) {
                return 344500 + ((salary - 2500000) / 100) * 25;
            } else if (salary > 4000000 && salary <= 6000000) {
                return 719500 + ((salary - 4000000) / 100) * 30;
            } else if (salary > 6000000) {
                return 1319500 + ((salary - 6000000) / 100) * 35;
            } else
                return 0.0;
        } else {
            return 0.0;
        }

    }

    public static Double getTaxRateFromPropertyRentSlab(Double propertyRent) {
        if (propertyRent != null) {
            if (propertyRent <= 200000) {
                return 0.0;
            } else if (propertyRent > 200000 && propertyRent <= 600000) {
                return ((propertyRent - 200000) / 100) * 5;
            } else if (propertyRent > 600000 && propertyRent <= 1000000) {
                return 20000 + ((propertyRent - 600000) / 100) * 10;
            } else if (propertyRent > 1000000 && propertyRent <= 2000000) {
                return 60000 + ((propertyRent - 1000000) / 100) * 15;
            } else if (propertyRent > 2000000) {
                return 210000 + ((propertyRent - 2000000) / 100) * 20;
            } else
                return 0.0;
        } else
            return 0.0;
    }

    public static List<Double> getCapitalGainOnDisposalOfSecurities(String year) {
        List<Double> percentages = new ArrayList<Double>();

        if (year != null && !year.equals("")) {
            if (year.equals("2015")) {
                percentages.add(12.5);
                percentages.add(10.0);
                percentages.add(0.0);
                percentages.add(0.0);

                return percentages;
            } else if (year.equals("2016") || year.equals("2017") || year.equals("2018") || year.equals("2019")) {
                percentages.add(15.0);
                percentages.add(12.5);
                percentages.add(7.5);
                percentages.add(0.0);

                return percentages;
            } else {
                return null;
            }
        }
        return null;
    }

    public static TaxformCalculationValuesBean getTaxformCalculationValuesByYear(Taxform taxform) {
        TaxformCalculationValuesBean taxformCalculationValuesBean = new TaxformCalculationValuesBean();
        if (taxform != null && taxform.getTaxformYear() != null && taxform.getTaxformYear().getYear() != null) {
            Integer year = taxform.getTaxformYear().getYear();
            if (year == 2017) {
                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> EDUCATION ALLOWANCE========================*/
                taxformCalculationValuesBean.setEducationAllowanceShowCheckAmount(1000000.0);
                taxformCalculationValuesBean.setEducationAllowanceTaxableIncomePercent(20.0);

                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> HEALTH INSURANCE PREMIUM========================*/
                taxformCalculationValuesBean.setHealthInsurancePremiumStaticAmount(100000.0);

                /*========================INCOME TAX -> OTHER SOURCES -> Dividends========================*/
                List<Double> dividendPercentages = new ArrayList<>();

                dividendPercentages.add(7.5);
                dividendPercentages.add(12.5);
                dividendPercentages.add(10.0);

                taxformCalculationValuesBean.setDividendPercentages(dividendPercentages);

            } else if (year == 2018 || year == 2019) {
                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> EDUCATION ALLOWANCE========================*/
                taxformCalculationValuesBean.setEducationAllowanceShowCheckAmount(1500000.0);
                taxformCalculationValuesBean.setEducationAllowanceTaxableIncomePercent(25.0);

                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> HEALTH INSURANCE PREMIUM========================*/
                taxformCalculationValuesBean.setHealthInsurancePremiumStaticAmount(150000.0);

                /*========================INCOME TAX -> OTHER SOURCES -> Dividends========================*/
                List<Double> dividendPercentages = new ArrayList<>();

                dividendPercentages.add(7.5);
                dividendPercentages.add(15.0);
                if (taxform.getTaxformIncomeTaxOtherSources() != null && taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFunds() != null) {
                    if (taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFunds() <= 2500000)
                        dividendPercentages.add(10.0);
                    else
                        dividendPercentages.add(12.5);
                }

                taxformCalculationValuesBean.setDividendPercentages(dividendPercentages);

            } else {
                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> EDUCATION ALLOWANCE========================*/
                taxformCalculationValuesBean.setEducationAllowanceShowCheckAmount(1000000.0);
                taxformCalculationValuesBean.setEducationAllowanceTaxableIncomePercent(20.0);

                /*========================DEDUCTABLE ALLOWANCE AND CREDITS -> HEALTH INSURANCE PREMIUM========================*/
                taxformCalculationValuesBean.setHealthInsurancePremiumStaticAmount(100000.0);

                /*========================INCOME TAX -> OTHER SOURCES -> Dividends========================*/
                List<Double> dividendPercentages = new ArrayList<>();

                dividendPercentages.add(7.5);
                dividendPercentages.add(12.5);
                dividendPercentages.add(10.0);

                taxformCalculationValuesBean.setDividendPercentages(dividendPercentages);
            }
        }
        return taxformCalculationValuesBean;
    }

    public static String changeDateToString(Date date) {
        String DATE_FORMAT = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            String formatedDate = sdf.format(date);

            return formatedDate;
        }
        return null;
    }

    public static String changeTimeToString(Time time) {
        String DATE_FORMAT = "hh:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        if (time != null) {
            String formatedDate = sdf.format(time);

            return formatedDate;
        }
        return null;
    }

    public static Date getCurrentDate(Date date) {
        String DATE_FORMAT = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        if (date != null) {
            String formatedDate = sdf.format(date);

            return changeDateStringToDate(formatedDate);
        }
        return null;
    }

    public static Date changeDateStringToDate(String date) {
        String DATE_FORMAT = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date parsedDate = null;
        if (date != null) {
            try {
                parsedDate = sdf.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
                Logger4j.getLogger().error("Exception:" + e);
            }
        }
        return parsedDate;
    }

    public static Time changeTimeStringToTime(String time) {
        Time t = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            long ms = sdf.parse(time).getTime();
            t = new Time(ms);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:" + e);
        }
        return t;
    }

    public static Timestamp getCurrentTimestamp() {
        Timestamp currentTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            currentTimestamp = new Timestamp(now.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
        }
        return currentTimestamp;
    }


    public static Timestamp getExpireTimestamp() {
        Timestamp currentTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            currentTimestamp = new Timestamp(now.getTime() + (60 * 15000)); // 15mint
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
        }
        return currentTimestamp;
    }

    public static Timestamp getCurrentTimestamp(Long minutes) {
        Timestamp currentTimestamp = null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            currentTimestamp = new Timestamp(now.getTime() + minutes * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:", e);
        }
        return currentTimestamp;
    }


    public static String changeTimestampToString(Timestamp timestamp) {
        Date date = timestamp;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String date1 = sdf.format(date);
        return date1;
    }

    public static String changeTimestampToString(Instant timestamp) {
        Date date = Date.from(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        String date1 = sdf.format(date);
        return date1;
    }

    public static Timestamp changeTimestampStringToTimestamp(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Timestamp timestamp1 = null;
        try {
            Date parsedDate = dateFormat.parse(timestamp);
            timestamp1 = new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            Logger4j.getLogger().error("Exception:" + e);
        }
        return timestamp1;

    }

    public static void getTaxformReport(Integer taxformId) {

    }

    public static Double calculateAge(Date birthDate, Date currentDate) {
        int years = 0;
        int months = 0;
        int days = 0;
        //create calendar object for birth day
        Calendar birthDay = Calendar.getInstance();
        birthDay.setTimeInMillis(birthDate.getTime());
        //create calendar object for current day
        /*long currentTime = System.currentTimeMillis();*/
        Calendar now = Calendar.getInstance();
        /*now.setTimeInMillis(currentDate.get);*/
        now.setTime(currentDate);
        //Get difference between years
        years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH) + 1;
        int birthMonth = birthDay.get(Calendar.MONTH) + 1;
        //Get difference between months
        months = currMonth - birthMonth;
        //if month difference is in negative then reduce years by one and calculate the number of months.
        if (months < 0) {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            years--;
            months = 11;
        }
        //Calculate the days
        if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
            days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
        else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE)) {
            int today = now.get(Calendar.DAY_OF_MONTH);
            now.add(Calendar.MONTH, -1);
            days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
        } else {
            days = 0;
            if (months == 12) {
                years++;
                months = 0;
            }
        }
        //Create new Age object
        /*return new Age(days, months, years);*/
        return Double.parseDouble(years + "");
    }

    public static Double allowedTaxableIncomePercentageOnPensionFunds(Double age) {
        if (age != null) {
            if (age.compareTo(40.0) <= 0) {
                return 20.0;
            }
            if (age.compareTo(40.0) > 0) {
                if (age.compareTo(41.0) == 0) {
                    return 22.0;
                }
                if (age.compareTo(42.0) == 0) {
                    return 24.0;
                }
                if (age.compareTo(43.0) == 0) {
                    return 26.0;
                }
                if (age.compareTo(44.0) == 0) {
                    return 28.0;
                }
                if (age.compareTo(45.0) == 0) {
                    return 30.0;
                }
                if (age.compareTo(45.0) > 0) {
                    return 30.0;
                }
            }
        }
        return 0.0;
    }

    public static Throwable getRootCause(Throwable throwable) {
        if (throwable.getCause() != null)
            return getRootCause(throwable.getCause());

        return throwable;
    }

    public static String sendMailForTaxformStatus(Taxform taxform) throws Exception {
        final boolean[] bool = {false};
        if (taxform != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String email = taxform.getEmail();
                    if (email != null && !email.isEmpty()) try {
                        bool[0] = EmailSender.sendEmail("Taxform status : " + taxform.getStatus().getStatus(), "", email);
                        MyPrint.println("sending Email::::::::::::::::::::: " + bool[0]);
                    } catch (Exception e) {
                        Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                        e.printStackTrace();
                    }
                }
            };
            return "email sending successfully: " + bool[0];
        }
        return "email sending failed: " + bool[0];
    }

    public static String sendMail(String[] emails, String subject, String body) throws Exception {
        Boolean[] bool = new Boolean[1];

        if (emails != null && subject != null && !subject.isEmpty() && body != null && !body.isEmpty()) {
            new Runnable() {
                @Override
                public void run() {
                    try {
                        bool[0] = EmailSender.sendEmail(body, subject, emails);
                        MyPrint.println("sending Email::::::::::::::::::::: " + bool[0]);
                    } catch (Exception e) {
                        Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                        e.printStackTrace();
                    }
                }
            };

            return "email sending successfully: " + bool[0];
        }
        return "email sending failed: " + bool[0];
    }

    public static String sendMail(String emails, String subject, String body) throws Exception {
        Boolean[] bool = new Boolean[1];

        if (emails != null && subject != null && !subject.isEmpty() && body != null && !body.isEmpty()) {
            new Runnable() {
                @Override
                public void run() {
                    try {
                        bool[0] = EmailSender.sendEmail(body, subject, emails);
                        MyPrint.println("sending Email::::::::::::::::::::: " + bool[0]);
                    } catch (Exception e) {
                        Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                        e.printStackTrace();
                    }
                }
            };

            return "email sending successfully: " + bool[0];
        }
        return "email sending failed: " + bool[0];
    }

    public static boolean getTimeStampDiffGreaterThenThreeDays(Timestamp first, Timestamp second) {
        // get time difference in seconds
        long milliseconds = first.getTime() - second.getTime();
        int seconds = (int) milliseconds / 1000;
        // calculate hours minutes and seconds
        int hours = seconds / 3600;
//        int minutes = (seconds % 3600) / 60;
//        seconds = (seconds % 3600) % 60;
        if (hours >= 72) {
            return true;
        }

        return false;
    }

    public static LinkedHashMap<Integer, Integer> sortHashMapByValues(HashMap<Integer, Integer> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Integer, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1 == comp2) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public static Date getStartDateOfCurrentMonth() {
        Date begining;
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        begining = calendar.getTime();

        return begining;
    }

    public static Date getLastDateOfCurrentMonth() {
        Date end;
        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        end = calendar.getTime();
        return end;
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }


}
