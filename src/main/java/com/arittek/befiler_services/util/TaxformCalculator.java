package com.arittek.befiler_services.util;

import com.arittek.befiler_services.beans.taxform.TaxformCalculationBean;
import com.arittek.befiler_services.beans.taxform.TaxformCalculationValuesBean;
import com.arittek.befiler_services.model.taxform.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaxformCalculator {
    public static synchronized TaxformCalculationBean taxformCalculator(Taxform taxform) throws Exception{
        TaxformCalculationBean taxformCalculationBean = new TaxformCalculationBean();
        TaxformCalculationValuesBean taxformCalculationValuesBean = CommonUtil.getTaxformCalculationValuesByYear(taxform);

        Boolean condition = true;

        Integer continueConditionForRentOnHouseLoan = 1;
        Integer continueConditionForEducationAllowance = 1;
        Integer continueConditionForPensionFunds = 1;
        Integer continueConditionForDonationsUnderClause61= 1;

        Double interestOrRentOnHouseLoanTaxCharge = 0.0;
        Double educationAllownaceTaxCharge = 0.0;
        Double pensionFundsTaxCharge = 0.0;
        Double donationsUnderClause61TaxCharge = 0.0;

        Double normalTaxableIncome = 0.0;
        Double exemptTaxableIncome = 0.0;
        Double fixedTaxableIncome = 0.0;

        while (condition) {

            Double taxableIncome = 0.0;
            Double withholdingTax = 0.0;
            Double taxCharge = 0.0;

            Double taxableIncomeForCalculation = 0.0;
            Double taxRateForCalculation = 0.0;
            Double taxCredit = 0.0;

            Double taxableIncomeForWelthStatement = 0.0;
            Double withholdingTaxForWelthStatement = 0.0;

            MyPrint.println("========================INCOME TAX -> SALARY========================");

            if (taxform.getTaxformIncomeTaxSalary() != null) {
                Double salaryTaxableIncome = 0.0;
                Double salaryWithholdingTax = 0.0;
                Double salaryTaxCharge = 0.0;

                Taxform_IncomeTax_Salary salary = taxform.getTaxformIncomeTaxSalary();

                if(salary.getSalaryTaxWithheldByEmployeer() != null) {
                    salaryWithholdingTax += salary.getSalaryTaxWithheldByEmployeer();
                }

                Double salaryIncome = 0.0;

                Double basicSalary = 0.0;
                Double medicalAllowance = 0.0;
                Double otherAllowance = 0.0;
                Double providentFundByEmployeer = 0.0;
                Double providentOrGratuityFundReceived = 0.0;

                if (salary.getBasicSalary() != null) {
                    MyPrint.println("Basic Salary: " + salary.getBasicSalary());
                    basicSalary = salary.getBasicSalary();
                }

                if (salary.getMedicalAllowance() != null) {
                    MyPrint.println("Medical Allowance: " + salary.getMedicalAllowance());
                    medicalAllowance = salary.getMedicalAllowance();
                }

                if (salary.getOtherAllowance() != null) {
                    MyPrint.println("Other Allowance: " + salary.getOtherAllowance());
                    otherAllowance = salary.getOtherAllowance();
                }

                if (salary.getProvidentFundByEmployeer() != null) {
                    MyPrint.println("Provident Fund By Employer: " + salary.getProvidentFundByEmployeer());
                    providentFundByEmployeer = salary.getProvidentFundByEmployeer();
                }

                if (salary.getProvidentOrGratuityFundReceived() != null) {
                    MyPrint.println("Provident/Gratuity Fund Received: " + salary.getProvidentOrGratuityFundReceived());
                    providentOrGratuityFundReceived = salary.getProvidentOrGratuityFundReceived();
                }


                taxableIncomeForWelthStatement += basicSalary;
                taxableIncomeForWelthStatement += medicalAllowance;
                taxableIncomeForWelthStatement += otherAllowance;
                taxableIncomeForWelthStatement += providentFundByEmployeer;
                taxableIncomeForWelthStatement += providentOrGratuityFundReceived;

                normalTaxableIncome += basicSalary;
                normalTaxableIncome += otherAllowance;

                salaryIncome += basicSalary;
                MyPrint.println("Salary Taxable Income : Basic Salary::::" + salaryIncome);
                taxformCalculationBean.setBasicSalary(basicSalary);

                if (taxform.getTaxformIncomeTaxSalary().getTada() != null) {
                    taxableIncomeForWelthStatement += taxform.getTaxformIncomeTaxSalary().getTada();
                }


                if (medicalAllowance > 0 && medicalAllowance > CommonUtil.dashPercentOfValue(10.0,basicSalary)) {
                    salaryIncome += medicalAllowance - CommonUtil.dashPercentOfValue(10.0,basicSalary);
                    normalTaxableIncome += medicalAllowance - CommonUtil.dashPercentOfValue(10.0,basicSalary);
                    exemptTaxableIncome += CommonUtil.dashPercentOfValue(10.0,basicSalary);
                    MyPrint.println("Salary Taxable Income : If Medical Allowance is > 10% of Salary::::" + salaryIncome);
                } else {
                    MyPrint.println("Salary Taxable Income : If Medical Allowance is < 10% of Salary::::" + salaryIncome);
                    exemptTaxableIncome += medicalAllowance;
                }

                if (providentFundByEmployeer > 0.0 && providentFundByEmployeer > 150000) {
                    salaryIncome += providentFundByEmployeer - 150000;
                    normalTaxableIncome += providentFundByEmployeer - 150000;
                    exemptTaxableIncome += 150000;
                    MyPrint.println("Salary Taxable Income : If Provident Fund By Employeer is < 150000 of Salary::::" + salaryIncome);
                } else {
                    MyPrint.println("Salary Taxable Income : If Provident Fund By Employeer is < 150000 of Salary::::" + salaryIncome);
                    exemptTaxableIncome += providentFundByEmployeer;
                }

                salaryIncome += otherAllowance;
                MyPrint.println("Salary Taxable Income : Other Allowance::::" + salaryIncome);

                exemptTaxableIncome += providentOrGratuityFundReceived;

                if (salary.getCompanyVehicleCost() != null && salary.getCompanyVehicleCost() > 0) {
                    MyPrint.println("Vehicle Cost:::" + salary.getCompanyVehicleCost());
                    Double fivePercentOfVehicleCost = CommonUtil.dashPercentOfValue(5.0,salary.getCompanyVehicleCost());
                    MyPrint.println("5% of Vehicle Cost:::" + fivePercentOfVehicleCost);
                    if (salary.getCompanyVehicleReceivedAfterJuly() != null && salary.getCompanyVehicleReceivedAfterJuly() == false) {
                        salaryIncome += fivePercentOfVehicleCost;
                        normalTaxableIncome += fivePercentOfVehicleCost;
                        taxformCalculationBean.setVehicleAllowance(fivePercentOfVehicleCost);
                        MyPrint.println("Salary Taxable Income : If Vechicle Received Before July 2016::::" + salaryIncome);
                    } else if (salary.getCompanyVehicleReceivedAfterJuly() != null && salary.getCompanyVehicleReceivedAfterJuly() == true && salary.getCompanyVehicleReceivedDate() != null) {
                        Long days = CommonUtil.daysBetweenTwoDates(salary.getCompanyVehicleReceivedDate(), CommonUtil.parseDate("06/30/" + taxform.getTaxformYear().getYear()));
                        System.out.println("Days:::: " + days);
                        salaryIncome += (fivePercentOfVehicleCost / 365) * days;
                        normalTaxableIncome += (fivePercentOfVehicleCost / 365) * days;
                        taxformCalculationBean.setVehicleAllowance((fivePercentOfVehicleCost / 365) * days);
                        MyPrint.println("Salary Taxable Income : If Vechicle Received After July 2016: Date:" + CommonUtil.parseDate("06/30/" + taxform.getTaxformYear().getYear()) + "::::" + salaryIncome);
                    }
                }

                MyPrint.println("Salary Income :::"  + salaryIncome);

                if (taxform.getTaxformIncomeTaxSalary().getSalaryTaxBorneByEmployeerCheck() != null && taxform.getTaxformIncomeTaxSalary().getSalaryTaxBorneByEmployeerCheck()) {
                    MyPrint.println("If tax is borne by employeer");
                    Double taxOnTax = 0.0;
                    Double finalSalary = salaryIncome;
                    Double tempSalary = finalSalary;
                    Boolean check = true;
                    MyPrint.println("Salary ::: " + finalSalary);
                    int i = 0;
                    do {
                        i++;
                        MyPrint.println(":::::::::::::::::::::::::::::");
                        MyPrint.println(i + " time");
                        MyPrint.println("Salary:::" + tempSalary);
                        Double salaryTax = CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxform.getTaxformYear().getYear(), tempSalary);
                        MyPrint.println("Tax:::" + salaryTax);
                        Double grossUp = finalSalary + salaryTax;
                        MyPrint.println("GrossUp:::" + grossUp);
                        if (tempSalary.compareTo(grossUp) == 0) {
                            taxOnTax += grossUp;
                            MyPrint.println("Inside");
                            check = false;
                        }
                        tempSalary = grossUp;
                        MyPrint.println(":::::::::::::::::::::::::::::");
                    } while (check);

                    MyPrint.println("Tax On Tax:::" + taxOnTax);
                    taxformCalculationBean.setTaxOnTax(taxOnTax);
                    MyPrint.println("Tax:::" + CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxform.getTaxformYear().getYear(), taxOnTax));
                    taxableIncomeForWelthStatement += taxOnTax - salaryIncome;

                    taxableIncomeForCalculation += taxOnTax;
                } else {
                    MyPrint.println("If Salary Tax is not Borne by Employeer");
                                /*taxableIncomeForWelthStatement += salaryIncome;*/
                    taxableIncomeForCalculation += salaryIncome;
                }

                MyPrint.println("Taxable Income for Calculation" + taxableIncomeForCalculation);

                if (/*continueConditionForPensionFunds == 2 && */pensionFundsTaxCharge != null && pensionFundsTaxCharge.compareTo(0.0) != 0) {
                    taxableIncomeForCalculation += pensionFundsTaxCharge;
                    taxformCalculationBean.setPensionFundWithdrawal(pensionFundsTaxCharge + "");
                    MyPrint.println("Pension Funds  :::" + pensionFundsTaxCharge);
                    MyPrint.println("After Addeding pension funds from salary:::" + taxableIncomeForCalculation);
                    continueConditionForPensionFunds = 3;
                }

                Double deductableAllowances = 0.0;

                if (/*continueConditionForDonationsUnderClause61 == 2 && */donationsUnderClause61TaxCharge != null && donationsUnderClause61TaxCharge.compareTo(0.0) != 0) {
                    taxableIncomeForCalculation -= donationsUnderClause61TaxCharge;
                    deductableAllowances += donationsUnderClause61TaxCharge;
                    MyPrint.println("Donation Under Clause 61 Part 1 of 2nd Schedule :::" + donationsUnderClause61TaxCharge);
                    MyPrint.println("After Deducting Donation from salary:::" + taxableIncomeForCalculation);
                    continueConditionForDonationsUnderClause61 = 3;
                }

                if (/*continueConditionForRentOnHouseLoan == 2 && */interestOrRentOnHouseLoanTaxCharge != null && interestOrRentOnHouseLoanTaxCharge.compareTo(0.0) != 0) {
                    taxableIncomeForCalculation -= interestOrRentOnHouseLoanTaxCharge;
                    deductableAllowances += interestOrRentOnHouseLoanTaxCharge;
                    MyPrint.println("Interest or Rent On House Loan :::" + interestOrRentOnHouseLoanTaxCharge);
                    MyPrint.println("After Deducting Rent on house loan from salary:::" + taxableIncomeForCalculation);
                    continueConditionForRentOnHouseLoan = 3;
                }

                if (/*continueConditionForEducationAllowance == 2 &&*/ educationAllownaceTaxCharge != null && educationAllownaceTaxCharge.compareTo(0.0) != 0) {
                    taxableIncomeForCalculation -= educationAllownaceTaxCharge;
                    deductableAllowances += educationAllownaceTaxCharge;
                    MyPrint.println("Education Allowance :::" + educationAllownaceTaxCharge);
                    MyPrint.println("After Deducting Rent on house loan from salary:::" + taxableIncomeForCalculation);
                    continueConditionForEducationAllowance = 3;
                }
                taxformCalculationBean.setDeductableAllowances("-" + deductableAllowances);

                MyPrint.println("Taxable Income for Calculation" + taxableIncomeForCalculation);

                salaryTaxableIncome += taxableIncomeForCalculation;
                salaryTaxCharge += CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxform.getTaxformYear().getYear(), taxableIncomeForCalculation);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date d = sdf.parse("30/06/" + taxform.getTaxformYear().getYear());
                Double age1 = CommonUtil.calculateAge(taxform.getDateOfBirth(), d);
                MyPrint.println("Age ::::: " + age1);

                if (age1.compareTo(60.0) >= 0 && taxableIncomeForCalculation.compareTo(1000000.0) < 0) {
                    MyPrint.println("If age is greater than 60 and taxable income is less than 1 million");
                    salaryTaxCharge -= CommonUtil.dashPercentOfValue(50.0, salaryTaxCharge);
                    MyPrint.println("Salary Tax Charge for Calculation after age " + salaryTaxCharge);
                }

                if (taxform.getOccupation() != null && taxform.getOccupation().trim().equalsIgnoreCase("Employee")) {
                    MyPrint.println("If employee is full-time researcher or teacher");
                    salaryTaxCharge -= CommonUtil.dashPercentOfValue(40.0, salaryTaxCharge);
                    MyPrint.println("Salary Tax Charge for Calculation Full Time Researcher/Teacher" + salaryTaxCharge);
                }

                taxRateForCalculation = (salaryTaxCharge / taxableIncomeForCalculation) * 100;

                MyPrint.println("Tax Rate for Calculation  " + taxRateForCalculation);

                            /*salaryTaxableIncome += finalTaxableSalary;*/
                MyPrint.println("Taxable Income : Income Tax -> Salary" + salaryTaxableIncome);
                MyPrint.println("Withhold Tax : Income Tax -> Salary" + salaryWithholdingTax);
                MyPrint.println("Tax Charge : Income Tax -> Salary" + salaryTaxCharge);
                MyPrint.println("Salary TaxableIncome Calculation::" + (salaryTaxableIncome - (pensionFundsTaxCharge != null ? pensionFundsTaxCharge : 0.0) + deductableAllowances));
                taxformCalculationBean.setSalaryTaxableIncome(""+(salaryTaxableIncome - (pensionFundsTaxCharge != null ? pensionFundsTaxCharge : 0.0) + deductableAllowances));
                taxformCalculationBean.setSalaryWithholdingTax(""+salaryWithholdingTax);
                taxformCalculationBean.setSalaryTaxCharge(""+salaryTaxCharge);

                taxformCalculationBean.setTaxableIncomeForCalculation(taxableIncomeForCalculation);
                taxformCalculationBean.setTaxRateForCalculation(taxRateForCalculation);

                taxableIncome += salaryTaxableIncome;
                withholdingTax += salaryWithholdingTax;
                withholdingTaxForWelthStatement += salaryWithholdingTax;
                taxCharge += salaryTaxCharge;

                            /*MyPrint.println(":::::::::::::::::::::::::::::");
                            MyPrint.println(":::::::::::::::::::::::::::::");
                            MyPrint.println(":::::::::::::::::::::::::::::");*/

                            /*Double salaryTaxBySalaryTaxSlab = CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(salaryTaxableIncome);
                            MyPrint.println("Salary Tax According Tax Slab:::::" + salaryTaxBySalaryTaxSlab);
                            Double taxRate = (salaryTaxBySalaryTaxSlab/salaryTaxableIncome) * 100;
                            MyPrint.println("Tax Rate:::::" + taxRate);
                            Double grossUp = (salaryTaxableIncome/(100-taxRate) * 100);
                            MyPrint.println("Gross Up:::::" + grossUp);
                            Double salaryTaxBorneByEmployeer = grossUp - salaryTaxableIncome;
                            MyPrint.println("Salary Tax Borne By Employeer:::::" + salaryTaxBorneByEmployeer);

                            taxableIncome += salaryTaxableIncome;*/
            }

            MyPrint.println("========================INCOME TAX -> PROPERTY========================");
            if (taxform.getTaxformIncomeTaxProperty() != null) {
                Double propertyTaxableIncome = 0.0;
                Double propertyWithholdingTax = 0.0;
                Double propertyTaxCharge = 0.0;

                Taxform_IncomeTax_Property property = taxform.getTaxformIncomeTaxProperty();

                if (property.getRentReceivedFromYourProperty() != null){
                    propertyTaxableIncome += property.getRentReceivedFromYourProperty();
                    normalTaxableIncome += property.getRentReceivedFromYourProperty();
                }

                if (property.getDoYouDeductAnyTax() != null && property.getDoYouDeductAnyTax() == true && property.getPropertyTax() != null) {
                    propertyWithholdingTax += property.getPropertyTax();
                }

                propertyTaxCharge += CommonUtil.getTaxRateFromPropertyRentSlab(property.getRentReceivedFromYourProperty());

                MyPrint.println("Taxable Income : Income Tax -> Property" + propertyTaxableIncome);
                MyPrint.println("Withhold Tax : Income Tax -> Property" + propertyWithholdingTax);
                MyPrint.println("Tax Charge : Income Tax -> Property" + propertyTaxCharge);

                taxableIncomeForWelthStatement += propertyTaxableIncome;
                taxableIncome += propertyTaxableIncome;
                withholdingTax += propertyWithholdingTax;
                withholdingTaxForWelthStatement += propertyWithholdingTax;
                taxCharge += propertyTaxCharge;

                taxformCalculationBean.setPropertyTaxableIncome(propertyTaxableIncome + "");
                taxformCalculationBean.setPropertyWithholdingTax(propertyWithholdingTax + "");
                taxformCalculationBean.setPropertyTaxCharge(propertyTaxCharge + "");
            }

            MyPrint.println("========================INCOME TAX -> CAPITAL GAIN -> On Shares========================");
            if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null) {
                Double capitalGainOnShareTaxableIncome = 0.0;
                Double capitalGainOnShareWithholdingTax = 0.0;
                Double capitalGainOnShareTaxCharge = 0.0;

                Taxform_IncomeTax_CapitalGain_OnShare tax_capitalGain_onShare = taxform.getTaxformIncomeTaxCapitalGainOnShare();
                List<Double> percentagesForCapitalGainOnShare = CommonUtil.getCapitalGainOnDisposalOfSecurities(taxform.getTaxformYear().getYear() + "");

                if (percentagesForCapitalGainOnShare != null) {
                    if (tax_capitalGain_onShare.getLessThan12MonthsFieldsCapitalGain() != null) {
                        capitalGainOnShareTaxableIncome += tax_capitalGain_onShare.getLessThan12MonthsFieldsCapitalGain();
                        capitalGainOnShareTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), tax_capitalGain_onShare.getLessThan12MonthsFieldsCapitalGain());
                    }

                    if (tax_capitalGain_onShare.getLessThan12MonthsTaxDeducted() != null) {
                        capitalGainOnShareWithholdingTax += tax_capitalGain_onShare.getLessThan12MonthsTaxDeducted();
                    }

                    if (tax_capitalGain_onShare.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null) {
                        capitalGainOnShareTaxableIncome += tax_capitalGain_onShare.getMoreThan12ButLessThan24MonthsFieldsCapitalGain();
                        capitalGainOnShareTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(1), tax_capitalGain_onShare.getMoreThan12ButLessThan24MonthsFieldsCapitalGain());
                    }

                    if (tax_capitalGain_onShare.getMoreThan12ButLessThan24MonthsTaxDeducted() != null) {
                        capitalGainOnShareWithholdingTax += tax_capitalGain_onShare.getMoreThan12ButLessThan24MonthsTaxDeducted();
                    }

                    if (tax_capitalGain_onShare.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null) {
                        capitalGainOnShareTaxableIncome += tax_capitalGain_onShare.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain();
                        capitalGainOnShareTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(2), tax_capitalGain_onShare.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain());
                    }

                    if (tax_capitalGain_onShare.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null) {
                        capitalGainOnShareWithholdingTax += tax_capitalGain_onShare.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted();
                    }

                    if (tax_capitalGain_onShare.getAquiredBefore1July2012FieldsCapitalGain() != null) {
                        capitalGainOnShareTaxableIncome += tax_capitalGain_onShare.getAquiredBefore1July2012FieldsCapitalGain();
                        capitalGainOnShareTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(3), tax_capitalGain_onShare.getAquiredBefore1July2012FieldsCapitalGain());
                    }

                    if (tax_capitalGain_onShare.getAquiredBefore1July2012TaxDeducted() != null) {
                        capitalGainOnShareWithholdingTax += tax_capitalGain_onShare.getAquiredBefore1July2012TaxDeducted();
                    }

                    if ((taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                        if (tax_capitalGain_onShare.getAcquiredOnOrAfter1JulyCapitalGain() != null) {
                            capitalGainOnShareTaxableIncome += tax_capitalGain_onShare.getAcquiredOnOrAfter1JulyCapitalGain();
                            capitalGainOnShareTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), tax_capitalGain_onShare.getAcquiredOnOrAfter1JulyCapitalGain());
                        }

                        if (tax_capitalGain_onShare.getAcquiredOnOrAfter1JulyTaxDeducted() != null) {
                            capitalGainOnShareWithholdingTax += tax_capitalGain_onShare.getAcquiredOnOrAfter1JulyTaxDeducted();
                        }
                    }
                }

                MyPrint.println("Taxable Income : Capital Gain -> On Share" + capitalGainOnShareTaxableIncome);
                MyPrint.println("Withhold Tax : Capital Gain -> On Share" + capitalGainOnShareWithholdingTax);
                MyPrint.println("Tax Charge : Capital Gain -> On Share" + capitalGainOnShareTaxCharge);

                taxableIncomeForWelthStatement += capitalGainOnShareTaxableIncome;
                taxableIncome += capitalGainOnShareTaxableIncome;
                withholdingTax += capitalGainOnShareWithholdingTax;
                withholdingTaxForWelthStatement += capitalGainOnShareWithholdingTax;
                taxCharge += capitalGainOnShareTaxCharge;

                taxformCalculationBean.setOnShareTaxableIncome(capitalGainOnShareTaxableIncome + "");
                taxformCalculationBean.setOnShareWithholdingTax(capitalGainOnShareWithholdingTax + "");
                taxformCalculationBean.setOnShareTaxCharge(capitalGainOnShareTaxCharge + "");

            }

            MyPrint.println("========================INCOME TAX -> CAPITAL GAIN -> MUTUAL FUNDS========================");

            if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null) {
                Double capitalGainMutualFundsTaxableIncome = 0.0;
                Double capitalGainMutualFundsWithholdingTax = 0.0;
                Double capitalGainMutualFundsTaxCharge = 0.0;

                Taxform_IncomeTax_CapitalGain_MutualFinds tax_capitalGain_mutualFinds = taxform.getTaxformIncomeTaxCapitalGainMutualFinds();
                List<Double> percentagesForCapitalGainMutualFund = CommonUtil.getCapitalGainOnDisposalOfSecurities(taxform.getTaxformYear().getYear() + "");

                if (percentagesForCapitalGainMutualFund != null) {

                    if (tax_capitalGain_mutualFinds.getLessThan12MonthsFieldsCapitalGain() != null) {
                        capitalGainMutualFundsTaxableIncome += tax_capitalGain_mutualFinds.getLessThan12MonthsFieldsCapitalGain();
                        capitalGainMutualFundsTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainMutualFund.get(0), tax_capitalGain_mutualFinds.getLessThan12MonthsFieldsCapitalGain());
                    }
                    if (tax_capitalGain_mutualFinds.getLessThan12MonthsTaxDeducted() != null) {
                        capitalGainMutualFundsWithholdingTax += tax_capitalGain_mutualFinds.getLessThan12MonthsTaxDeducted();
                    }

                    if (tax_capitalGain_mutualFinds.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null) {
                        capitalGainMutualFundsTaxableIncome += tax_capitalGain_mutualFinds.getMoreThan12ButLessThan24MonthsFieldsCapitalGain();
                        capitalGainMutualFundsTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainMutualFund.get(1), tax_capitalGain_mutualFinds.getMoreThan12ButLessThan24MonthsFieldsCapitalGain());
                    }
                    if (tax_capitalGain_mutualFinds.getMoreThan12ButLessThan24MonthsTaxDeducted() != null) {
                        capitalGainMutualFundsWithholdingTax += tax_capitalGain_mutualFinds.getMoreThan12ButLessThan24MonthsTaxDeducted();
                    }

                    if (tax_capitalGain_mutualFinds.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null) {
                        capitalGainMutualFundsTaxableIncome += tax_capitalGain_mutualFinds.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain();
                        capitalGainMutualFundsTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainMutualFund.get(2), tax_capitalGain_mutualFinds.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain());
                    }
                    if (tax_capitalGain_mutualFinds.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null) {
                        capitalGainMutualFundsWithholdingTax += tax_capitalGain_mutualFinds.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted();
                    }

                    if (tax_capitalGain_mutualFinds.getAquiredBefore1July2012FieldsCapitalGain() != null) {
                        capitalGainMutualFundsTaxableIncome += tax_capitalGain_mutualFinds.getAquiredBefore1July2012FieldsCapitalGain();
                        capitalGainMutualFundsTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainMutualFund.get(3), tax_capitalGain_mutualFinds.getAquiredBefore1July2012FieldsCapitalGain());
                    }
                    if (tax_capitalGain_mutualFinds.getAquiredBefore1July2012TaxDeducted() != null) {
                        capitalGainMutualFundsWithholdingTax += tax_capitalGain_mutualFinds.getAquiredBefore1July2012TaxDeducted();
                    }

                    if ((taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                        if (tax_capitalGain_mutualFinds.getAcquiredOnOrAfter1JulyCapitalGain() != null) {
                            capitalGainMutualFundsTaxableIncome += tax_capitalGain_mutualFinds.getAcquiredOnOrAfter1JulyCapitalGain();
                            capitalGainMutualFundsTaxCharge += CommonUtil.dashPercentOfValue(percentagesForCapitalGainMutualFund.get(0), tax_capitalGain_mutualFinds.getAcquiredOnOrAfter1JulyCapitalGain());
                        }

                        if (tax_capitalGain_mutualFinds.getAcquiredOnOrAfter1JulyTaxDeducted() != null) {
                            capitalGainMutualFundsWithholdingTax += tax_capitalGain_mutualFinds.getAcquiredOnOrAfter1JulyTaxDeducted();
                        }
                    }
                }
                MyPrint.println("Taxable Income : Capital Gain -> Mutual Funds" + capitalGainMutualFundsTaxableIncome);
                MyPrint.println("Withhold Tax : Capital Gain -> Mutual Funds" + capitalGainMutualFundsWithholdingTax);
                MyPrint.println("Tax Charge : Capital Gain -> Mutual Funds" + capitalGainMutualFundsTaxCharge);


                taxableIncomeForWelthStatement += capitalGainMutualFundsTaxableIncome;
                taxableIncome += capitalGainMutualFundsTaxableIncome;
                withholdingTax += capitalGainMutualFundsWithholdingTax;
                withholdingTaxForWelthStatement += capitalGainMutualFundsWithholdingTax;
                taxCharge += capitalGainMutualFundsTaxCharge;

                taxformCalculationBean.setMutualFundsTaxableIncome(capitalGainMutualFundsTaxableIncome + "");
                taxformCalculationBean.setMutualFundsWithholdingTax(capitalGainMutualFundsWithholdingTax + "");
                taxformCalculationBean.setMutualFundsTaxCharge(capitalGainMutualFundsTaxCharge + "");

            }

            MyPrint.println("========================INCOME TAX -> CAPITAL GAIN -> PROPERTY========================");

            if (taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {
                Double capitalGainPropertyTaxableIncome = 0.0;
                Double capitalGainPropertyWithholdingTax = 0.0;
                Double capitalGainPropertyTaxCharge = 0.0;

                Taxform_IncomeTax_CapitalGain_Property tax_capitalGain_property = taxform.getTaxformIncomeTaxCapitalGainProperty();

                if (tax_capitalGain_property.getBefore1JulyAndUpto3YearsPurchaseCost() != null && tax_capitalGain_property.getBefore1JulyAndUpto3YearsSaleCost() != null && tax_capitalGain_property.getBefore1JulyAndUpto3YearsSaleCost() > tax_capitalGain_property.getBefore1JulyAndUpto3YearsPurchaseCost()) {
                    capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getBefore1JulyAndUpto3YearsSaleCost() - tax_capitalGain_property.getBefore1JulyAndUpto3YearsPurchaseCost());
                    capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(5.0, (tax_capitalGain_property.getBefore1JulyAndUpto3YearsSaleCost() - tax_capitalGain_property.getBefore1JulyAndUpto3YearsPurchaseCost()));
                }

                if (tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsPurchaseCost() != null && tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsSaleCost() != null && (tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsSaleCost() > tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsPurchaseCost())) {
                    capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsSaleCost() - tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsPurchaseCost());
                    capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(0.0, (tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsSaleCost() - tax_capitalGain_property.getBefore1JulyAndMoreThan3YearsPurchaseCost()));
                }

                if ((taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                    if (tax_capitalGain_property.getAfter1JulyUpto1YearPurchaseCost() != null && tax_capitalGain_property.getAfter1JulyUpto1YearSaleCost() != null && (tax_capitalGain_property.getAfter1JulyUpto1YearSaleCost() > tax_capitalGain_property.getAfter1JulyUpto1YearPurchaseCost())) {
                        capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getAfter1JulyUpto1YearSaleCost() - tax_capitalGain_property.getAfter1JulyUpto1YearPurchaseCost());
                        capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(10.0, (tax_capitalGain_property.getAfter1JulyUpto1YearSaleCost() - tax_capitalGain_property.getAfter1JulyUpto1YearPurchaseCost()));
                    }

                    if (tax_capitalGain_property.getAfter1July1To2YearsPurchaseCost() != null && tax_capitalGain_property.getAfter1July1To2YearsSaleCost() != null && (tax_capitalGain_property.getAfter1July1To2YearsSaleCost() > tax_capitalGain_property.getAfter1July1To2YearsPurchaseCost())) {
                        capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getAfter1July1To2YearsSaleCost() - tax_capitalGain_property.getAfter1July1To2YearsPurchaseCost());
                        capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(7.5, (tax_capitalGain_property.getAfter1July1To2YearsSaleCost() - tax_capitalGain_property.getAfter1July1To2YearsPurchaseCost()));
                    }

                    if (tax_capitalGain_property.getAfter1July2To3YearsPurchaseCost() != null && tax_capitalGain_property.getAfter1July2To3YearsSaleCost() != null && (tax_capitalGain_property.getAfter1July2To3YearsSaleCost() > tax_capitalGain_property.getAfter1July2To3YearsPurchaseCost())) {
                        capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getAfter1July2To3YearsSaleCost() - tax_capitalGain_property.getAfter1July2To3YearsPurchaseCost());
                        capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(5.0, (tax_capitalGain_property.getAfter1July2To3YearsSaleCost() - tax_capitalGain_property.getAfter1July2To3YearsPurchaseCost()));
                    }

                    if (tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsPurchaseCost() != null && tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsSaleCost() != null && (tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsSaleCost() > tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsPurchaseCost())) {
                        capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsSaleCost() - tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsPurchaseCost());
                        capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(0.0, (tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsSaleCost() - tax_capitalGain_property.getAfter1JulyAndMoreThan3YearsPurchaseCost()));
                    }

                } else {
                    if (tax_capitalGain_property.getAfter1JulyPurchaseCost() != null && tax_capitalGain_property.getAfter1JulySaleCost() != null && (tax_capitalGain_property.getAfter1JulySaleCost() > tax_capitalGain_property.getAfter1JulyPurchaseCost())) {
                        capitalGainPropertyTaxableIncome += (tax_capitalGain_property.getAfter1JulySaleCost() - tax_capitalGain_property.getAfter1JulyPurchaseCost());
                        capitalGainPropertyTaxCharge += CommonUtil.dashPercentOfValue(10.0, (tax_capitalGain_property.getAfter1JulySaleCost() - tax_capitalGain_property.getAfter1JulyPurchaseCost()));
                    }
                }

                MyPrint.println("Taxable Income : Capital Gain -> Property" + capitalGainPropertyTaxableIncome);
                MyPrint.println("Withhold Tax : Capital Gain -> Property" + capitalGainPropertyWithholdingTax);
                MyPrint.println("Tax Charge : Capital Gain -> Property" + capitalGainPropertyTaxCharge);


                taxableIncomeForWelthStatement += capitalGainPropertyTaxableIncome;
                taxableIncome += capitalGainPropertyTaxableIncome;
                withholdingTax += capitalGainPropertyWithholdingTax;
                withholdingTaxForWelthStatement += capitalGainPropertyWithholdingTax;
                taxCharge += capitalGainPropertyTaxCharge;

                taxformCalculationBean.setCapitalGainPropertyTaxableIncome(capitalGainPropertyTaxableIncome + "");
                taxformCalculationBean.setCapitalGainPropertyWithholdingTax(capitalGainPropertyWithholdingTax + "");
                taxformCalculationBean.setCapitalGainPropertyTaxCharge(capitalGainPropertyTaxCharge + "");

            }

            MyPrint.println("========================INCOME TAX -> OTHER SOURCES========================");

            if (taxform.getTaxformIncomeTaxOtherSources() != null) {
                Taxform_IncomeTax_OtherSources tax_otherSources = taxform.getTaxformIncomeTaxOtherSources();

                MyPrint.println("========================INCOME TAX -> OTHER SOURCES -> AGRICULTURAL INCOME========================");
                if (tax_otherSources.getAgriculturalIncome() != null) {

                    Double otherSourcesAgriculturalIncomeTaxableIncome = 0.0;
                    Double otherSourcesAgriculturalIncomeWithholdingTax = 0.0;
                    Double otherSourcesAgriculturalIncomeTaxCharge = 0.0;

                    otherSourcesAgriculturalIncomeTaxableIncome += tax_otherSources.getAgriculturalIncome();

                    MyPrint.println("Taxable Income : Income Tax -> Other Sources -> Agricultural Income" + otherSourcesAgriculturalIncomeTaxableIncome);
                    MyPrint.println("Withhold Tax : Income Tax -> Other Sources -> Agricultural Income" + otherSourcesAgriculturalIncomeWithholdingTax);
                    MyPrint.println("Tax Charge : Income Tax -> Other Sources -> Agricultural Income" + otherSourcesAgriculturalIncomeTaxCharge);

                    taxableIncomeForWelthStatement += otherSourcesAgriculturalIncomeTaxableIncome;

                    taxformCalculationBean.setAgriculturalIncomeTaxableIncome(otherSourcesAgriculturalIncomeTaxableIncome + "");

                }

                MyPrint.println("========================INCOME TAX -> OTHER SOURCES -> OTHER INFLOWS========================");
                if (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null) {

                    Double otherSourcesOtherInflowsTaxableIncome = 0.0;
                    Double otherSourcesOtherInflowsWithholdingTax = 0.0;
                    Double otherSourcesOtherInflowsTaxCharge = 0.0;

                    for (TaxForm_IncomeTax_OtherSources_OtherInflow taxFormIncomeTaxOtherSourcesOtherInflow : taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList()) {
                        if (taxFormIncomeTaxOtherSourcesOtherInflow != null && taxFormIncomeTaxOtherSourcesOtherInflow.getAmount() != null) {
                            otherSourcesOtherInflowsTaxableIncome += taxFormIncomeTaxOtherSourcesOtherInflow.getAmount();
                        }
                    }

                    MyPrint.println("Taxable Income : Income Tax -> Other Sources -> Other Inflows" + otherSourcesOtherInflowsTaxableIncome);
                    MyPrint.println("Withhold Tax : Income Tax -> Other Sources -> Other Inflows" + otherSourcesOtherInflowsWithholdingTax);
                    MyPrint.println("Tax Charge : Income Tax -> Other Sources -> Other Inflows" + otherSourcesOtherInflowsTaxCharge);

                    taxableIncomeForWelthStatement += otherSourcesOtherInflowsTaxableIncome;

                    taxformCalculationBean.setOtherInflowsTaxableIncome(otherSourcesOtherInflowsTaxableIncome + "");

                }

                MyPrint.println("========================INCOME TAX -> OTHER SOURCES -> BONUS SHARES========================");

                if (tax_otherSources.getBonusShares() != null) {

                    Double otherSourcesBonusSharesTaxableIncome = 0.0;
                    Double otherSourcesBonusSharesWithholdingTax = 0.0;
                    Double otherSourcesBonusSharesTaxCharge = 0.0;

                    otherSourcesBonusSharesTaxableIncome += tax_otherSources.getBonusShares();
                    otherSourcesBonusSharesTaxCharge += CommonUtil.dashPercentOfValue(5.0, tax_otherSources.getBonusShares());
                    otherSourcesBonusSharesWithholdingTax += tax_otherSources.getBonusSharesTaxDeducted();

                    MyPrint.println("Taxable Income : Income Tax -> Other Sources -> Bonus Shares" + otherSourcesBonusSharesTaxableIncome);
                    MyPrint.println("Withhold Tax : Income Tax -> Other Sources -> Bonus Shares" + otherSourcesBonusSharesWithholdingTax);
                    MyPrint.println("Tax Charge : Income Tax -> Other Sources -> Bonus Shares" + otherSourcesBonusSharesTaxCharge);


                    taxableIncomeForWelthStatement += otherSourcesBonusSharesTaxableIncome;
                    taxableIncome += otherSourcesBonusSharesTaxableIncome;
                    withholdingTax += otherSourcesBonusSharesWithholdingTax;
                    withholdingTaxForWelthStatement += otherSourcesBonusSharesWithholdingTax;
                    taxCharge += otherSourcesBonusSharesTaxCharge;

                    taxformCalculationBean.setBonusSharesTaxableIncome(otherSourcesBonusSharesTaxableIncome + "");
                    taxformCalculationBean.setBonusSharesWithholdingTax(otherSourcesBonusSharesWithholdingTax + "");
                    taxformCalculationBean.setBonusSharesTaxCharge(otherSourcesBonusSharesTaxCharge + "");
                }

                MyPrint.println("========================INCOME TAX -> OTHER SOURCES -> Dividends========================");

                List<Double> dividendPercentages = taxformCalculationValuesBean.getDividendPercentages();
                if (dividendPercentages != null && dividendPercentages.size() > 0) {
                    Double otherSourcesDividendsTaxableIncome = 0.0;
                    Double otherSourcesDividendsWithholdingTax = 0.0;
                    Double otherSourcesDividendsTaxCharge = 0.0;

                    if (tax_otherSources.getDividentByPowerCompanies() != null) {
                        otherSourcesDividendsTaxableIncome += tax_otherSources.getDividentByPowerCompanies();
                        otherSourcesDividendsTaxCharge += CommonUtil.dashPercentOfValue(dividendPercentages.get(0), tax_otherSources.getDividentByPowerCompanies());
                    }
                    if (tax_otherSources.getDividentByPowerCompaniesTaxDeducted() != null) {
                        otherSourcesDividendsWithholdingTax += tax_otherSources.getDividentByPowerCompaniesTaxDeducted();
                    }

                    if (tax_otherSources.getDividentByOtherCompaniesStockFund() != null) {
                        otherSourcesDividendsTaxableIncome += tax_otherSources.getDividentByOtherCompaniesStockFund();
                        otherSourcesDividendsTaxCharge += CommonUtil.dashPercentOfValue(dividendPercentages.get(1), tax_otherSources.getDividentByOtherCompaniesStockFund());
                    }
                    if (tax_otherSources.getDividentByOtherCompaniesStockFundTaxDeducted() != null) {
                        otherSourcesDividendsWithholdingTax += tax_otherSources.getDividentByOtherCompaniesStockFundTaxDeducted();
                    }

                    if (tax_otherSources.getDividentByMutualFunds() != null) {
                        otherSourcesDividendsTaxableIncome += tax_otherSources.getDividentByMutualFunds();
                        otherSourcesDividendsTaxCharge += CommonUtil.dashPercentOfValue(dividendPercentages.get(2), tax_otherSources.getDividentByMutualFunds());
                    }
                    if (tax_otherSources.getDividentByMutualFundsTaxDeducted() != null) {
                        otherSourcesDividendsWithholdingTax += tax_otherSources.getDividentByMutualFundsTaxDeducted();
                    }


                    MyPrint.println("Taxable Income : Income Tax -> Other Sources -> Dividends" + otherSourcesDividendsTaxableIncome);
                    MyPrint.println("Withhold Tax : Income Tax -> Other Sources -> Dividends" + otherSourcesDividendsWithholdingTax);
                    MyPrint.println("Tax Charge : Income Tax -> Other Sources -> Dividends" + otherSourcesDividendsTaxCharge);


                    taxableIncomeForWelthStatement += otherSourcesDividendsTaxableIncome;
                    taxableIncome += otherSourcesDividendsTaxableIncome;
                    withholdingTax += otherSourcesDividendsWithholdingTax;
                    withholdingTaxForWelthStatement += otherSourcesDividendsWithholdingTax;
                    taxCharge += otherSourcesDividendsTaxCharge;

                    taxformCalculationBean.setDividendsTaxableIncome(otherSourcesDividendsTaxableIncome + "");
                    taxformCalculationBean.setDividendsWithholdingTax(otherSourcesDividendsWithholdingTax + "");
                    taxformCalculationBean.setDividendsTaxCharge(otherSourcesDividendsTaxCharge + "");
                }
            }

            MyPrint.println("========================INCOME TAX -> OTHER SOURCES -> PROFIT ON BANK DEPOSIT========================");

            if (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0) {
                Double profitOnBankDepositTaxableIncome = 0.0;
                Double profitOnBankDepositWithholdingTax = 0.0;
                Double profitOnBankDepositTaxCharge = 0.0;

                List<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit> tax_otherSources_profitOnBankDepositList = taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList();

                for (Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit tax_otherSources_profitOnBankDeposit : tax_otherSources_profitOnBankDepositList) {
                    if (tax_otherSources_profitOnBankDeposit.getProfitAmount() != null) {
                        profitOnBankDepositTaxableIncome += tax_otherSources_profitOnBankDeposit.getProfitAmount();
                    }
                    if (tax_otherSources_profitOnBankDeposit.getTaxDeducted() != null) {
                        profitOnBankDepositWithholdingTax += tax_otherSources_profitOnBankDeposit.getTaxDeducted();
                    }
                }
                profitOnBankDepositTaxCharge += CommonUtil.getTaxRateFromProfitOnBankDeposit(taxform, profitOnBankDepositTaxableIncome);

                /*if (profitOnBankDepositTaxableIncome != null && profitOnBankDepositTaxableIncome > 0) {
                    Double taxRateForProfitOnBankDeposit = CommonUtil.getTaxRateFromProfitOnBankDeposit(taxform, profitOnBankDepositTaxableIncome);
                    if (taxRateForProfitOnBankDeposit != null) {
                        profitOnBankDepositTaxCharge += CommonUtil.dashPercentOfValue(taxRateForProfitOnBankDeposit, profitOnBankDepositTaxableIncome);
                    }
                }*/

                MyPrint.println("Taxable Income : Income Tax -> Other Sources -> Profit On Bank Deposit" + profitOnBankDepositTaxableIncome);
                MyPrint.println("Withhold Tax : Income Tax -> Other Sources -> Profit On Bank Deposit" + profitOnBankDepositWithholdingTax);
                MyPrint.println("Tax Charge : Income Tax -> Other Sources ->  Profit On Bank Deposit" + profitOnBankDepositTaxCharge);

                taxableIncomeForWelthStatement += profitOnBankDepositTaxableIncome;
                taxableIncome += profitOnBankDepositTaxableIncome;
                withholdingTax += profitOnBankDepositWithholdingTax;
                withholdingTaxForWelthStatement += profitOnBankDepositWithholdingTax;
                taxCharge += profitOnBankDepositTaxCharge;

                taxformCalculationBean.setProfitOnBankDepositTaxableIncome(profitOnBankDepositTaxableIncome + "");
                taxformCalculationBean.setProfitOnBankDepositWithholdingTax(profitOnBankDepositWithholdingTax + "");
                taxformCalculationBean.setProfitOnBankDepositTaxCharge(profitOnBankDepositTaxCharge + "");
            }
                        /*if (taxform.getTaxformIncomeTaxOtherSources() != null) {
                        }*/
            MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS========================");

            if (taxform.getResidenceStatus() != null && taxform.getResidenceStatus().equals("Resident")) {
                MyPrint.println("Investment In Shares :::: Check " + true);
                taxformCalculationBean.setInvestmentsInSharesShowCheck(true);
            } else {
                MyPrint.println("Investment In Shares :::: Check " + false);
                taxformCalculationBean.setInvestmentsInSharesShowCheck(false);
            }
            if (taxform.getNationality() != null && taxform.getNationality().equals("Pakistani") && taxableIncomeForCalculation != null && taxableIncomeForCalculation.compareTo(0.0) > 0) {
                MyPrint.println("Contribution To Pension Funds :::: Check " + true);
                taxformCalculationBean.setPensionFundsShowCheck(true);
            } else {
                MyPrint.println("Contribution To Pension Funds :::: Check " + false);
                taxformCalculationBean.setPensionFundsShowCheck(false);
            }

            if (taxform.getResidenceStatus() != null && taxform.getResidenceStatus().equals("Resident") && taxableIncomeForCalculation != null && taxableIncomeForCalculation.compareTo(1.0) > 0){
                MyPrint.println("Health Insurance Premium :::: Check " + true);
                taxformCalculationBean.setHealthInsurancePremiumShowCheck(true);
            } else {
                MyPrint.println("Health Insurance Premium :::: Check " + false);
                taxformCalculationBean.setHealthInsurancePremiumShowCheck(false);
            }

            Double educationAllowanceShowCheckAmount = taxformCalculationValuesBean.getEducationAllowanceShowCheckAmount();
            if (taxableIncomeForCalculation != null && educationAllowanceShowCheckAmount != null && taxableIncomeForCalculation < educationAllowanceShowCheckAmount){
                MyPrint.println("Education Allowance :::: Check " + true);
                taxformCalculationBean.setEducationAllowancesShowCheck(true);
            } else {
                MyPrint.println("Education Allowance :::: Check " + false);
                taxformCalculationBean.setEducationAllowancesShowCheck(false);
            }


            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> ONLY FOR ALLOWANCE========================");
            if (taxform.getTaxformTaxDeductedCollectedOther() != null) {
                Taxform_TaxDeductedCollected_Other taxDeductedCollectedOther = taxform.getTaxformTaxDeductedCollectedOther();

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> WITHDRAWAL FROM PENSION FUND========================");
                if (taxDeductedCollectedOther.getWithdrawalFromPensionFund() != null) {
                    Double taxDeductedCollectedPensionFundTaxableIncome = 0.0;
                    Double taxDeductedCollectedPensionFundWithholdingTax = 0.0;
                    Double taxDeductedCollectedPensionFundTaxCharge = 0.0;

                    if ((taxDeductedCollectedOther.getWithdrawalFromPensionFundTaxDeducted() != null && taxDeductedCollectedOther.getWithdrawalFromPensionFundTaxDeducted().compareTo(0.0) > 0)) {
                        taxDeductedCollectedPensionFundWithholdingTax += taxDeductedCollectedOther.getWithdrawalFromPensionFundTaxDeducted();
                        pensionFundsTaxCharge = taxDeductedCollectedOther.getWithdrawalFromPensionFund();
                        if (continueConditionForPensionFunds == 1) {
                            continueConditionForPensionFunds ++;
                            MyPrint.println("------------- CONTINUE ------------");
                            MyPrint.println("------------- CONTINUE ------------");
                            continue;
                        }
                        taxDeductedCollectedPensionFundTaxableIncome += taxDeductedCollectedOther.getWithdrawalFromPensionFund();
                    }

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Pension Fund" + taxDeductedCollectedPensionFundTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Pension Fund" + taxDeductedCollectedPensionFundWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Pension Fund" + taxDeductedCollectedPensionFundTaxCharge);

                                /*taxableIncomeForWelthStatement += taxDeductedCollectedPensionFundTaxableIncome;*/
                    /*taxableIncome += taxDeductedCollectedPensionFundTaxableIncome;*/
                    withholdingTax += taxDeductedCollectedPensionFundWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedPensionFundWithholdingTax;
                    taxCharge += taxDeductedCollectedPensionFundTaxCharge;

                    taxformCalculationBean.setWithdrawalFromPensionFundsTaxableIncome(taxDeductedCollectedPensionFundTaxableIncome + "");
                    taxformCalculationBean.setWithdrawalFromPensionFundsWithholdingTax(taxDeductedCollectedPensionFundWithholdingTax + "");
                    taxformCalculationBean.setWithdrawalFromPensionFundsTaxCharge(taxDeductedCollectedPensionFundTaxCharge + "");
                }
            }


            MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS========================");
            if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                Double deductibleAllownaceOrCreditTaxableIncome = 0.0;
                Double deductibleAllownaceOrCreditWithholdingTax = 0.0;
                Double deductibleAllownaceOrCreditTaxCharge = 0.0;

                Taxform_DeductibleAllowanceOrCredit taxformDeductibleAllowanceOrCredit = taxform.getTaxformDeductibleAllowanceOrCredit();

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> DONATION UNDER CLAUSE 61========================");
                if (continueConditionForDonationsUnderClause61 == 1 &&
                        taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check() != null &&
                        taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check() &&
                        taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61() != null) {

                    Double charity = taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61();
                    MyPrint.println("Taxable Salary:::" + taxableIncomeForCalculation);
                    if (charity <= CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation)) {
                        donationsUnderClause61TaxCharge = charity;
                    }
                    else{
                        MyPrint.println("charity:::2:::" + CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation));
                        donationsUnderClause61TaxCharge = CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation);
                    }
                    if (donationsUnderClause61TaxCharge != null && donationsUnderClause61TaxCharge.compareTo(0.0) > 0){
                        MyPrint.println("Donation under clause 61:::" + donationsUnderClause61TaxCharge);
                        continueConditionForDonationsUnderClause61 ++;
                        MyPrint.println("------------- CONTINUE ------------");
                        MyPrint.println("------------- CONTINUE ------------");
                        MyPrint.println("------------- CONTINUE ------------");
                        continue;
                    }
                }

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> INTEREST OR RENT ON HOUSE LOAN========================");
                if (continueConditionForRentOnHouseLoan == 1 && taxform.getTaxformDeductibleAllowanceOrCredit().getInterestOrRateOnHouseHolds() != null) {
                    Double interestOrRent = taxformDeductibleAllowanceOrCredit.getInterestOrRateOnHouseHolds();
                    Double smallest = CommonUtil.findSmallestNumberBetween3(interestOrRent, 2000000.0, CommonUtil.dashPercentOfValue(50.0, taxableIncomeForCalculation));
                    if (smallest != null && smallest.compareTo(0.0) > 0) {
                        Double taxRate = (CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxform.getTaxformYear().getYear(), taxableIncomeForCalculation) / taxableIncomeForCalculation) * 100;
                        MyPrint.println("Tax Rate:::" + taxRate);
                        interestOrRentOnHouseLoanTaxCharge =  smallest ;
                        if (interestOrRentOnHouseLoanTaxCharge != null && interestOrRentOnHouseLoanTaxCharge.compareTo(0.0) > 0){
                            MyPrint.println("Interest or rent on loan:::" + interestOrRentOnHouseLoanTaxCharge);
                                        /*if (continueConditionForRentOnHouseLoan == 1) {*/
                            continueConditionForRentOnHouseLoan ++;
                            MyPrint.println("------------- CONTINUE ------------");
                            MyPrint.println("------------- CONTINUE ------------");
                            MyPrint.println("------------- CONTINUE ------------");
                            continue;
                                        /*}*/
                                        /*deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRate, smallest);*/
                        }
                    }
                }

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> EDUCATION ALLOWANCE========================");
                if (continueConditionForEducationAllowance == 1 && taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceTutionFees() != null) {
                    Double tutionFees = taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceTutionFees();
                    Integer noOfChildrens = 0;
                    Double educationAllowanceTaxableIncomePercent = taxformCalculationValuesBean.getEducationAllowanceTaxableIncomePercent();
                    if (taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceNoOfChildrens() != null && educationAllowanceTaxableIncomePercent != null)
                        noOfChildrens = taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceNoOfChildrens();
                    Double smallest = CommonUtil.findSmallestNumberBetween3(CommonUtil.dashPercentOfValue(5.0, tutionFees), CommonUtil.dashPercentOfValue(educationAllowanceTaxableIncomePercent, taxableIncomeForCalculation), 60000.0 * noOfChildrens);
                    if (smallest != null && smallest.compareTo(0.0) > 0) {
                        /*Double taxRate = (CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxableIncomeForCalculation) / taxableIncomeForCalculation) * 100;
                        MyPrint.println("Tax Rate:::" + taxRate);*/
                        educationAllownaceTaxCharge = smallest;
                        if (educationAllownaceTaxCharge != null && educationAllownaceTaxCharge.compareTo(0.0) > 0){
                            MyPrint.println("Interest or rent on loan:::" + educationAllownaceTaxCharge);
                                        /*if (continueCondition1 == 1) {*/
                            continueConditionForEducationAllowance ++;
                            MyPrint.println("------------- CONTINUE ------------");
                            MyPrint.println("------------- CONTINUE ------------");
                            continue;
                                        /*}*/
                                        /*deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest);*/
                        }
                    }
                }

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> DONATION UNDER SECTION 61========================");
                if (taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check() != null &&
                        taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check() &&
                        taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61() != null) {

                    Double charity = taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61();
                    MyPrint.println("Taxable Salary:::" + taxableIncomeForCalculation);
                    if (charity <= CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation)) {
                        MyPrint.println("charity:::1:::" + CommonUtil.dashPercentOfValue(taxRateForCalculation, charity));
                        deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, charity);
                        if (!CommonUtil.dashPercentOfValue(taxRateForCalculation, charity).isNaN())
                            taxformCalculationBean.setDonationsTaxCredit("-" + CommonUtil.dashPercentOfValue(taxRateForCalculation, charity));
                        else
                            taxformCalculationBean.setDonationsTaxCredit("0");
                    } else {
                        MyPrint.println("charity:::2:::" + CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation));
                        deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation));
                        if (!CommonUtil.dashPercentOfValue(taxRateForCalculation, CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation)).isNaN())
                            taxformCalculationBean.setDonationsTaxCredit("-"+CommonUtil.dashPercentOfValue(taxRateForCalculation, CommonUtil.dashPercentOfValue(30.0, taxableIncomeForCalculation)));
                        else
                            taxformCalculationBean.setDonationsTaxCredit("0");
                    }

                }

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> INVESTMENTS IN SHARES========================");
                if (taxform.getResidenceStatus().equals("Resident")) {
                    if (taxformDeductibleAllowanceOrCredit.getInvestmentInSharesMutualFundsAndLifeInsurance() != null) {
                        Double funds = taxformDeductibleAllowanceOrCredit.getInvestmentInSharesMutualFundsAndLifeInsurance();
			Double smallest = null;
                        if (taxform.getTaxformYear().getYear() == 2019) {
			    smallest = CommonUtil.findSmallestNumberBetween3(2000000.0, CommonUtil.dashPercentOfValue(20.0, taxableIncomeForCalculation), funds);
			} else {
			    smallest = CommonUtil.findSmallestNumberBetween3(1500000.0, CommonUtil.dashPercentOfValue(20.0, taxableIncomeForCalculation), funds);
			}

                        if (smallest != null) {
                            /*Double taxRate = (CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxableIncomeForCalculation) / taxableIncomeForCalculation) * 100;
                            MyPrint.println("Tax Rate:::" + taxRate);*/
                            MyPrint.println("funds:::" + CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                            deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest);
                            if (!CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest).isNaN())
                                taxformCalculationBean.setInvestementsTaxCredit("-"+CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                            else
                                taxformCalculationBean.setInvestementsTaxCredit("0");
                        }
                    }
                }


                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> CONTRIBUTION TO PENSION FUNDS========================");
                if (taxform.getNationality().equals("Pakistani") && taxableIncomeForCalculation.compareTo(0.0) > 0) {
                    if (taxformDeductibleAllowanceOrCredit.getInvestmentInApprovedPensionFund() != null){
                        Double pensionFunds = taxformDeductibleAllowanceOrCredit.getInvestmentInApprovedPensionFund();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date d = sdf.parse("30/06/" + taxform.getTaxformYear().getYear() + "");
                                    /*Date dateAccordingToTaxYear = CommonUtil.changeDateStringToDate(taxform.getYear() + "-06-30");*/
                        Double age = CommonUtil.calculateAge(taxform.getDateOfBirth(), d);
                        Double allowedTaxPercentage = CommonUtil.allowedTaxableIncomePercentageOnPensionFunds(age);
                        MyPrint.println("Age::" + age + ":::" + "Allowed Tax Percentage:::" + allowedTaxPercentage);
                        Double smallest = CommonUtil.findSmallestNumberBetween2(pensionFunds, CommonUtil.dashPercentOfValue(allowedTaxPercentage, taxableIncomeForCalculation));
                        if (smallest != null) {
                            MyPrint.println("pension funds:::" + CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                            deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest);
                            taxformCalculationBean.setPensionFundsTaxCredit("-" + CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                        }
                    }
                }

                MyPrint.println("========================DEDUCTABLE ALLOWANCE AND CREDITS -> HEALTH INSURANCE PREMIUM========================");


                Double healthInsurancePremiumStaticAmount = taxformCalculationValuesBean.getHealthInsurancePremiumStaticAmount();
                if (taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium() != null && healthInsurancePremiumStaticAmount != null) {
                    Double smallest = CommonUtil.findSmallestNumberBetween3(taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium(), CommonUtil.dashPercentOfValue(5.0, taxableIncomeForCalculation), healthInsurancePremiumStaticAmount);
                    if (smallest != null && smallest.compareTo(0.0) > 0){
                        /*Double taxRate = (CommonUtil.getTaxRateFromSalaryTaxSlabSalaryIncomeExceeds50(taxableIncomeForCalculation) / taxableIncomeForCalculation) * 100;
                        MyPrint.println("Tax Rate:::" + taxRate);*/
                        MyPrint.println("Health Insurance Premium:::" + CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                        deductibleAllownaceOrCreditTaxCharge -= CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest);
                        taxformCalculationBean.setHealthInsuranceTaxCredit("-" + CommonUtil.dashPercentOfValue(taxRateForCalculation, smallest));
                    }
                }

                MyPrint.println("Taxable Income : Deductable Allownace And Credits" + deductibleAllownaceOrCreditTaxableIncome);
                MyPrint.println("Withhold Tax : Deductable Allownace And Credits" + deductibleAllownaceOrCreditWithholdingTax);
                MyPrint.println("Tax Charge : Deductable Allownace And Credits" + deductibleAllownaceOrCreditTaxCharge);

                taxableIncomeForWelthStatement += deductibleAllownaceOrCreditTaxableIncome;
                taxableIncome += deductibleAllownaceOrCreditTaxableIncome;
                withholdingTax += deductibleAllownaceOrCreditWithholdingTax;
                withholdingTaxForWelthStatement += deductibleAllownaceOrCreditWithholdingTax;
                taxCharge += deductibleAllownaceOrCreditTaxCharge;
                taxCredit = deductibleAllownaceOrCreditTaxCharge;

                taxformCalculationBean.setDeductibleAllowanceOrCreditTaxableIncome(deductibleAllownaceOrCreditTaxableIncome + "");
                taxformCalculationBean.setDeductibleAllowanceOrCreditWithholdingTax(deductibleAllownaceOrCreditWithholdingTax + "");
                taxformCalculationBean.setDeductibleAllowanceOrCreditTaxCharge(deductibleAllownaceOrCreditTaxCharge + "");
            }

            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> BANKING TRANSACTIONS========================");
            if (taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null){
                Double taxDeductedCollectedBankingTransactionTaxableIncome = 0.0;
                Double taxDeductedCollectedBankingTransactionWithholdingTax = 0.0;
                Double taxDeductedCollectedBankingTransactionTaxCharge = 0.0;

                for (Taxform_TaxDeductedCollected_BankingTransaction taxDeductedCollectedBankingTransaction : taxform.getTaxformTaxDeductedCollectedBankingTransactionList()){
                    if (taxDeductedCollectedBankingTransaction.getTaxDeductedAmount() != null){
                        taxDeductedCollectedBankingTransactionWithholdingTax += taxDeductedCollectedBankingTransaction.getTaxDeductedAmount();
                    }
                }

                MyPrint.println("Taxable Income : Tax Deducted Collected -> Banking Transaction" + taxDeductedCollectedBankingTransactionTaxableIncome);
                MyPrint.println("Withhold Tax : Tax Deducted Collected -> Banking Transaction" + taxDeductedCollectedBankingTransactionWithholdingTax);
                MyPrint.println("Tax Charge : Tax Deducted Collected -> Banking Transaction" + taxDeductedCollectedBankingTransactionTaxCharge);

                taxableIncomeForWelthStatement += taxDeductedCollectedBankingTransactionTaxableIncome;
                taxableIncome += taxDeductedCollectedBankingTransactionTaxableIncome;
                withholdingTax += taxDeductedCollectedBankingTransactionWithholdingTax;
                withholdingTaxForWelthStatement += taxDeductedCollectedBankingTransactionWithholdingTax;
                taxCharge += taxDeductedCollectedBankingTransactionTaxCharge;

                taxformCalculationBean.setBankingTransactionTaxableIncome(taxDeductedCollectedBankingTransactionTaxableIncome + "");
                taxformCalculationBean.setBankingTransactionWithholdingTax(taxDeductedCollectedBankingTransactionWithholdingTax + "");
                taxformCalculationBean.setBankingTranxactionTaxCharge(taxDeductedCollectedBankingTransactionTaxCharge + "");
            }

            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> WITHHOLD TAX VEHICLE========================");
            if (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null){
                Double taxDeductedCollectedWithholdTaxVehicleTaxableIncome = 0.0;
                Double taxDeductedCollectedWithholdTaxVehicleWithholdingTax = 0.0;
                Double taxDeductedCollectedWithholdTaxVehicleTaxCharge = 0.0;

                for (Taxform_TaxDeductedCollected_WithholdTaxVehicle taxDeductedCollectedWithholdTaxVehicle : taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList()){
                    if (taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted() != null){
                        taxDeductedCollectedWithholdTaxVehicleWithholdingTax += taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted();
                    }
                }

                MyPrint.println("Taxable Income : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedWithholdTaxVehicleTaxableIncome);
                MyPrint.println("Withhold Tax : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedWithholdTaxVehicleWithholdingTax);
                MyPrint.println("Tax Charge : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedWithholdTaxVehicleTaxCharge);

                taxableIncomeForWelthStatement += taxDeductedCollectedWithholdTaxVehicleTaxableIncome;
                taxableIncome += taxDeductedCollectedWithholdTaxVehicleTaxableIncome;
                withholdingTax += taxDeductedCollectedWithholdTaxVehicleWithholdingTax;
                withholdingTaxForWelthStatement += taxDeductedCollectedWithholdTaxVehicleWithholdingTax;
                taxCharge += taxDeductedCollectedWithholdTaxVehicleTaxCharge;

                taxformCalculationBean.setWithholdTaxVehicleTaxableIncome(taxDeductedCollectedWithholdTaxVehicleTaxableIncome + "");
                taxformCalculationBean.setWithholdTaxVehicleWithholdingTax(taxDeductedCollectedWithholdTaxVehicleWithholdingTax + "");
                taxformCalculationBean.setWithholdTaxVehicleTaxCharge(taxDeductedCollectedWithholdTaxVehicleTaxCharge + "");
            }

            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> Utilities========================");
            if (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null){
                Double taxDeductedCollectedUtilitiesTaxableIncome = 0.0;
                Double taxDeductedCollectedUtilitiesWithholdingTax = 0.0;
                Double taxDeductedCollectedUtilitiesTaxCharge = 0.0;

                for (Taxform_TaxDeductedCollected_Utilities taxDeductedCollectedUtilities : taxform.getTaxformTaxDeductedCollectedUtilitiesList()){
                    if (taxDeductedCollectedUtilities.getTaxDeducted() != null){
                        taxDeductedCollectedUtilitiesWithholdingTax += taxDeductedCollectedUtilities.getTaxDeducted();
                    }
                }

                MyPrint.println("Taxable Income : Tax Deducted Collected -> Utilities" + taxDeductedCollectedUtilitiesTaxableIncome);
                MyPrint.println("Withhold Tax : Tax Deducted Collected -> Utilities" + taxDeductedCollectedUtilitiesWithholdingTax);
                MyPrint.println("Tax Charge : Tax Deducted Collected -> Utilities" + taxDeductedCollectedUtilitiesTaxCharge);

                taxableIncomeForWelthStatement += taxDeductedCollectedUtilitiesTaxableIncome;
                taxableIncome += taxDeductedCollectedUtilitiesTaxableIncome;
                withholdingTax += taxDeductedCollectedUtilitiesWithholdingTax;
                withholdingTaxForWelthStatement += taxDeductedCollectedUtilitiesWithholdingTax;
                taxCharge += taxDeductedCollectedUtilitiesTaxCharge;

                taxformCalculationBean.setUtilitiesTaxableIncome(taxDeductedCollectedUtilitiesTaxableIncome + "");
                taxformCalculationBean.setUtilitiesWithholdingTax(taxDeductedCollectedUtilitiesWithholdingTax + "");
                taxformCalculationBean.setUtilitiesTaxCharge(taxDeductedCollectedUtilitiesTaxCharge + "");
            }

            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> Other========================");
            if (taxform.getTaxformTaxDeductedCollectedOther() != null){


                Taxform_TaxDeductedCollected_Other taxDeductedCollectedOther = taxform.getTaxformTaxDeductedCollectedOther();

                Double propertyPurchaseSaleWithholdingTax = 0.0;
                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> PROPERTY PURCHASE========================");
                if (taxDeductedCollectedOther.getPropertyPurchaseTaxDeducted() != null) {
                    Double taxDeductedCollectedPropertyTaxableIncome = 0.0;
                    Double taxDeductedCollectedPropertyWithholdingTax = 0.0;
                    Double taxDeductedCollectedPropertyTaxCharge = 0.0;

                    taxDeductedCollectedPropertyWithholdingTax += taxDeductedCollectedOther.getPropertyPurchaseTaxDeducted();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Property Pruchase " + taxDeductedCollectedPropertyTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Property Pruchase " + taxDeductedCollectedPropertyWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Property Pruchase " + taxDeductedCollectedPropertyTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedPropertyTaxableIncome;
                    taxableIncome += taxDeductedCollectedPropertyTaxableIncome;
                    withholdingTax += taxDeductedCollectedPropertyWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedPropertyWithholdingTax;
                    taxCharge += taxDeductedCollectedPropertyTaxCharge;

                    propertyPurchaseSaleWithholdingTax += taxDeductedCollectedPropertyWithholdingTax;
                    taxformCalculationBean.setPropertyPurchaseTaxableIncome(taxDeductedCollectedPropertyTaxableIncome + "");
                    taxformCalculationBean.setPropertyPurchaseWithholdingTax(taxDeductedCollectedPropertyWithholdingTax + "");
                    taxformCalculationBean.setPropertyPurchaseTaxCharge(taxDeductedCollectedPropertyTaxCharge + "");
                }

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> PROPERTY SALE========================");
                if (taxDeductedCollectedOther.getPropertySaleTaxDeducted() != null) {
                    Double taxDeductedCollectedPropertyTaxableIncome = 0.0;
                    Double taxDeductedCollectedPropertyWithholdingTax = 0.0;
                    Double taxDeductedCollectedPropertyTaxCharge = 0.0;

                    taxDeductedCollectedPropertyWithholdingTax += taxDeductedCollectedOther.getPropertySaleTaxDeducted();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Property Sale " + taxDeductedCollectedPropertyTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Property Sale " + taxDeductedCollectedPropertyWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Property Sale " + taxDeductedCollectedPropertyTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedPropertyTaxableIncome;
                    taxableIncome += taxDeductedCollectedPropertyTaxableIncome;
                    withholdingTax += taxDeductedCollectedPropertyWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedPropertyWithholdingTax;
                    taxCharge += taxDeductedCollectedPropertyTaxCharge;

                    propertyPurchaseSaleWithholdingTax += taxDeductedCollectedPropertyWithholdingTax;
                    taxformCalculationBean.setPropertySaleTaxableIncome(taxDeductedCollectedPropertyTaxableIncome + "");
                    taxformCalculationBean.setPropertySaleWithholdingTax(taxDeductedCollectedPropertyWithholdingTax + "");
                    taxformCalculationBean.setPropertySaleTaxCharge(taxDeductedCollectedPropertyTaxCharge + "");
                }

                taxformCalculationBean.setPropertyPurchaseSaleWithholdingTax(propertyPurchaseSaleWithholdingTax + "");

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> EDUCATION FEES========================");
                if (taxDeductedCollectedOther.getEducationFeeTaxDeductedTaxDeducted() != null) {
                    Double taxDeductedCollectedEducationFeeTaxableIncome = 0.0;
                    Double taxDeductedCollectedEducationFeeWithholdingTax = 0.0;
                    Double taxDeductedCollectedEducationFeeTaxCharge = 0.0;

                    taxDeductedCollectedEducationFeeWithholdingTax += taxDeductedCollectedOther.getEducationFeeTaxDeductedTaxDeducted();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Education Fees" + taxDeductedCollectedEducationFeeTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Education Fees" + taxDeductedCollectedEducationFeeWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Education Fees" + taxDeductedCollectedEducationFeeTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedEducationFeeTaxableIncome;
                    taxableIncome += taxDeductedCollectedEducationFeeTaxableIncome;
                    withholdingTax += taxDeductedCollectedEducationFeeWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedEducationFeeWithholdingTax;
                    taxCharge += taxDeductedCollectedEducationFeeTaxCharge;

                    taxformCalculationBean.setEducationFeesTaxableIncome(taxDeductedCollectedEducationFeeTaxableIncome + "");
                    taxformCalculationBean.setEducationFeesWithholdingTax(taxDeductedCollectedEducationFeeWithholdingTax + "");
                    taxformCalculationBean.setEducationFeesTaxCharge(taxDeductedCollectedEducationFeeTaxCharge + "");
                }

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> AIR TICKET IN YEAR========================");
                if (taxDeductedCollectedOther.getAirTicketInYearTaxDeducted() != null){
                    Double taxDeductedCollectedAirTicketFeeTaxableIncome = 0.0;
                    Double taxDeductedCollectedAirTicketFeeWithholdingTax = 0.0;
                    Double taxDeductedCollectedAirTicketFeeTaxCharge = 0.0;

                    taxDeductedCollectedAirTicketFeeWithholdingTax += taxDeductedCollectedOther.getAirTicketInYearTaxDeducted();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedAirTicketFeeTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedAirTicketFeeWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Withhold Tax Vehicle" + taxDeductedCollectedAirTicketFeeTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedAirTicketFeeTaxableIncome;
                    taxableIncome += taxDeductedCollectedAirTicketFeeTaxableIncome;
                    withholdingTax += taxDeductedCollectedAirTicketFeeWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedAirTicketFeeWithholdingTax;
                    taxCharge += taxDeductedCollectedAirTicketFeeTaxCharge;

                    taxformCalculationBean.setAirTicketInYearTaxableIncome(taxDeductedCollectedAirTicketFeeTaxableIncome + "");
                    taxformCalculationBean.setAirTicketInYearWithholdingTax(taxDeductedCollectedAirTicketFeeWithholdingTax + "");
                    taxformCalculationBean.setAirTicketInYearTaxCharge(taxDeductedCollectedAirTicketFeeTaxCharge + "");
                }

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> FUNCTIONS AND GATHERINGS========================");
                if (taxDeductedCollectedOther.getFunctionsAndGatherings() != null){
                    Double taxDeductedCollectedFunctionsAndGatheringsTaxableIncome = 0.0;
                    Double taxDeductedCollectedFunctionsAndGatheringsWithholdingTax = 0.0;
                    Double taxDeductedCollectedFunctionsAndGatheringsTaxCharge = 0.0;

                    taxDeductedCollectedFunctionsAndGatheringsWithholdingTax += taxDeductedCollectedOther.getFunctionsAndGatherings();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Functions And Gatherings" + taxDeductedCollectedFunctionsAndGatheringsTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Functions And Gatherings" + taxDeductedCollectedFunctionsAndGatheringsWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Functions And Gatherings" + taxDeductedCollectedFunctionsAndGatheringsTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedFunctionsAndGatheringsTaxableIncome;
                    taxableIncome += taxDeductedCollectedFunctionsAndGatheringsTaxableIncome;
                    withholdingTax += taxDeductedCollectedFunctionsAndGatheringsWithholdingTax;
                    withholdingTaxForWelthStatement += taxDeductedCollectedFunctionsAndGatheringsWithholdingTax;
                    taxCharge += taxDeductedCollectedFunctionsAndGatheringsTaxCharge;

                    taxformCalculationBean.setFunctionsAndGatheringsTaxableIncome(taxDeductedCollectedFunctionsAndGatheringsTaxableIncome + "");
                    taxformCalculationBean.setFunctionsAndGatheringsWithholdingTax(taxDeductedCollectedFunctionsAndGatheringsWithholdingTax + "");
                    taxformCalculationBean.setFunctionsAndGatheringsTaxCharge(taxDeductedCollectedFunctionsAndGatheringsTaxCharge + "");
                }

                MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> OTHER -> TAX REFUND FROM PRIOR YEARS========================");
                if (taxDeductedCollectedOther.getTaxRefundOfPriorYear() != null){
                    Double taxDeductedCollectedTaxRefundFromPriorYearsTaxableIncome = 0.0;
                    Double taxDeductedCollectedTaxRefundFromPriorYearsWithholdingTax = 0.0;
                    Double taxDeductedCollectedTaxRefundFromPriorYearsTaxCharge = 0.0;

                    taxDeductedCollectedTaxRefundFromPriorYearsWithholdingTax += taxDeductedCollectedOther.getTaxRefundOfPriorYear();

                    MyPrint.println("Taxable Income : Tax Deducted Collected -> Tax Refund From Prior Years :" + taxDeductedCollectedTaxRefundFromPriorYearsTaxableIncome);
                    MyPrint.println("Withhold Tax : Tax Deducted Collected -> Tax Refund From Prior Years :" + taxDeductedCollectedTaxRefundFromPriorYearsWithholdingTax);
                    MyPrint.println("Tax Charge : Tax Deducted Collected -> Tax Refund From Prior Years :"  + taxDeductedCollectedTaxRefundFromPriorYearsTaxCharge);

                    taxableIncomeForWelthStatement += taxDeductedCollectedTaxRefundFromPriorYearsTaxableIncome;
                    taxableIncome += taxDeductedCollectedTaxRefundFromPriorYearsTaxableIncome;
                    withholdingTax += taxDeductedCollectedTaxRefundFromPriorYearsWithholdingTax;
                    taxCharge += taxDeductedCollectedTaxRefundFromPriorYearsTaxCharge;

                    taxformCalculationBean.setTaxRefundFromPriorYearsTaxableIncome(taxDeductedCollectedTaxRefundFromPriorYearsTaxableIncome + "");
                    taxformCalculationBean.setTaxRefundFromPriorYearsWithholdingTax(taxDeductedCollectedTaxRefundFromPriorYearsWithholdingTax + "");
                    taxformCalculationBean.setTaxRefundFromPriorYearsTaxCharge(taxDeductedCollectedTaxRefundFromPriorYearsTaxCharge + "");
                }
            }

            MyPrint.println("========================TAX DEDUCTED OR COLLECTED -> WEALTH STATEMENT========================");

            Double openingWealth = 0.0;
            Double expenses = 0.0;
            Double welthStatement = 0.0;
            if (taxform.getTaxformWelthStatement() != null) {
                if (taxform.getTaxformWelthStatement().getOpeningWealth() != null) {
                    openingWealth = taxform.getTaxformWelthStatement().getOpeningWealth();
                }

                if (taxform.getTaxformWelthStatementPropertyDetailList() != null){
                    Double propetyDetailValues = 0.0;
                    for (Taxform_WelthStatement_PropertyDetail taxformWelthStatementPropertyDetail : taxform.getTaxformWelthStatementPropertyDetailList()) {
                        if (taxformWelthStatementPropertyDetail.getPropertyCost() != null) {
                            propetyDetailValues += taxformWelthStatementPropertyDetail.getPropertyCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementPropertyDetail(propetyDetailValues + "");
                    welthStatement += propetyDetailValues;
                }else {
                    taxformCalculationBean.setWealthStatementPropertyDetail("0.0");
                }

                if (taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList() != null) {
                    Double BankAccountOrInvestmentsValues = 0.0;
                    for (Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementBankAccountsOrInvestments : taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList()) {
                        if (taxformWelthStatementBankAccountsOrInvestments.getCost() != null) {
                            BankAccountOrInvestmentsValues += taxformWelthStatementBankAccountsOrInvestments.getCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementBankAccountOrInvestment(BankAccountOrInvestmentsValues + "");
                    welthStatement += BankAccountOrInvestmentsValues;
                } else {
                    taxformCalculationBean.setWealthStatementBankAccountOrInvestment("0.0");
                }

                if (taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList()!= null) {
                    Double otherReceivablesOrAssets = 0.0;
                    for (Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementOtherReceivablesOrAssets : taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList()) {
                        if (taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() != null) {
                            otherReceivablesOrAssets += taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementOtherReceivablesOrAssets(otherReceivablesOrAssets + "");
                    welthStatement += otherReceivablesOrAssets;
                } else {
                    taxformCalculationBean.setWealthStatementOtherReceivablesOrAssets("0.0");
                }

                if (taxform.getTaxformWelthStatementOwnVehicleList() != null) {
                    Double ownVehicleValues = 0.0;
                    for (Taxform_WelthStatement_OwnVehicle taxformWelthStatementOwnVehicle : taxform.getTaxformWelthStatementOwnVehicleList()) {
                        if (taxformWelthStatementOwnVehicle.getValueAtCost() != null) {
                            ownVehicleValues += taxformWelthStatementOwnVehicle.getValueAtCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementOwnVehicle(ownVehicleValues + "");
                    welthStatement += ownVehicleValues;
                } else {
                    taxformCalculationBean.setWealthStatementOwnVehicle("0.0");
                }

                if (taxform.getTaxformWelthStatementOtherPossessionsList() != null) {
                    Double otherPossessionsValues = 0.0;
                    for (Taxform_WelthStatement_OtherPossessions taxformWelthStatementOtherPossessions : taxform.getTaxformWelthStatementOtherPossessionsList()) {
                        if (taxformWelthStatementOtherPossessions.getValueAtCost() != null) {
                            otherPossessionsValues += taxformWelthStatementOtherPossessions.getValueAtCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementOtherPossessions(otherPossessionsValues + "");
                    welthStatement += otherPossessionsValues;
                } else {
                    taxformCalculationBean.setWealthStatementOtherPossessions("0.0");
                }

                if (taxform.getTaxformWelthStatementAssetsOutSidePakistanList() != null) {
                    Double assetsOutsidePakistanValues = 0.0;
                    for (Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementAssetsOutSidePakistan : taxform.getTaxformWelthStatementAssetsOutSidePakistanList()) {
                        if (taxformWelthStatementAssetsOutSidePakistan.getValueAtCost() != null) {
                            assetsOutsidePakistanValues += taxformWelthStatementAssetsOutSidePakistan.getValueAtCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementAssetsOutsidePakistan(assetsOutsidePakistanValues + "");
                    welthStatement += assetsOutsidePakistanValues;
                } else {
                    taxformCalculationBean.setWealthStatementAssetsOutsidePakistan("0.0");
                }

                if (taxform.getTaxformWelthStatementOweAnyLoansOrCreditList() != null) {
                    Double oweAnyLoanOrCreditValues = 0.0;
                    for (Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementOweAnyLoansOrCredit : taxform.getTaxformWelthStatementOweAnyLoansOrCreditList()) {
                        if (taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost() != null){
                            oweAnyLoanOrCreditValues += taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost();
                        }
                    }
                    taxformCalculationBean.setWealthStatementOweAnyLoanOrCredit(oweAnyLoanOrCreditValues + "");
                    welthStatement += oweAnyLoanOrCreditValues;
                } else {
                    taxformCalculationBean.setWealthStatementOweAnyLoanOrCredit("0.0");
                }

                if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense() != null) {
                    Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementDetailsOfPersonalExpense = taxform.getTaxformWelthStatementDetailsOfPersonalExpense();

                    if (taxformWelthStatementDetailsOfPersonalExpense.getRent() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getRent();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getRatesTaxesChargeCess() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getRatesTaxesChargeCess();
                    }

                    if (taxformCalculationBean.getWithholdingTax() != null) {
                        expenses += Double.parseDouble(taxformCalculationBean.getWithholdingTax());
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getVehicleRunningOrMaintenance() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getVehicleRunningOrMaintenance();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getTravelling() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getTravelling();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getElectricity() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getElectricity();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getWater() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getWater();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getGas() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getGas();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getTelephone() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getTelephone();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getAssetsInsuranceOrSecurity() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getAssetsInsuranceOrSecurity();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getMedical() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getMedical();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getEducational() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getEducational();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getClub() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getClub();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getFunctionsOrGatherings() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getFunctionsOrGatherings();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getDonationZakatAnnuityProfitOnDebutEtc() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getDonationZakatAnnuityProfitOnDebutEtc();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getOtherPersonalOrHouseholdExpense() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getOtherPersonalOrHouseholdExpense();
                    }

                    if (taxformWelthStatementDetailsOfPersonalExpense.getGift() != null) {
                        expenses += taxformWelthStatementDetailsOfPersonalExpense.getGift();
                    }
                }
            }

            Double closingWealth = openingWealth  + taxableIncomeForWelthStatement - expenses - withholdingTaxForWelthStatement;
            MyPrint.println(openingWealth + " + " + taxableIncomeForWelthStatement + " - " + expenses + " - " + withholdingTaxForWelthStatement + " = " + closingWealth);
            MyPrint.println("Wealth Statement:::" + welthStatement);

            taxformCalculationBean.setTaxableIncome(taxableIncome + "");
            taxformCalculationBean.setWithholdingTax(withholdingTax + "");
            Double diff = taxCharge - taxCredit;
            if (!diff.isNaN())
                taxformCalculationBean.setTaxCharge(diff + "");
            else
                taxformCalculationBean.setTaxCharge("0");

            if (!taxCredit.isNaN())
                taxformCalculationBean.setTaxCredit(-(taxCredit) + "");
            else
                taxformCalculationBean.setTaxCredit("0");


            MyPrint.println((taxCharge != null && !taxCharge.isNaN() ? taxCharge:0.0) +"+"+
                    (taxCredit != null && !taxCredit.isNaN() ? taxCredit:0.0) +"-"+
                    (withholdingTax != null && !withholdingTax.isNaN() ? withholdingTax:0.0));
            Double taxRefundableOrPayable = (taxCharge != null && !taxCharge.isNaN() ? taxCharge:0.0) /*+
                                            (taxCredit != null && !taxCredit.isNaN() ? taxCredit:0.0) */-
                                            (withholdingTax != null && !withholdingTax.isNaN() ? withholdingTax:0.0);
            if (taxRefundableOrPayable.isNaN())
                taxformCalculationBean.setTaxRefundableOrPayable("0");
            else
                taxformCalculationBean.setTaxRefundableOrPayable(taxRefundableOrPayable + "");

            System.out.println("Tax Refundable Of Payable ::: " + taxRefundableOrPayable);

            taxformCalculationBean.setWealthStatementOpeningWealth(openingWealth + "");
            taxformCalculationBean.setWealthStatementTaxableIncome(taxableIncomeForWelthStatement + "");
            taxformCalculationBean.setWealthStatementExpenses(expenses + "");
            taxformCalculationBean.setWealthStatementWithhodingTax(withholdingTaxForWelthStatement + "");
            taxformCalculationBean.setWealthStatementClosingWealth(closingWealth + "");
            taxformCalculationBean.setWealthStatement(welthStatement + "");
            Double wealthStatementDifference = welthStatement - closingWealth;
            taxformCalculationBean.setWealthStatementDifference((wealthStatementDifference.compareTo(0.0) != 0 ? wealthStatementDifference : 0.0) + "");

            break;

        }

        taxformCalculationBean.setCode(1);
        taxformCalculationBean.setMessage("Successfull");

        return taxformCalculationBean;
    }
}
