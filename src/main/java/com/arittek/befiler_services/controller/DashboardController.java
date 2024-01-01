package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.ExpensePieChartBean;
import com.arittek.befiler_services.beans.taxform.TaxformBean;
import com.arittek.befiler_services.beans.taxform.TaxformCalculationBean;
import com.arittek.befiler_services.beans.taxform.TaxformCalculationLineChartBean;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.taxform.Taxform_WelthStatement_DetailsOfPersonalExpense;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.TaxformYearsServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.TaxformCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/dashboard")
public class DashboardController {

    private TaxformServices taxformServices;
    private UsersServices usersServices;
    private TaxformYearsServices taxformYearsServices;

    @Autowired
    DashboardController(UsersServices usersServices, TaxformServices taxformServices, TaxformYearsServices taxformYearsServices) {
        this.taxformServices = taxformServices;
        this.usersServices = usersServices;
        this.taxformYearsServices = taxformYearsServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/expensesForPieChart", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<ExpensePieChartBean> getTaxformExpensesForPieChart(@RequestBody TaxformBean taxformBean) {
        Logger4j.getLogger().info("Inside Expense Pie Chart");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if (user != null && taxform != null) {
                    Taxform_WelthStatement_DetailsOfPersonalExpense expense = taxform.getTaxformWelthStatementDetailsOfPersonalExpense();

                    if (expense != null) {

                        Double rentPercentage = 0.0;
                        Double electricityPercentage = 0.0;
                        Double waterPercentage = 0.0;
                        Double gasPercentage = 0.0;
                        Double telephonePercentage = 0.0;
                        Double medicalPercentage = 0.0;
                        Double educationalPercentage = 0.0;
                        Double otherExpensesPercentage = 0.0;

                        Double totalExpenses = (expense.getRent() != null ? expense.getRent() : 0.0) +
                                (expense.getRatesTaxesChargeCess() != null ? expense.getRatesTaxesChargeCess() : 0.0) +
                                (expense.getVehicleRunningOrMaintenance() != null ? expense.getVehicleRunningOrMaintenance() : 0.0) +
                                (expense.getTravelling() != null ? expense.getTravelling() : 0.0) +
                                (expense.getElectricity() != null ? expense.getElectricity() : 0.0) +
                                (expense.getWater() != null ? expense.getWater() : 0.0) +
                                (expense.getGas() != null ? expense.getGas() : 0.0) +
                                (expense.getTelephone() != null ? expense.getTelephone() : 0.0) +
                                (expense.getAssetsInsuranceOrSecurity() != null ? expense.getAssetsInsuranceOrSecurity() : 0.0) +
                                (expense.getMedical() != null ? expense.getMedical() : 0.0) +
                                (expense.getEducational() != null ? expense.getEducational() : 0.0) +
                                (expense.getClub() != null ? expense.getClub() : 0.0) +
                                (expense.getFunctionsOrGatherings() != null ? expense.getFunctionsOrGatherings() : 0.0) +
                                (expense.getDonationZakatAnnuityProfitOnDebutEtc() != null ? expense.getDonationZakatAnnuityProfitOnDebutEtc() : 0.0) +
                                (expense.getOtherPersonalOrHouseholdExpense() != null ? expense.getOtherPersonalOrHouseholdExpense() : 0.0);

                        if (totalExpenses != null && totalExpenses.compareTo(0.0) > 0) {
                            rentPercentage = (expense.getRent() != null && expense.getRent().compareTo(0.0) > 0 ? (expense.getRent() / totalExpenses) * 100 : 0.0);
                            electricityPercentage = (expense.getElectricity() != null && expense.getElectricity().compareTo(0.0) > 0 ? (expense.getElectricity() / totalExpenses) * 100 : 0.0);
                            waterPercentage = (expense.getWater() != null && expense.getWater().compareTo(0.0) > 0 ? (expense.getWater() / totalExpenses) * 100 : 0.0);
                            gasPercentage = (expense.getGas() != null && expense.getGas().compareTo(0.0) > 0 ? (expense.getGas() / totalExpenses) * 100 : 0.0);
                            telephonePercentage = (expense.getTelephone() != null && expense.getTelephone().compareTo(0.0) > 0 ? (expense.getTelephone() / totalExpenses) * 100 : 0.0);
                            medicalPercentage = (expense.getMedical() != null && expense.getMedical().compareTo(0.0) > 0 ? (expense.getMedical() / totalExpenses) * 100 : 0.0);
                            educationalPercentage = (expense.getEducational() != null && expense.getEducational().compareTo(0.0) > 0 ? (expense.getEducational() / totalExpenses) * 100 : 0.0);
                            Double otherExpenses = (expense.getRatesTaxesChargeCess() != null ? expense.getRatesTaxesChargeCess() : 0.0) +
                                    (expense.getVehicleRunningOrMaintenance() != null ? expense.getVehicleRunningOrMaintenance() : 0.0) +
                                    (expense.getTravelling() != null ? expense.getTravelling() : 0.0) +
                                    (expense.getAssetsInsuranceOrSecurity() != null ? expense.getAssetsInsuranceOrSecurity() : 0.0) +
                                    (expense.getClub() != null ? expense.getClub() : 0.0) +
                                    (expense.getFunctionsOrGatherings() != null ? expense.getFunctionsOrGatherings() : 0.0) +
                                    (expense.getDonationZakatAnnuityProfitOnDebutEtc() != null ? expense.getDonationZakatAnnuityProfitOnDebutEtc() : 0.0) +
                                    (expense.getOtherPersonalOrHouseholdExpense() != null ? expense.getOtherPersonalOrHouseholdExpense() : 0.0);
                            if (otherExpenses != null && otherExpenses.compareTo(0.0) > 0)
                                otherExpensesPercentage = (otherExpenses / totalExpenses) * 100;
                        }

                        ExpensePieChartBean expensePieChartBean = new ExpensePieChartBean();

                        expensePieChartBean.setRentPercentage(rentPercentage);
                        expensePieChartBean.setElectricityPercentage(electricityPercentage);
                        expensePieChartBean.setWaterPercentage(waterPercentage);
                        expensePieChartBean.setGasPercentage(gasPercentage);
                        expensePieChartBean.setTelephonePercentage(telephonePercentage);
                        expensePieChartBean.setMedicalPercentage(medicalPercentage);
                        expensePieChartBean.setEducationalPercentage(educationalPercentage);
                        expensePieChartBean.setOtherExpensesPercentage(otherExpensesPercentage);

                        expensePieChartBean.setCode(1);
                        expensePieChartBean.setMessage("Successfull");

                        return new ResponseEntity<>(expensePieChartBean, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ExpensePieChartBean(1, "No Expenses Found"), HttpStatus.OK);
                    }
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<>(new ExpensePieChartBean(0, "Exception : " + CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ExpensePieChartBean(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ExpensePieChartBean(0, "Incomplete Data"), HttpStatus.OK);

    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/lineChart", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Map<String, TaxformCalculationLineChartBean>> getTaxformExpensesForLineChart(@RequestBody TaxformBean taxformBean) {
        Map<String, TaxformCalculationLineChartBean> lineChartJson = new HashMap<String, TaxformCalculationLineChartBean>();
        if (taxformBean != null && taxformBean.getUserId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);

                if (user != null) {

                    List<Taxform> taxformList = taxformServices.findAllByUserId(user.getId());
                    if (taxformList != null) {
                        for (Taxform taxform : taxformList) {
                            if (taxform.getStatus() != null && taxform.getStatus().getId() != 0 && taxform.getTaxformYear() != null) {
                                TaxformCalculationBean taxformCalculationBean = TaxformCalculator.taxformCalculator(taxform);

                                TaxformCalculationLineChartBean taxformCalculationLineChartBean = new TaxformCalculationLineChartBean();

                                taxformCalculationLineChartBean.setIncome(taxformCalculationBean.getTaxableIncome());
                                taxformCalculationLineChartBean.setCredit(taxformCalculationBean.getTaxCredit());
                                taxformCalculationLineChartBean.setTaxDeducted(taxformCalculationBean.getWithholdingTax());
                                taxformCalculationLineChartBean.setWealthStatement(taxformCalculationBean.getWealthStatement());

                                lineChartJson.put(taxform.getTaxformYear().getYear() + "", taxformCalculationLineChartBean);
                            }
                        }
                    }
                    return new ResponseEntity<Map<String, TaxformCalculationLineChartBean>>(lineChartJson, HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<Map<String, TaxformCalculationLineChartBean>>(lineChartJson, HttpStatus.OK);
            }
            return new ResponseEntity<Map<String, TaxformCalculationLineChartBean>>(lineChartJson, HttpStatus.OK);
        }
        return new ResponseEntity<Map<String, TaxformCalculationLineChartBean>>(lineChartJson, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/barChart", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Map<String, TaxformCalculationLineChartBean>> getTaxformExpensesForPieCharts(@RequestBody TaxformBean taxformBean) {
        Map<String, TaxformCalculationLineChartBean> lineChartJson = new HashMap<String, TaxformCalculationLineChartBean>();
        if (taxformBean != null && taxformBean.getUserId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);

                if (user != null) {
                    List<TaxformYears> taxformList = taxformYearsServices.findAllActiveYears();
                    if (taxformList != null) {
                        for (TaxformYears taxformYears : taxformList) {
                            Taxform taxform = taxformServices.findOneByTaxformYearAndUser(taxformYears, user);
                            if (taxform != null && taxform.getStatus() != null && taxform.getStatus().getId() != 0 && taxform.getTaxformYear() != null) {
                                TaxformCalculationBean taxformCalculationBean = TaxformCalculator.taxformCalculator(taxform);

                                TaxformCalculationLineChartBean taxformCalculationLineChartBean = new TaxformCalculationLineChartBean();

                                taxformCalculationLineChartBean.setIncome(taxformCalculationBean.getTaxableIncome());
                                taxformCalculationLineChartBean.setCredit(taxformCalculationBean.getTaxCredit());
                                taxformCalculationLineChartBean.setTaxDeducted(taxformCalculationBean.getWithholdingTax());
                                taxformCalculationLineChartBean.setWealthStatement(taxformCalculationBean.getWealthStatement());

                                lineChartJson.put(taxform.getTaxformYear().getYear() + "", taxformCalculationLineChartBean);
                            }
                        }
                    }
                    return new ResponseEntity<>(lineChartJson, HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : ", e);
                return new ResponseEntity<>(lineChartJson, HttpStatus.OK);
            }
            return new ResponseEntity<>(lineChartJson, HttpStatus.OK);
        }
        return new ResponseEntity<>(lineChartJson, HttpStatus.OK);
    }
}

