package com.arittek.befiler_services.controller.taxform;

import com.arittek.befiler_services.beans.*;
import com.arittek.befiler_services.beans.taxform.*;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.Assign;
import com.arittek.befiler_services.model.taxform.TaxformYears;
import com.arittek.befiler_services.model.TermsAndConditions;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.settings.taxform.ApprovedDonee;
import com.arittek.befiler_services.model.taxform.*;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.repository.taxformRepository.Taxform_Status_Repository;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.setting.taxform.ApprovedDoneeServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/taxform")
public class TaxformController {

    private UsersServices usersServices;
    private AssignServices assignServices;
    private TaxformServices taxformServices;
    private TaxformStatusServices statusServices;
    private TaxformYearsServices taxformYearsServices;
    private ApprovedDoneeServices approvedDoneeServices;
    private Taxform_Status_Repository taxform_status_repository;
    private TermsAndConditionsServices termsAndConditionsServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;

    @Value("${static.content.taxform.path}")
    private String staticContentTaxformPath;

    @Value("${befiler.url}")
    private String serverUrl;

    @Autowired
    public TaxformController(UsersServices usersServices, AssignServices assignServices, TaxformServices taxformServices, TaxformStatusServices statusServices, TaxformYearsServices taxformYearsServices, ApprovedDoneeServices approvedDoneeServices, Taxform_Status_Repository taxform_status_repository, TermsAndConditionsServices termsAndConditionsServices, FbrUserAccountInfoServices fbrUserAccountInfoServices) {
        this.usersServices = usersServices;
        this.assignServices = assignServices;
        this.taxformServices = taxformServices;
        this.statusServices = statusServices;
        this.taxformYearsServices = taxformYearsServices;
        this.approvedDoneeServices = approvedDoneeServices;
        this.taxform_status_repository = taxform_status_repository;
        this.termsAndConditionsServices = termsAndConditionsServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/calculator/{id}" ,produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> getTaxformCalculator(@PathVariable("id") Integer taxformId) {
        Logger4j.getLogger().info("First INFO");
        if (taxformId != null) {
            Logger4j.getLogger().info("Second INFO " + taxformId);
            try {
                Taxform taxform = taxformServices.findOne(taxformId);
                if(taxform != null && taxform.getTaxformYear() != null && taxform.getTaxformYear().getYear() != null) {
                    TaxformCalculationBean taxformCalculationBean = TaxformCalculator.taxformCalculator(taxform);
                    return new ResponseEntity<>(taxformCalculationBean, HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new TaxformCalculationBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new TaxformCalculationBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getByUserId" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxFormMinimal(@RequestBody TaxformBean taxformBean) {
        List<TaxformMinimalBean> taxformMinimalBeanList = new ArrayList<TaxformMinimalBean>();
        if (taxformBean != null) {
            try {
                User user;
                if (taxformBean.getUserId() != null) {
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new StatusBean(0, "Incomplete data : user"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Incomplete data : userId"), HttpStatus.OK);
                }

                List<Taxform> taxformList = taxformServices.findAllByUserAndActiveTaxformYears(user);
                if (taxformList != null) {
                    for (Taxform taxform : taxformList) {
                        TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean();

                        taxformMinimalBean.setTaxformId(taxform.getId());
                        taxformMinimalBean.setYearId(taxform.getTaxformYear().getId());
                        taxformMinimalBean.setYear(taxform.getTaxformYear().getYear());
                        taxformMinimalBean.setCnic(taxform.getCnic());
                        taxformMinimalBean.setNameAsPerCnic(taxform.getNameAsPerCnic());

                        taxformMinimalBeanList.add(taxformMinimalBean);
                    }
                    StatusBean statusBean = new StatusBean(1, "Successfully");
                    statusBean.setResponse(taxformMinimalBeanList);

                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }
                return new ResponseEntity<>(new StatusBean(1, "No record found"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<TaxformMinimalBean> saveTaxforms(@RequestBody TaxformBean taxformBean, Device device) {
        MyPrint.println("Device ::::: Mobile :: " + device.isMobile() + " :: Normal :: " + device.isNormal() + " :: Tablet :: " + device.isTablet());
        try {
            if (taxformBean != null) {
                MyPrint.println("Year:::" + taxformBean.getYear());

                User user;
                if (taxformBean.getUserId() != null) {
                    user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new TaxformMinimalBean(0, "Session expired."), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new TaxformMinimalBean(0, "Session expired."), HttpStatus.OK);
                }

                TaxformYears taxformYear;
                if (taxformBean.getYearId() != null) {
                    taxformYear = taxformYearsServices.findOneByIdAndActiveStatus(taxformBean.getYearId());
                    if (taxformYear == null) {
                        return new ResponseEntity<>(new TaxformMinimalBean(0, "Year is not selected."), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new TaxformMinimalBean(0, "Year is not selected."), HttpStatus.OK);
                }

                Taxform taxform = taxformServices.findOneByTaxformYearAndUser(taxformYear, user);
                if (taxform == null) {
                    taxform = new Taxform();

                    taxform.setTaxformYear(taxformYear);
                    taxform.setCurrentScreen(-1);
                    taxform.setUser(user);
                    taxform.setAuthorizer(user);
                    taxform.setStatus(statusServices.findOneByTaxformStatus());
                    taxform.setCreationDate(CommonUtil.getCurrentTimestamp());

                    Taxform persistTaxform = taxformServices.createTaxfrom(taxform);

                    if (persistTaxform != null && persistTaxform.getId() != null) {
                        return new ResponseEntity<>(new TaxformMinimalBean(1, persistTaxform.getId(), "Taxform created successfully."), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new TaxformMinimalBean(0, "Error while create tax form"), HttpStatus.OK);
                    }
                } else {
                    if (taxform.getStatus().getId() != 0) {
                        TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean(3, taxform.getId(), "Taxform Already Completed");
                        taxformMinimalBean.setCurrentScreen(taxform.getCurrentScreen());

                        Map<Integer, Boolean> menuMap = new HashMap<>();
                        menuMap.put(1, false);
                        menuMap.put(2, false);
                        menuMap.put(3, false);
                        menuMap.put(4, false);
                        menuMap.put(5, false);
                        menuMap.put(6, false);
                        menuMap.put(7, false);
                        menuMap.put(8, false);
                        menuMap.put(9, false);
                        menuMap.put(10, false);
                        menuMap.put(11, false);

                        menuMap.put(1, true);
                        menuMap.put(2, true);
                        menuMap.put(3, true);
                        menuMap.put(4, true);
                        menuMap.put(5, true);

                        if (taxform.getStatus().getId() == 1) {
                            taxformMinimalBean.setCurrentScreen(27);
                            taxform.setCurrentScreen(27);
                            menuMap.put(6, true);
                        } else if (taxform.getStatus().getId() == 2) {
                            taxformMinimalBean.setCurrentScreen(28);
                            taxform.setCurrentScreen(28);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                        } else if (taxform.getStatus().getId() == 3) {
                            taxformMinimalBean.setCurrentScreen(29);
                            taxform.setCurrentScreen(29);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                            menuMap.put(8, true);
                        } else if (taxform.getStatus().getId() == 4) {
                            taxformMinimalBean.setCurrentScreen(30);
                            taxform.setCurrentScreen(30);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                            menuMap.put(8, true);
                            menuMap.put(9, true);
                        } else if (taxform.getStatus().getId() == 5) {
                            taxformMinimalBean.setCurrentScreen(30);
                            taxform.setCurrentScreen(30);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                            menuMap.put(8, true);
                            menuMap.put(9, true);
                            menuMap.put(10, true);
                        } else if (taxform.getStatus().getId() == 6) {
                            taxformMinimalBean.setCurrentScreen(30);
                            taxform.setCurrentScreen(30);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                            menuMap.put(8, true);
                            menuMap.put(9, true);
                            menuMap.put(10, true);
                        }
                        else if (taxform.getStatus().getId() == 7) {
                            taxformMinimalBean.setCurrentScreen(30);
                            taxform.setCurrentScreen(30);
                            menuMap.put(6, true);
                            menuMap.put(7, true);
                            menuMap.put(8, true);
                            menuMap.put(9, true);
                            menuMap.put(10, true);
                            menuMap.put(11, true);

                            List<TaxformDocuments> taxformDocumentsList = taxform.getTaxformDocumentsList();
                            if (taxformDocumentsList != null && taxformDocumentsList.size() > 0) {
                                List<TaxformDocumentsBean> taxformDocumentsBeanList = new ArrayList<>();
                                for (TaxformDocuments taxformDocuments : taxformDocumentsList) {
                                    TaxformDocumentsBean taxformDocumentsBean = new TaxformDocumentsBean();

                                    taxformDocumentsBean.setId(taxformDocuments.getId());
                                    taxformDocumentsBean.setFilename(taxformDocuments.getDocumentDescription());

                                    if (device.isTablet() || device.isMobile()) {
                                        taxformDocumentsBean.setUrl(serverUrl + "TaxForm/" + taxformDocuments.getDocumentName());
                                    } else {
                                        taxformDocumentsBean.setBase64(org.apache.commons.codec.binary.Base64.encodeBase64String(Base64Util.readBytesFromFile(staticContentTaxformPath + taxformDocuments.getDocumentName())));
                                        if (taxformDocuments.getDocumentFormat() != null) {
                                            taxformDocumentsBean.setFiletype(taxformDocuments.getDocumentFormat());
                                        }
                                    }
                                    taxformDocumentsBeanList.add(taxformDocumentsBean);
                                }
                                taxformMinimalBean.setTaxformDocumentsBeanList(taxformDocumentsBeanList);
                            }
                        }
                        taxformMinimalBean.setResponseMap(menuMap);
                        taxformServices.updateTaxform(taxform);
                        return new ResponseEntity<>(taxformMinimalBean, HttpStatus.OK);
                    }

                    Map<Integer, Boolean> menuMap = new HashMap<>();
                    menuMap.put(1, false);
                    menuMap.put(2, false);
                    menuMap.put(3, false);
                    menuMap.put(4, false);
                    menuMap.put(5, false);
                    menuMap.put(6, false);
                    menuMap.put(7, false);
                    menuMap.put(8, false);
                    menuMap.put(9, false);
                    menuMap.put(10, false);
                    menuMap.put(11, false);

                    menuMap.put(1, true);
                    if (taxform.getTaxformIncomeTaxSalary() != null ||
                            taxform.getTaxformIncomeTaxProperty() != null ||
                            taxform.getTaxformIncomeTaxCapitalGainOnShare() != null ||
                            taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null ||
                            taxform.getTaxformIncomeTaxCapitalGainProperty() != null ||
                            taxform.getTaxformIncomeTaxOtherSources() != null ||
                            (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) ||
                            (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0)) {
                        menuMap.put(2, true);
                    }
                    if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                        menuMap.put(2, true);
                        menuMap.put(3, true);
                    }
                    if ((taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) ||
                            (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) ||
                            (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) ||
                            taxform.getTaxformTaxDeductedCollectedOther() != null) {
                        menuMap.put(2, true);
                        menuMap.put(3, true);
                        menuMap.put(4, true);
                    }
                    if (taxform.getTaxformWelthStatement() != null) {
                        menuMap.put(2, true);
                        menuMap.put(3, true);
                        menuMap.put(4, true);
                        menuMap.put(5, true);
                    }

                    TaxformMinimalBean taxformMinimalBean = new TaxformMinimalBean(2, taxform.getId(), "Taxform Already Exists");
                    taxformMinimalBean.setCurrentScreen(taxform.getCurrentScreen());
                    taxformMinimalBean.setResponseMap(menuMap);
                    return new ResponseEntity<>(taxformMinimalBean, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
        }
        return new ResponseEntity<>(new TaxformMinimalBean(0, 0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(produces = "application/json", method = RequestMethod.PUT)
    public ResponseEntity<Status> updateTaxform(@RequestBody TaxformBean taxformBean) {
        try {
            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
            }

            Taxform taxform;
            if (taxformBean.getTaxformId() != null) {
                taxform = taxformServices.findOne(taxformBean.getTaxformId());
                if (taxform == null)
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: Taxform"), HttpStatus.OK);
            } else
                return new ResponseEntity<>(new Status(0,"Incomplete Data :: Taxform"), HttpStatus.OK);

            TermsAndConditions termsAndConditions;
            if (taxformBean.getTermsAndConditionsId() != null) {
                termsAndConditions = termsAndConditionsServices.findOneById(taxformBean.getTermsAndConditionsId());
                if (termsAndConditions == null)
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: Terms & Conditions"), HttpStatus.OK);
            } else
                return new ResponseEntity<>(new Status(0,"Incomplete Data :: Terms & Conditions"), HttpStatus.OK);


            taxform.setVerifyCorporateEmployee(0);
            taxform.setStatus(statusServices.findOneByFBRStatus());
            taxform.setTermsAndConditions(termsAndConditions);

            taxformServices.updateTaxform(taxform);

            return new ResponseEntity<>(new Status(1, "Taxform Updated Successfully"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Something went wrong"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/nationalityAndResidence", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> saveNationalityAndResidence(@RequestBody TaxformBean taxformBean) {
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            try {
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if (user != null && taxform != null) {

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    if (taxformBean.getNationality() != null && !taxformBean.getNationality().isEmpty()) {
                        taxform.setNationality(taxformBean.getNationality());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Nationality"), HttpStatus.OK);
                    }

                    if (taxformBean.getResidenceStatus() != null && !taxformBean.getResidenceStatus().isEmpty()) {
                        System.out.println("Resident Status :::::::::::::::::::::::::::::::::::::::::::::::::: " + taxformBean.getResidenceStatus());
                        taxform.setResidenceStatus(taxformBean.getResidenceStatus());

                        if (!taxformBean.getResidenceStatus().equalsIgnoreCase("Resident")) {
                            taxformServices.deleteWealthStatement(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Residence Status"), HttpStatus.OK);
                    }

                    if (!taxformBean.getNationality().equals("Pakistani") && taxformBean.getResidenceStatus().equals("Resident")) {
                        if (taxformBean.getStayInPakistanBecauseOfEmployement() != null) {
                            taxform.setStayInPakistanBecauseOfEmployement(taxformBean.getStayInPakistanBecauseOfEmployement());
                            if (taxformBean.getStayInPakistanBecauseOfEmployement()) {
                                if (taxformBean.getStayInPakistanMoreThan3Years() != null) {
                                    taxform.setStayInPakistanMoreThan3Years(taxformBean.getStayInPakistanMoreThan3Years());
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Stay in Pakistan More than 3 Years"), HttpStatus.OK);
                                }
                            } else {
                                taxform.setStayInPakistanMoreThan3Years(null);
                            }
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Stay in Pakistan Because of Employement"), HttpStatus.OK);
                        }
                    } else {
                        taxform.setStayInPakistanBecauseOfEmployement(null);
                        taxform.setStayInPakistanMoreThan3Years(null);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }

                    taxformServices.updateTaxform(taxform);

                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }

            }catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/personalInformation" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> savePersonalInformation(@RequestBody TaxformBean taxformBean){
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if (user != null && taxform != null) {

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    if (taxformBean.getNameAsPerCnic() != null && !taxformBean.getNameAsPerCnic().isEmpty()) {
                        taxform.setNameAsPerCnic(taxformBean.getNameAsPerCnic());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Name as per CNIC"), HttpStatus.OK);
                    }

                    if (taxformBean.getCnic() != null && !taxformBean.getCnic().isEmpty()) {
                        taxform.setCnic(taxformBean.getCnic());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : CNIC"), HttpStatus.OK);
                    }

                    if (taxformBean.getDateOfBirth() != null && !taxformBean.getDateOfBirth().isEmpty()) {
                        Double age = CommonUtil.calculateAge(CommonUtil.changeDateStringToDate(taxformBean.getDateOfBirth()), CommonUtil.getCurrentDate(new Date()));
                        if(age >= 18) {
                            taxform.setDateOfBirth(CommonUtil.changeDateStringToDate(taxformBean.getDateOfBirth()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Invalid : Date of Birth"), HttpStatus.OK);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Date of Birth"), HttpStatus.OK);
                    }

                    if (taxformBean.getEmail() != null && !taxformBean.getEmail().isEmpty()) {
                        taxform.setEmail(taxformBean.getEmail());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Email"), HttpStatus.OK);
                    }

                    if (taxformBean.getMobileNo() != null && !taxformBean.getMobileNo().isEmpty()) {
                        taxform.setMobileNo(taxformBean.getMobileNo());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Mobile No"), HttpStatus.OK);
                    }

                    if (taxformBean.getOccupation() != null && !taxformBean.getOccupation().isEmpty()) {
                        taxform.setOccupation(taxformBean.getOccupation());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : OccupationType"), HttpStatus.OK);
                    }

                    if (taxformBean.getResidenceAddress() != null && !taxformBean.getResidenceAddress().isEmpty()) {
                        taxform.setResidenceAddress(taxformBean.getResidenceAddress());
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Residence Address"), HttpStatus.OK);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }

                    taxformServices.updateTaxform(taxform);

                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }

            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/incomeTax" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformSourceOfIncomeChecks(@RequestBody TaxformBean taxformBean){
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            try {
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Third INFO :::::"  + taxform.getId() );
                    if (taxformBean.getSalaryCheck() != null) {
                        if (!taxformBean.getSalaryCheck()) {
                            taxformServices.deleteTaxformIncomeTaxSalary(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Salary Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getPropertyCheck() != null) {
                        if (!taxformBean.getPropertyCheck()) {
                            taxformServices.deleteTaxformIncomeTaxProperty(taxform);
                            /*message.append("Property Check");*/
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getCapitalGainCheck() != null) {
                        if (!taxformBean.getCapitalGainCheck()) {
                            taxformServices.deleteTaxformIncomeTaxCapitalGain(taxform);
                            /*message.append("Capital Gain Check");*/
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Capital Gain Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getIncomeTaxOtherSourcesCheck() != null) {
                        if (!taxformBean.getIncomeTaxOtherSourcesCheck()) {
                            taxformServices.deleteTaxformIncomeTaxOtherSources(taxform);
                            /*message.append("Income Tax Other Sources Check");*/
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Capital Gain Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }

                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/incomeTax/salary" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformIncomeTaxSalary(@RequestBody TaxformBean taxformBean){
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Third INFO :::::"  + taxform.getId() );

                    /*Taxform_IncomeTax_Salary taxformIncomeTaxSalary = taxformServices.findOneTaxformIncomeTaxSalary(taxform.getId());*/
                    Taxform_IncomeTax_Salary taxformIncomeTaxSalary = taxform.getTaxformIncomeTaxSalary();

                    if (taxformIncomeTaxSalary == null){
                        taxformIncomeTaxSalary = new Taxform_IncomeTax_Salary();
                        taxformIncomeTaxSalary.setTaxform(taxform);
                    }

                    if (taxformBean.getBasicSalary() != null && !taxformBean.getBasicSalary().isEmpty()){
                        taxformIncomeTaxSalary.setBasicSalary(Double.parseDouble(taxformBean.getBasicSalary()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Basic Salary"), HttpStatus.OK);
                    }

                    if (taxformBean.getTadaCheck() != null && taxformBean.getTadaCheck()) {
                        if (taxformBean.getTada() != null && !taxformBean.getTada().isEmpty()){
                            taxformIncomeTaxSalary.setTada(Double.parseDouble(taxformBean.getTada()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : TADA"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxSalary.setTada(null);
                    }

                    if (taxformBean.getMedicalAllowanceCheck() != null && taxformBean.getMedicalAllowanceCheck()) {
                        if (taxformBean.getMedicalAllowance() != null && !taxformBean.getMedicalAllowance().isEmpty()){
                            taxformIncomeTaxSalary.setMedicalAllowance(Double.parseDouble(taxformBean.getMedicalAllowance()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Medical Allowance"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxSalary.setMedicalAllowance(null);
                    }

                    if (taxformBean.getProvidentFundByEmployeerCheck() != null && taxformBean.getProvidentFundByEmployeerCheck()) {
                        if (taxformBean.getProvidentFundByEmployeer() != null && !taxformBean.getProvidentFundByEmployeer().isEmpty()) {
                            taxformIncomeTaxSalary.setProvidentFundByEmployeer(Double.parseDouble(taxformBean.getProvidentFundByEmployeer()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Provident Fund By Employer"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxSalary.setProvidentFundByEmployeer(null);
                    }

                    if (taxformBean.getOtherAllowanceCheck() != null && taxformBean.getOtherAllowanceCheck()) {
                        if (taxformBean.getOtherAllownace() != null && !taxformBean.getOtherAllownace().isEmpty()) {
                            taxformIncomeTaxSalary.setOtherAllowance(Double.parseDouble(taxformBean.getOtherAllownace()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Other Allowance"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxSalary.setOtherAllowance(null);
                    }

                    if (taxformBean.getCompanyVehcileProvidedCheck() != null && taxformBean.getCompanyVehcileProvidedCheck()){
                        if (taxformBean.getCompanyVehicleCost() != null && !taxformBean.getCompanyVehicleCost().isEmpty()){
                            taxformIncomeTaxSalary.setCompanyVehicleCost(Double.parseDouble(taxformBean.getCompanyVehicleCost()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Company Vehicle Cost"), HttpStatus.OK);
                        }
                        if(taxformBean.getCompanyVehicleReceivedAfterJulyCheck() != null){
                            taxformIncomeTaxSalary.setCompanyVehicleReceivedAfterJuly(taxformBean.getCompanyVehicleReceivedAfterJulyCheck());
                            if (taxformBean.getCompanyVehicleReceivedAfterJulyCheck()){
                                if (taxformBean.getCompanyVehicleReceivedDate() != null && !taxformBean.getCompanyVehicleReceivedDate().isEmpty()){
                                    taxformIncomeTaxSalary.setCompanyVehicleReceivedDate(CommonUtil.changeDateStringToDate(taxformBean.getCompanyVehicleReceivedDate()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Company Vehicle Receive Date"), HttpStatus.OK);
                                }
                            }
                            else {
                                taxformIncomeTaxSalary.setCompanyVehicleReceivedDate(null);
                            }
                        } else {
                            taxformIncomeTaxSalary.setCompanyVehicleReceivedAfterJuly(false);
                            taxformIncomeTaxSalary.setCompanyVehicleReceivedDate(null);
                        }
                    } else {
                        taxformIncomeTaxSalary.setCompanyVehicleReceivedAfterJuly(false);
                        taxformIncomeTaxSalary.setCompanyVehicleCost(null);
                        taxformIncomeTaxSalary.setCompanyVehicleReceivedDate(null);
                    }

                    if (taxformBean.getProvidentOrGratuityFundReceivedCheck() != null && taxformBean.getProvidentOrGratuityFundReceivedCheck()) {
                        if (taxformBean.getProvidentOrGratuityFundReceived() != null && !taxformBean.getProvidentOrGratuityFundReceived().isEmpty()) {
                            taxformIncomeTaxSalary.setProvidentOrGratuityFundReceived(Double.parseDouble(taxformBean.getProvidentOrGratuityFundReceived()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Provident/Gratuity Fund Received"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxSalary.setProvidentOrGratuityFundReceived(null);
                    }

                    MyPrint.println("Salary Tax Borne By Employeer " + taxformBean.getSalaryTaxBorneByEmployeerCheck());
                    if(taxformBean.getSalaryTaxBorneByEmployeerCheck() != null){
                        taxformIncomeTaxSalary.setSalaryTaxBorneByEmployeerCheck(taxformBean.getSalaryTaxBorneByEmployeerCheck());
                    }

                    if(taxformBean.getSalaryTaxWithheldByEmployeer() != null && !taxformBean.getSalaryTaxWithheldByEmployeer().isEmpty()){
                        taxformIncomeTaxSalary.setSalaryTaxWithheldByEmployeer(Double.parseDouble(taxformBean.getSalaryTaxWithheldByEmployeer()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Salary Tax Withheld By Employer"), HttpStatus.OK);
                    }

                    taxformServices.createTaxformIncomeTaxSalary(taxformIncomeTaxSalary);

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/incomeTax/property", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformIncomeTaxProperty(@RequestBody TaxformBean taxformBean) {
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    Taxform_IncomeTax_Property taxformIncomeTaxProperty = taxform.getTaxformIncomeTaxProperty();
                    if (taxformIncomeTaxProperty == null) {
                        taxformIncomeTaxProperty = new Taxform_IncomeTax_Property();
                        taxformIncomeTaxProperty.setTaxform(taxform);
                    }

                    /*if (taxformBean.getDoYouDeductAnyTax() != null) {
                        taxformIncomeTaxProperty.setDoYouDeductAnyTax(taxformBean.getDoYouDeductAnyTax());
                        if (taxformBean.getDoYouDeductAnyTax()) {

                        }
                    }*/

                    if (taxformBean.getRentReceivedFromYourProperty() != null && !taxformBean.getRentReceivedFromYourProperty().isEmpty()) {
                        taxformIncomeTaxProperty.setRentReceivedFromYourProperty(Double.parseDouble(taxformBean.getRentReceivedFromYourProperty()));
                    }else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Rent"), HttpStatus.OK);
                    }

                    MyPrint.println("Do you Deduct Any tax " + taxformBean.getDoYouDeductAnyTax());
                    if (taxformBean.getDoYouDeductAnyTax() != null && taxformBean.getDoYouDeductAnyTax()) {
                        taxformIncomeTaxProperty.setDoYouDeductAnyTax(taxformBean.getDoYouDeductAnyTax());
                        if (taxformBean.getPropertyTax() != null && !taxformBean.getPropertyTax().isEmpty()) {
                            taxformIncomeTaxProperty.setPropertyTax(Double.parseDouble(taxformBean.getPropertyTax()));
                        } else
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Tax"), HttpStatus.OK);
                    }
                    else {
                        taxformIncomeTaxProperty.setDoYouDeductAnyTax(false);
                        taxformIncomeTaxProperty.setPropertyTax(null);
                    }

                    taxformServices.createTaxformIncomeTaxProperty(taxformIncomeTaxProperty);

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/incomeTax/capitalGain" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformIncomeTaxCapitalGain(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getCapitalGainOnShareCheck()){
                        Taxform_IncomeTax_CapitalGain_OnShare taxformIncomeTaxCapitalGainOnShare = taxform.getTaxformIncomeTaxCapitalGainOnShare();

                        if (taxformIncomeTaxCapitalGainOnShare == null) {
                            taxformIncomeTaxCapitalGainOnShare = new Taxform_IncomeTax_CapitalGain_OnShare();
                            taxformIncomeTaxCapitalGainOnShare.setTaxform(taxform);
                        }

                        if (taxformBean.getOnShareLessThan12MonthsCheck() != null && taxformBean.getOnShareLessThan12MonthsCheck()){
                            if (taxformBean.getOnShareLessThan12MonthsFieldsCapitalGain() != null && !taxformBean.getOnShareLessThan12MonthsFieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setLessThan12MonthsFieldsCapitalGain(Double.parseDouble(taxformBean.getOnShareLessThan12MonthsFieldsCapitalGain()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share Less Than 12 Months : Field Capital Gain"), HttpStatus.OK);
                            }

                            if (taxformBean.getOnShareLessThan12MonthsTaxDeducted() != null && !taxformBean.getOnShareLessThan12MonthsTaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setLessThan12MonthsTaxDeducted(Double.parseDouble(taxformBean.getOnShareLessThan12MonthsTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share Less Than 12 Months : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainOnShare.setLessThan12MonthsFieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainOnShare.setLessThan12MonthsTaxDeducted(null);
                        }

                        if (taxformBean.getOnShareMoreThan12ButLessThan24MonthsCheck() != null && taxformBean.getOnShareMoreThan12ButLessThan24MonthsCheck()) {
                            if (taxformBean.getOnShareMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null && !taxformBean.getOnShareMoreThan12ButLessThan24MonthsFieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(Double.parseDouble(taxformBean.getOnShareMoreThan12ButLessThan24MonthsFieldsCapitalGain()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Than 12 But Less Than 24 Months : Field Capital Gain"), HttpStatus.OK);
                            }
                            if (taxformBean.getOnShareMoreThan12ButLessThan24MonthsTaxDeducted() != null && !taxformBean.getOnShareMoreThan12ButLessThan24MonthsTaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setMoreThan12ButLessThan24MonthsTaxDeducted(Double.parseDouble(taxformBean.getOnShareMoreThan12ButLessThan24MonthsTaxDeducted()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Than 12 But Less Than 24 Months : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainOnShare.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainOnShare.setMoreThan12ButLessThan24MonthsTaxDeducted(null);
                        }

                        if (taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012Check() != null && taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012Check()) {
                            if (taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null && !taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(Double.parseDouble(taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Than 24 Months But Aquired After 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                            }

                            if (taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null && !taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012TaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(Double.parseDouble(taxformBean.getOnShareMoreThan24MonthsButAquiredAfter1July2012TaxDeducted()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Than 24 Months But Aquired After 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainOnShare.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainOnShare.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(null);
                        }

                        if (taxformBean.getOnShareAquiredBefore1July2012Check() != null && taxformBean.getOnShareAquiredBefore1July2012Check()){
                            if (taxformBean.getOnShareAquiredBefore1July2012FieldsCapitalGain() != null && !taxformBean.getOnShareAquiredBefore1July2012FieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setAquiredBefore1July2012FieldsCapitalGain(Double.parseDouble(taxformBean.getOnShareAquiredBefore1July2012FieldsCapitalGain()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Aquired Before 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                            }
                            if (taxformBean.getOnShareAquiredBefore1July2012TaxDeducted() != null && !taxformBean.getOnShareAquiredBefore1July2012TaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainOnShare.setAquiredBefore1July2012TaxDeducted(Double.parseDouble(taxformBean.getOnShareAquiredBefore1July2012TaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Aquired Before 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainOnShare.setAquiredBefore1July2012FieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainOnShare.setAquiredBefore1July2012TaxDeducted(null);
                        }


                        /*FIELDS FOR TAX FORM YEAR 2018*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxformBean.getOnShareAcquiredOnOrAfter1JulyCheck() != null && taxformBean.getOnShareAcquiredOnOrAfter1JulyCheck()){
                                if (taxformBean.getOnShareAcquiredOnOrAfter1JulyCapitalGain() != null && !taxformBean.getOnShareAcquiredOnOrAfter1JulyCapitalGain().isEmpty()){
                                    taxformIncomeTaxCapitalGainOnShare.setAcquiredOnOrAfter1JulyCapitalGain(Double.parseDouble(taxformBean.getOnShareAcquiredOnOrAfter1JulyCapitalGain()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Aquired Before 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                                }
                                if (taxformBean.getOnShareAcquiredOnOrAfter1JulyTaxDeducted() != null && !taxformBean.getOnShareAcquiredOnOrAfter1JulyTaxDeducted().isEmpty()){
                                    taxformIncomeTaxCapitalGainOnShare.setAcquiredOnOrAfter1JulyTaxDeducted(Double.parseDouble(taxformBean.getOnShareAcquiredOnOrAfter1JulyTaxDeducted()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : On Share More Aquired Before 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                                }
                            } else {
                                taxformIncomeTaxCapitalGainOnShare.setAcquiredOnOrAfter1JulyCapitalGain(null);
                                taxformIncomeTaxCapitalGainOnShare.setAcquiredOnOrAfter1JulyTaxDeducted(null);
                            }
                        }

                        taxformServices.createTaxformIncomeTaxCapitalGainOnShare(taxformIncomeTaxCapitalGainOnShare);
                    } else {
                        taxformServices.deleteTaxformIncomeTaxCapitalGainOnShare(taxform);
                    }

                    if(taxformBean.getCapitalGainMutualFundsCheck() != null && taxformBean.getCapitalGainMutualFundsCheck()){
                        Taxform_IncomeTax_CapitalGain_MutualFinds taxformIncomeTaxCapitalGainMutualFinds = taxform.getTaxformIncomeTaxCapitalGainMutualFinds();

                        if (taxformIncomeTaxCapitalGainMutualFinds == null) {
                            taxformIncomeTaxCapitalGainMutualFinds = new Taxform_IncomeTax_CapitalGain_MutualFinds();
                            taxformIncomeTaxCapitalGainMutualFinds.setTaxform(taxform);
                        }

                        if (taxformBean.getMutualFundsLessThan12MonthsCheck() != null && taxformBean.getMutualFundsLessThan12MonthsCheck()){
                            if (taxformBean.getMutualFundsLessThan12MonthsFieldsCapitalGain() != null && !taxformBean.getMutualFundsLessThan12MonthsFieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setLessThan12MonthsFieldsCapitalGain(Double.parseDouble(taxformBean.getMutualFundsLessThan12MonthsFieldsCapitalGain()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds Less Than 12 Months : Field Capital Gain"), HttpStatus.OK);
                            }

                            if (taxformBean.getMutualFundsLessThan12MonthsTaxDeducted() != null && !taxformBean.getMutualFundsLessThan12MonthsTaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setLessThan12MonthsTaxDeducted(Double.parseDouble(taxformBean.getMutualFundsLessThan12MonthsTaxDeducted()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds Less Than 12 Months : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainMutualFinds.setLessThan12MonthsFieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainMutualFinds.setLessThan12MonthsTaxDeducted(null);
                        }

                        if (taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsCheck() != null && taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsCheck()) {
                            if (taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null && !taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsFieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(Double.parseDouble(taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsFieldsCapitalGain()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Than 12 But Less Than 24 Months : Field Capital Gain"), HttpStatus.OK);
                            }
                            if (taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsTaxDeducted() != null && !taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsTaxDeducted().isEmpty()) {
                                taxformIncomeTaxCapitalGainMutualFinds.setMoreThan12ButLessThan24MonthsTaxDeducted(Double.parseDouble(taxformBean.getMutualFundsMoreThan12ButLessThan24MonthsTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Than 12 But Less Than 24 Months : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainMutualFinds.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainMutualFinds.setMoreThan12ButLessThan24MonthsTaxDeducted(null);
                        }

                        if (taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012Check() != null && taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012Check()) {
                            if (taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null && !taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(Double.parseDouble(taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Than 24 Months But Aquired After 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                            }

                            if (taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null && !taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012TaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(Double.parseDouble(taxformBean.getMutualFundsMoreThan24MonthsButAquiredAfter1July2012TaxDeducted()));
                            }else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Than 24 Months But Aquired After 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainMutualFinds.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainMutualFinds.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(null);
                        }

                        if (taxformBean.getMutualFundsAquiredBefore1July2012Check() != null && taxformBean.getMutualFundsAquiredBefore1July2012Check()){
                            if (taxformBean.getMutualFundsAquiredBefore1July2012FieldsCapitalGain() != null && !taxformBean.getMutualFundsAquiredBefore1July2012FieldsCapitalGain().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setAquiredBefore1July2012FieldsCapitalGain(Double.parseDouble(taxformBean.getMutualFundsAquiredBefore1July2012FieldsCapitalGain()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Aquired Before 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                            }
                            if (taxformBean.getMutualFundsAquiredBefore1July2012TaxDeducted() != null && !taxformBean.getMutualFundsAquiredBefore1July2012TaxDeducted().isEmpty()){
                                taxformIncomeTaxCapitalGainMutualFinds.setAquiredBefore1July2012TaxDeducted(Double.parseDouble(taxformBean.getMutualFundsAquiredBefore1July2012TaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Aquired Before 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainMutualFinds.setAquiredBefore1July2012FieldsCapitalGain(null);
                            taxformIncomeTaxCapitalGainMutualFinds.setAquiredBefore1July2012TaxDeducted(null);
                        }

                        /*FIELDS FOR TAX FORM YEAR 2018*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxformBean.getMutualFundsAcquiredOnOrAfter1JulyCheck() != null && taxformBean.getMutualFundsAcquiredOnOrAfter1JulyCheck()){
                                if (taxformBean.getMutualFundsAcquiredOnOrAfter1JulyCapitalGain() != null && !taxformBean.getMutualFundsAcquiredOnOrAfter1JulyCapitalGain().isEmpty()){
                                    taxformIncomeTaxCapitalGainMutualFinds.setAcquiredOnOrAfter1JulyCapitalGain(Double.parseDouble(taxformBean.getMutualFundsAcquiredOnOrAfter1JulyCapitalGain()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Aquired Before 1 July 2012 : Field Capital Gain"), HttpStatus.OK);
                                }
                                if (taxformBean.getMutualFundsAcquiredOnOrAfter1JulyTaxDeducted() != null && !taxformBean.getMutualFundsAcquiredOnOrAfter1JulyTaxDeducted().isEmpty()){
                                    taxformIncomeTaxCapitalGainMutualFinds.setAcquiredOnOrAfter1JulyTaxDeducted(Double.parseDouble(taxformBean.getMutualFundsAcquiredOnOrAfter1JulyTaxDeducted()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Mutual Funds More Aquired Before 1 July 2012 : Tax Deducted"), HttpStatus.OK);
                                }
                            } else {
                                taxformIncomeTaxCapitalGainMutualFinds.setAcquiredOnOrAfter1JulyCapitalGain(null);
                                taxformIncomeTaxCapitalGainMutualFinds.setAcquiredOnOrAfter1JulyTaxDeducted(null);
                            }
                        }

                        taxformServices.createTaxformIncomeTaxCapitalGainMutualFinds(taxformIncomeTaxCapitalGainMutualFinds);
                    } else {
                        taxformServices.deleteTaxformIncomeTaxCapitalGainMutualFunds(taxform);
                    }

                    // CAPITAL GAIN PROPERTY

                    if (taxformBean.getCapitalGainPropertyCheck() != null && taxformBean.getCapitalGainPropertyCheck()) {

                        Taxform_IncomeTax_CapitalGain_Property taxformIncomeTaxCapitalGainProperty = taxform.getTaxformIncomeTaxCapitalGainProperty();

                        if (taxformIncomeTaxCapitalGainProperty == null) {
                            taxformIncomeTaxCapitalGainProperty = new Taxform_IncomeTax_CapitalGain_Property();
                            taxformIncomeTaxCapitalGainProperty.setTaxform(taxform);
                        }

                        if (taxformBean.getPropertyBefore1JulyCheck() != null && taxformBean.getPropertyBefore1JulyCheck()) {

                            MyPrint.println("Property Before 1 July Upto 3 Years Check  ::" + taxformBean.getPropertyBefore1JulyAndUpto3YearsCheck());
                            if (taxformBean.getPropertyBefore1JulyAndUpto3YearsCheck()) {

                                if (taxformBean.getPropertyBefore1JulyAndUpto3YearsPurchaseCost() != null && !taxformBean.getPropertyBefore1JulyAndUpto3YearsPurchaseCost().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsPurchaseCost(Double.parseDouble(taxformBean.getPropertyBefore1JulyAndUpto3YearsPurchaseCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Before 1 July and Upto 3 Years : Purchase Cost"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyBefore1JulyAndUpto3YearsSaleCost() != null && !taxformBean.getPropertyBefore1JulyAndUpto3YearsSaleCost().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsSaleCost(Double.parseDouble(taxformBean.getPropertyBefore1JulyAndUpto3YearsSaleCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Before 1 July and Upto 3 Years : Sales Costs"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyBefore1JulyAndUpto3YearsLocation() != null && !taxformBean.getPropertyBefore1JulyAndUpto3YearsLocation().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsLocation(taxformBean.getPropertyBefore1JulyAndUpto3YearsLocation());
                                }
                            } else {
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsLocation(null);
                            }

                            MyPrint.println("Property Before 1 July More than 3 Years Check  ::" + taxformBean.getPropertyBefore1JulyAndMoreThan3YearsCheck());
                            if (taxformBean.getPropertyBefore1JulyAndMoreThan3YearsCheck() != null && taxformBean.getPropertyBefore1JulyAndMoreThan3YearsCheck()) {
                                if (taxformBean.getPropertyBefore1JulyAndMoreThan3YearsPurchaseCost() != null && !taxformBean.getPropertyBefore1JulyAndMoreThan3YearsPurchaseCost().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsPurchaseCost(Double.parseDouble(taxformBean.getPropertyBefore1JulyAndMoreThan3YearsPurchaseCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Before 1 July and MoreThan 3 Years : Purchase Cost"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyBefore1JulyAndMoreThan3YearsSaleCost() != null && !taxformBean.getPropertyBefore1JulyAndMoreThan3YearsSaleCost().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsSaleCost(Double.parseDouble(taxformBean.getPropertyBefore1JulyAndMoreThan3YearsSaleCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Before 1 July and MoreThan 3 Years : Sales Costs"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyBefore1JulyAndMoreThan3YearsLocation() != null && !taxformBean.getPropertyBefore1JulyAndMoreThan3YearsLocation().isEmpty()) {
                                    taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsLocation(taxformBean.getPropertyBefore1JulyAndMoreThan3YearsLocation());
                                }
                            } else {
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsLocation(null);
                            }
                        } else {
                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndUpto3YearsLocation(null);

                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setBefore1JulyAndMoreThan3YearsLocation(null);
                        }

                        /*FIELDS FOR TAX FORM YEAR 2018*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {

                            if (taxformBean.getPropertyAfter1JulyCheck() != null && taxformBean.getPropertyAfter1JulyCheck()) {

                                if (taxformBean.getPropertyAfter1JulyUpto1YearCheck()!= null && taxformBean.getPropertyAfter1JulyUpto1YearCheck()) {
                                    if (taxformBean.getPropertyAfter1JulyUpto1YearPurchaseCost() != null && !taxformBean.getPropertyAfter1JulyUpto1YearPurchaseCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearPurchaseCost(Double.parseDouble(taxformBean.getPropertyAfter1JulyUpto1YearPurchaseCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July Upto 1 Year : Purchase Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1JulyUpto1YearSaleCost() != null && !taxformBean.getPropertyAfter1JulyUpto1YearSaleCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearSaleCost(Double.parseDouble(taxformBean.getPropertyAfter1JulyUpto1YearSaleCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July Upto 1 Year : Sale Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1JulyUpto1YearLocation() != null && !taxformBean.getPropertyAfter1JulyUpto1YearLocation().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearLocation(taxformBean.getPropertyAfter1JulyUpto1YearLocation());
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July Upto 1 Year : Location"), HttpStatus.OK);
                                    }
                                } else {
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearPurchaseCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearSaleCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearLocation(null);
                                }

                                if (taxformBean.getPropertyAfter1July1To2YearsCheck()!= null && taxformBean.getPropertyAfter1July1To2YearsCheck()) {
                                    if (taxformBean.getPropertyAfter1July1To2YearsPurchaseCost() != null && !taxformBean.getPropertyAfter1July1To2YearsPurchaseCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsPurchaseCost(Double.parseDouble(taxformBean.getPropertyAfter1July1To2YearsPurchaseCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 1 Year And Less Than 2 Years : Purchase Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1July1To2YearsSaleCost() != null && !taxformBean.getPropertyAfter1July1To2YearsSaleCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsSaleCost(Double.parseDouble(taxformBean.getPropertyAfter1July1To2YearsSaleCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 1 Year And Less Than 2 Years : Sale Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1July1To2YearsLocation() != null && !taxformBean.getPropertyAfter1July1To2YearsLocation().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsLocation(taxformBean.getPropertyAfter1July1To2YearsLocation());
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 1 Year And Less Than 2 Years : Location"), HttpStatus.OK);
                                    }
                                } else {
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsPurchaseCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsSaleCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsLocation(null);
                                }

                                if (taxformBean.getPropertyAfter1July2To3YearsCheck()!= null && taxformBean.getPropertyAfter1July2To3YearsCheck()) {
                                    if (taxformBean.getPropertyAfter1July2To3YearsPurchaseCost() != null && !taxformBean.getPropertyAfter1July2To3YearsPurchaseCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsPurchaseCost(Double.parseDouble(taxformBean.getPropertyAfter1July2To3YearsPurchaseCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 2 Years And Less Than 3 Years : Purchase Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1July2To3YearsSaleCost() != null && !taxformBean.getPropertyAfter1July2To3YearsSaleCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsSaleCost(Double.parseDouble(taxformBean.getPropertyAfter1July2To3YearsSaleCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 2 Years And Less Than 3 Years : Sale Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1July2To3YearsLocation() != null && !taxformBean.getPropertyAfter1July2To3YearsLocation().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsLocation(taxformBean.getPropertyAfter1July2To3YearsLocation());
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 2 Years And Less Than 3 Years : Location"), HttpStatus.OK);
                                    }
                                } else {
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsPurchaseCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsSaleCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsLocation(null);
                                }


                                if (taxformBean.getPropertyAfter1JulyAndMoreThan3YearsCheck()!= null && taxformBean.getPropertyAfter1JulyAndMoreThan3YearsCheck()) {
                                    if (taxformBean.getPropertyAfter1JulyAndMoreThan3YearsPurchaseCost() != null && !taxformBean.getPropertyAfter1JulyAndMoreThan3YearsPurchaseCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsPurchaseCost(Double.parseDouble(taxformBean.getPropertyAfter1JulyAndMoreThan3YearsPurchaseCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 3 Years : Purchase Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1JulyAndMoreThan3YearsSaleCost() != null && !taxformBean.getPropertyAfter1JulyAndMoreThan3YearsSaleCost().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsSaleCost(Double.parseDouble(taxformBean.getPropertyAfter1JulyAndMoreThan3YearsSaleCost()));
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 3 Years : Sale Costs"), HttpStatus.OK);
                                    }

                                    if (taxformBean.getPropertyAfter1JulyAndMoreThan3YearsLocation() != null && !taxformBean.getPropertyAfter1JulyAndMoreThan3YearsLocation().isEmpty()){
                                        taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsLocation(taxformBean.getPropertyAfter1JulyAndMoreThan3YearsLocation());
                                    } else {
                                        return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July More Than 2 3 Years : Location"), HttpStatus.OK);
                                    }
                                } else {
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsPurchaseCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsSaleCost(null);
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsLocation(null);
                                }

                            } else {
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearLocation(null);

                                taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsLocation(null);

                                taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsLocation(null);

                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsSaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsLocation(null);
                            }

                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulySaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyLocation(null);
                        } else {
                            if (taxformBean.getPropertyAfter1JulyCheck() != null && taxformBean.getPropertyAfter1JulyCheck()) {
                                if (taxformBean.getPropertyAfter1JulyPurchaseCost() != null && !taxformBean.getPropertyAfter1JulyPurchaseCost().isEmpty()){
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyPurchaseCost(Double.parseDouble(taxformBean.getPropertyAfter1JulyPurchaseCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July : Purchase Costs"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyAfter1JulySaleCost() != null && !taxformBean.getPropertyAfter1JulySaleCost().isEmpty()){
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulySaleCost(Double.parseDouble(taxformBean.getPropertyAfter1JulySaleCost()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete Data : After 1 July : Sales Costs"), HttpStatus.OK);
                                }

                                if (taxformBean.getPropertyAfter1JulyLocation() != null && !taxformBean.getPropertyAfter1JulyLocation().isEmpty()){
                                    taxformIncomeTaxCapitalGainProperty.setAfter1JulyLocation(taxformBean.getPropertyAfter1JulyLocation());
                                }

                            } else {
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyPurchaseCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulySaleCost(null);
                                taxformIncomeTaxCapitalGainProperty.setAfter1JulyLocation(null);
                            }
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyUpto1YearLocation(null);

                            taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1July1To2YearsLocation(null);

                            taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1July2To3YearsLocation(null);

                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsPurchaseCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsSaleCost(null);
                            taxformIncomeTaxCapitalGainProperty.setAfter1JulyAndMoreThan3YearsLocation(null);
                        }

                        taxformServices.createTaxformIncomeTaxCapitalGainProperty(taxformIncomeTaxCapitalGainProperty);
                    } else {
                        taxformServices.deleteTaxformIncomeTaxCapitalGainProperty(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/incomeTax/otherSources" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> saveTaxformIncomeTaxOtherSources(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){
                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Tax Form already submitted"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");
                    Taxform_IncomeTax_OtherSources taxformIncomeTaxOtherSources = taxform.getTaxformIncomeTaxOtherSources();

                    if (taxformIncomeTaxOtherSources == null) {
                        taxformIncomeTaxOtherSources = new Taxform_IncomeTax_OtherSources();
                        taxformIncomeTaxOtherSources.setTaxform(taxform);
                    }

                    if (taxformBean.getBonusSharesAgriculturalIncomeCheck() != null && taxformBean.getBonusSharesAgriculturalIncomeCheck()) {
                        if (StringUtils.isNotEmpty(taxformBean.getBonusSharesAgriculturalIncome())) {
                            taxformIncomeTaxOtherSources.setAgriculturalIncome(Double.parseDouble(taxformBean.getBonusSharesAgriculturalIncome()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Agricultural Income"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxOtherSources.setAgriculturalIncome(null);
                    }

                    if (taxformBean.getBonusSharesCheck() != null && taxformBean.getBonusSharesCheck()) {
                        if (StringUtils.isNotEmpty(taxformBean.getBonusShares())){
                            taxformIncomeTaxOtherSources.setBonusShares(Double.parseDouble(taxformBean.getBonusShares()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Bonus Shares"), HttpStatus.OK);
                        }

                        if (StringUtils.isNotEmpty(taxformBean.getBonusSharesTaxDeducted())) {
                            taxformIncomeTaxOtherSources.setBonusSharesTaxDeducted(Double.parseDouble(taxformBean.getBonusSharesTaxDeducted()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Bonus Shares Tax Deducted"), HttpStatus.OK);
                        }
                    } else {
                        taxformIncomeTaxOtherSources.setBonusShares(null);
                        taxformIncomeTaxOtherSources.setBonusSharesTaxDeducted(null);
                    }

                    if (taxformBean.getDividentCheck() != null && taxformBean.getDividentCheck()) {

                        taxformIncomeTaxOtherSources.setDividentByPowerCompanies(Double.parseDouble(taxformBean.getDividentByPowerCompanies()));
                        taxformIncomeTaxOtherSources.setDividentByPowerCompaniesTaxDeducted(Double.parseDouble(taxformBean.getDividentByPowerCompaniesTaxDeducted()));

                        taxformIncomeTaxOtherSources.setDividentByOtherCompaniesStockFund(Double.parseDouble(taxformBean.getDividentByOtherCompaniesStockFund()));
                        taxformIncomeTaxOtherSources.setDividentByOtherCompaniesStockFundTaxDeducted(Double.parseDouble(taxformBean.getDividentByOtherCompaniesStockFundTaxDeducted()));

                        taxformIncomeTaxOtherSources.setDividentByMutualFunds(Double.parseDouble(taxformBean.getDividentByMutualFunds()));
                        taxformIncomeTaxOtherSources.setDividentByMutualFundsTaxDeducted(Double.parseDouble(taxformBean.getDividentByMutualFundsTaxDeducted()));

                    } else {
                        taxformIncomeTaxOtherSources.setDividentByPowerCompanies(null);
                        taxformIncomeTaxOtherSources.setDividentByPowerCompaniesTaxDeducted(null);

                        taxformIncomeTaxOtherSources.setDividentByOtherCompaniesStockFund(null);
                        taxformIncomeTaxOtherSources.setDividentByOtherCompaniesStockFundTaxDeducted(null);

                        taxformIncomeTaxOtherSources.setDividentByMutualFunds(null);
                        taxformIncomeTaxOtherSources.setDividentByMutualFundsTaxDeducted(null);
                    }

                    taxformServices.createTaxformIncomeTaxOtherSources(taxformIncomeTaxOtherSources);

                    if(taxformBean.getBankDepositCheck() != null && taxformBean.getBankDepositCheck() && taxformBean.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositBeans() != null && taxformBean.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositBeans().size() > 0) {
                        List<Integer> bankDepositUpdatedRecords = new ArrayList<>();

                        for(TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean incomeTaxOtherSourcesProfitOnBankDepositBean : taxformBean.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositBeans()) {
                            Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit = null;

                            if(incomeTaxOtherSourcesProfitOnBankDepositBean.getId() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDeposit = taxformServices.findOneTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(incomeTaxOtherSourcesProfitOnBankDepositBean.getId());
                            }

                            if(taxformIncomeTaxOtherSourcesProfitOnBankDeposit == null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDeposit = new Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit();
                                taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setTaxform(taxform);
                            }

                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setAccountType(incomeTaxOtherSourcesProfitOnBankDepositBean.getAccountType());
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setBankName(incomeTaxOtherSourcesProfitOnBankDepositBean.getBankName());
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setBranch(incomeTaxOtherSourcesProfitOnBankDepositBean.getBranch());
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setAccountNo(incomeTaxOtherSourcesProfitOnBankDepositBean.getAccountNo());
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setCurrency(incomeTaxOtherSourcesProfitOnBankDepositBean.getCurrency());
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setProfitAmount(Double.parseDouble(incomeTaxOtherSourcesProfitOnBankDepositBean.getProfitAmount()));
                            taxformIncomeTaxOtherSourcesProfitOnBankDeposit.setTaxDeducted(Double.parseDouble(incomeTaxOtherSourcesProfitOnBankDepositBean.getTaxDeducted()));

                            taxformServices.createTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(taxformIncomeTaxOtherSourcesProfitOnBankDeposit);

                            bankDepositUpdatedRecords.add(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getId());
                        }
                        if (bankDepositUpdatedRecords.size() > 0) {
                            taxformServices.deleteTaxformIncomeTaxOtherSourcesProfitOnBankDepositByTaxformAndNotIn(taxform, bankDepositUpdatedRecords);
                        }
                    } else {
                        taxformServices.deleteAllTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(taxform);
                    }

                    if(taxformBean.getOtherInflowsCheck() != null && taxformBean.getOtherInflowsCheck() && taxformBean.getTaxformIncomeTaxOtherSourcesOtherInFlowBeanList() != null && taxformBean.getTaxformIncomeTaxOtherSourcesOtherInFlowBeanList().size() > 0) {
                        List<Integer> otherInflowsUpdatedRecords = new ArrayList<>();

                        for(TaxformIncomeTaxOtherSourcesOtherInFlowBean otherSourcesOtherInFlowBean : taxformBean.getTaxformIncomeTaxOtherSourcesOtherInFlowBeanList()) {
                            TaxForm_IncomeTax_OtherSources_OtherInflow  otherSourcesOtherInflow = null;

                            if(otherSourcesOtherInFlowBean.getId() != null) {
                                otherSourcesOtherInflow = taxformServices.findOneTaxformIncomeTaxOtherSourcesOtherInflow(otherSourcesOtherInFlowBean.getId());
                            }

                            if(otherSourcesOtherInflow == null){
                                otherSourcesOtherInflow = new TaxForm_IncomeTax_OtherSources_OtherInflow();
                                otherSourcesOtherInflow.setTaxform(taxform);
                            }

                            otherSourcesOtherInflow.setType(otherSourcesOtherInFlowBean.getType());
                            otherSourcesOtherInflow.setAmount(Double.parseDouble(otherSourcesOtherInFlowBean.getAmount()));
                            otherSourcesOtherInflow.setMemoDescription(otherSourcesOtherInFlowBean.getMemoDescription());

                            taxformServices.createTaxformIncomeTaxOtherSourcesOtherInflow(otherSourcesOtherInflow);

                            otherInflowsUpdatedRecords.add(otherSourcesOtherInflow.getId());
                        }
                        if (otherInflowsUpdatedRecords.size() > 0) {
                            taxformServices.deleteTaxFormIncomeTaxOtherSourcesOtherInflowByTaxformAndNotIn(taxform, otherInflowsUpdatedRecords);
                        }
                    } else {
                        taxformServices.deleteAllTaxformIncomeTaxOtherSourcesInflow(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete tax null Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete tax is nj... Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/deductableAllowanceAndTaxCredit" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformDeductibleAllowanceAndTaxCredit(@RequestBody TaxformBean taxformBean){
        //Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
          //  Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    Taxform_DeductibleAllowanceOrCredit taxform_deductibleAllowanceOrCredit = taxform.getTaxformDeductibleAllowanceOrCredit();

                    if (taxform_deductibleAllowanceOrCredit == null) {
                        taxform_deductibleAllowanceOrCredit = new Taxform_DeductibleAllowanceOrCredit();
                        taxform_deductibleAllowanceOrCredit.setTaxform(taxform);
                    }

                    if (taxformBean.getDonationsToCharityCheck() != null && taxformBean.getDonationsToCharityCheck()){

                        if (taxformBean.getDonationsUnderSection61Check() != null || taxformBean.getDonationsUnderClause61Check() != null) {

                            if (taxformBean.getDonationsUnderSection61Check() != null && taxformBean.getDonationsUnderSection61Check()) {
                                if (taxformBean.getDonationsUnderSection61() != null) {
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61Check(true);
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61(Double.parseDouble(taxformBean.getDonationsUnderSection61()));
                                } else {
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61Check(false);
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61(null);
                                    return new ResponseEntity<>(new Status(0, "Incomeplete data :: donation under section 61"), HttpStatus.OK);
                                }
                            } else {
                                taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61Check(false);
                                taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61(null);
                            }

                            if (taxformBean.getDonationsUnderClause61Check() != null && taxformBean.getDonationsUnderClause61Check()) {
                                if (taxformBean.getDonationsUnderClause61() != null && taxformBean.getDonationsUnderClause61ApprovedDoneeId() != null) {
                                    ApprovedDonee approvedDonee = approvedDoneeServices.findOneByIdAndActiveStatus(taxformBean.getDonationsUnderClause61ApprovedDoneeId());
                                    if (approvedDonee == null) {
                                        return new ResponseEntity<>(new Status(0, "Please select donee"), HttpStatus.OK);
                                    }
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61Check(true);
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61ApprovedDonee(approvedDonee);
                                    taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61(Double.parseDouble(taxformBean.getDonationsUnderClause61()));
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Incomplete data :: Donation under clause 61 approved donee Id"), HttpStatus.OK);
                                }
                            } else {
                                taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61Check(false);
                                taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61(null);
                            }
                        } else {
                            return new ResponseEntity<>(new Status(0, "One check is required"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61Check(false);
                        taxform_deductibleAllowanceOrCredit.setDonationsUnderSection61(null);
                        taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61Check(false);
                        taxform_deductibleAllowanceOrCredit.setDonationsUnderClause61(null);
                    }

                    if (taxformBean.getInvestmentInSharesMutualFundsAndLifeInsuranceCheck() != null && taxformBean.getInvestmentInSharesMutualFundsAndLifeInsuranceCheck()) {
                        if (taxformBean.getInvestmentInSharesMutualFundsAndLifeInsurance() != null && !taxformBean.getInvestmentInSharesMutualFundsAndLifeInsurance().isEmpty()){
                            taxform_deductibleAllowanceOrCredit.setInvestmentInSharesMutualFundsAndLifeInsurance(Double.parseDouble(taxformBean.getInvestmentInSharesMutualFundsAndLifeInsurance()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Investment in Shares Mutual Funds and Life Insurance"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setInvestmentInSharesMutualFundsAndLifeInsurance(null);
                    }

                    if (taxformBean.getInvestmentInApprovedPensionFundCheck() != null && taxformBean.getInvestmentInApprovedPensionFundCheck()) {
                        if (taxformBean.getInvestmentInApprovedPensionFund() != null && !taxformBean.getInvestmentInApprovedPensionFund().isEmpty()) {
                            taxform_deductibleAllowanceOrCredit.setInvestmentInApprovedPensionFund(Double.parseDouble(taxformBean.getInvestmentInApprovedPensionFund()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Approved Pension Funds"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setInvestmentInApprovedPensionFund(null);
                    }

                    if (taxformBean.getInterestOrRateOnHouseHoldsCheck() != null && taxformBean.getInterestOrRateOnHouseHoldsCheck()) {
                        if (taxformBean.getInterestOrRateOnHouseHolds() != null && !taxformBean.getInterestOrRateOnHouseHolds().isEmpty()) {
                            taxform_deductibleAllowanceOrCredit.setInterestOrRateOnHouseHolds(Double.parseDouble(taxformBean.getInterestOrRateOnHouseHolds()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Interest Rate on House Holds"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setInterestOrRateOnHouseHolds(null);
                    }

                    MyPrint.println("Health Insurance Premium ::::::::::::::::::::::::::::::: " + taxformBean.getHelthInsurancePremiumCheck());
                    if (taxformBean.getHelthInsurancePremiumCheck() != null && taxformBean.getHelthInsurancePremiumCheck()){
                        if (taxformBean.getHelthInsurancePremium() != null && !taxformBean.getHelthInsurancePremium().isEmpty()) {
                            taxform_deductibleAllowanceOrCredit.setHelthInsurancePremium(Double.parseDouble(taxformBean.getHelthInsurancePremium()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Health Insurance Premium"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setHelthInsurancePremium(null);
                    }

                    if (taxformBean.getEducationAllowanceTutionFeesCheck() != null && taxformBean.getEducationAllowanceTutionFeesCheck()){
                        if (taxformBean.getEducationAllowanceTutionFees() != null && !taxformBean.getEducationAllowanceTutionFees().isEmpty()) {
                            taxform_deductibleAllowanceOrCredit.setEducationAllowanceTutionFees(Double.parseDouble(taxformBean.getEducationAllowanceTutionFees()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Education Allowance Tution Feed"), HttpStatus.OK);
                        }

                        if (taxformBean.getEducationAllowanceNoOfChildrens() != null && !taxformBean.getEducationAllowanceNoOfChildrens().isEmpty()){
                            taxform_deductibleAllowanceOrCredit.setEducationAllowanceNoOfChildrens(Integer.parseInt(taxformBean.getEducationAllowanceNoOfChildrens()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Education Allowance No of Childrens"), HttpStatus.OK);
                        }
                    } else {
                        taxform_deductibleAllowanceOrCredit.setEducationAllowanceTutionFees(null);
                        taxform_deductibleAllowanceOrCredit.setEducationAllowanceNoOfChildrens(null);
                    }


                    taxformServices.createTaxformDeductedAllowanceOrCredit(taxform_deductibleAllowanceOrCredit);

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    MyPrint.println("call before exception");
                    taxformServices.updateTaxform(taxform);



                    Logger4j.getLogger().info("Fifth INFO");
                    MyPrint.println("call before fifth info");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/taxDeductedCollected" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformTaxDeductedCollected(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            Logger4j.getLogger().info("Second INFO");
            try {
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if (user != null && taxform != null) {

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    if (taxformBean.getBankingTransactionCheck() != null) {
                        if (!taxformBean.getBankingTransactionCheck()) {
                            taxformServices.deleteTaxformTaxDeductedCollectedBankingTransactions(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Banking Transaction Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getWithholdTaxVehicleCheck() != null) {
                        if (!taxformBean.getWithholdTaxVehicleCheck()) {
                            taxformServices.deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Withhold Vehicle Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getUtilitiesCheck() != null) {
                        if (!taxformBean.getUtilitiesCheck()) {
                            taxformServices.deleteTaxformTaxDeductedCollectedUtilities(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Utilities Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getTaxDeductedCollectedOtherFieldsCheck() != null) {
                        if (!taxformBean.getTaxDeductedCollectedOtherFieldsCheck()) {
                            taxformServices.deleteTaxformTaxDeductedCollectedOthers(taxform);
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Others Check"), HttpStatus.OK);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fourth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/taxDeductedCollected/bankingTransaction" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformTaxDeductedCollectedBankingTransaction(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");
                    if(taxformBean.getBankingTransactionCheck() != null && taxformBean.getBankingTransactionCheck() && taxformBean.getTaxformTaxDeductedCollectedBankingTransactionBeans() != null && taxformBean.getTaxformTaxDeductedCollectedBankingTransactionBeans().size() > 0) {

                        List<Integer> deductedCollectedBankingTransactionUpdatedRecord = new ArrayList<>();
                        for(TaxformTaxDeductedCollectedBankingTransactionBean collectedBankingTransactionBean : taxformBean.getTaxformTaxDeductedCollectedBankingTransactionBeans()) {
                            Taxform_TaxDeductedCollected_BankingTransaction deductedCollectedBankingTransaction = null;
                            if(collectedBankingTransactionBean.getId() != null) {
                                deductedCollectedBankingTransaction = taxformServices.findOneTaxformTaxDeductedCollectedBankingTransaction(collectedBankingTransactionBean.getId());
                            }
                            if(deductedCollectedBankingTransaction == null){
                                deductedCollectedBankingTransaction = new Taxform_TaxDeductedCollected_BankingTransaction();
                                deductedCollectedBankingTransaction.setTaxform(taxform);
                            }

                            if (StringUtils.isNotEmpty(collectedBankingTransactionBean.getTransactionType())){
                                deductedCollectedBankingTransaction.setTransactionType(collectedBankingTransactionBean.getTransactionType());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Transaction Type"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getAccountType() != null && !collectedBankingTransactionBean.getAccountType().isEmpty()){
                                deductedCollectedBankingTransaction.setAccountType(collectedBankingTransactionBean.getAccountType());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Account Type"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getBankName() != null && !collectedBankingTransactionBean.getBankName().isEmpty()){
                                deductedCollectedBankingTransaction.setBankName(collectedBankingTransactionBean.getBankName());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Bank Name"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getBranch() != null && !collectedBankingTransactionBean.getBranch().isEmpty()){
                                deductedCollectedBankingTransaction.setBranch(collectedBankingTransactionBean.getBranch());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Branch"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getAccountNo() != null && !collectedBankingTransactionBean.getAccountNo().isEmpty()){
                                deductedCollectedBankingTransaction.setAccountNo(collectedBankingTransactionBean.getAccountNo());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Account No"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getCurrency() != null && !collectedBankingTransactionBean.getCurrency().isEmpty()){
                                deductedCollectedBankingTransaction.setCurrency(collectedBankingTransactionBean.getCurrency());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Currency"), HttpStatus.OK);
                            }

                            if (collectedBankingTransactionBean.getTaxDeductedAmount() != null && !collectedBankingTransactionBean.getTaxDeductedAmount().isEmpty()){
                                deductedCollectedBankingTransaction.setTaxDeductedAmount(Double.parseDouble(collectedBankingTransactionBean.getTaxDeductedAmount()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Tax Deducted Amount"), HttpStatus.OK);
                            }

                            taxformServices.createTaxformTaxDeductedCollectedBankingTransaction(deductedCollectedBankingTransaction);
                            deductedCollectedBankingTransactionUpdatedRecord.add(deductedCollectedBankingTransaction.getId());
                        }
                        if(deductedCollectedBankingTransactionUpdatedRecord != null && deductedCollectedBankingTransactionUpdatedRecord.size()>0){
                            taxformServices.deleteTaxformTaxDeductedCollectedBankingTransactionByTaxformAndNotIn(taxform,deductedCollectedBankingTransactionUpdatedRecord);
                        }

                    } else {
                        taxformServices.deleteTaxformTaxDeductedCollectedBankingTransactions(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/taxDeductedCollected/WithholdTaxVehicle" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformTaxDeductedCollectedWithholdTaxVehicle(@RequestBody TaxformBean taxformBean){
        MyPrint.println("Method is called----------------------------------------------");
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getWithholdTaxVehicleCheck() != null && taxformBean.getWithholdTaxVehicleCheck() && taxformBean.getTaxformTaxDeductedCollectedWithholdVehicleTaxBeans() != null && taxformBean.getTaxformTaxDeductedCollectedWithholdVehicleTaxBeans().size() > 0) {

                        List<Integer> withholdTaxVehicleUpdatedRecord = new ArrayList<>();
                        for(TaxformTaxDeductedCollectedWithholdVehicleTaxBean collectedWithholdVehicleTaxBean : taxformBean.getTaxformTaxDeductedCollectedWithholdVehicleTaxBeans()){

                            Taxform_TaxDeductedCollected_WithholdTaxVehicle deductedCollectedWithholdTaxVehicle = null;
                            if (collectedWithholdVehicleTaxBean.getId() != null) {
                                deductedCollectedWithholdTaxVehicle = taxformServices.findOneTaxformTaxDeductedCollectedWithholdTaxVehicle(collectedWithholdVehicleTaxBean.getId());
                            }
                            if(deductedCollectedWithholdTaxVehicle == null){
                                deductedCollectedWithholdTaxVehicle = new Taxform_TaxDeductedCollected_WithholdTaxVehicle();
                                deductedCollectedWithholdTaxVehicle.setTaxform(taxform);
                            }

                            if (collectedWithholdVehicleTaxBean.getType() != null && !collectedWithholdVehicleTaxBean.getType().isEmpty()){
                                deductedCollectedWithholdTaxVehicle.setType(collectedWithholdVehicleTaxBean.getType());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Vehicle Type"), HttpStatus.OK);
                            }

                            if (collectedWithholdVehicleTaxBean.getVehicleType() != null && !collectedWithholdVehicleTaxBean.getVehicleType().isEmpty()){
                                deductedCollectedWithholdTaxVehicle.setVehicleType(collectedWithholdVehicleTaxBean.getVehicleType());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Vehicle Type"), HttpStatus.OK);
                            }

                            if (collectedWithholdVehicleTaxBean.getVehicleRegistrationNo() != null && !collectedWithholdVehicleTaxBean.getVehicleRegistrationNo().isEmpty()){
                                deductedCollectedWithholdTaxVehicle.setVehicleRegistrationNo(collectedWithholdVehicleTaxBean.getVehicleRegistrationNo());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Registration N0"), HttpStatus.OK);
                            }

                            if (collectedWithholdVehicleTaxBean.getTaxDeducted() != null && !collectedWithholdVehicleTaxBean.getTaxDeducted().isEmpty()){
                                deductedCollectedWithholdTaxVehicle.setTaxDeducted(Double.parseDouble(collectedWithholdVehicleTaxBean.getTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Tax Deducted"), HttpStatus.OK);
                            }

                            taxformServices.createTaxformTaxDeductedCollectedWithholdTaxVehicle(deductedCollectedWithholdTaxVehicle);
                            withholdTaxVehicleUpdatedRecord.add(deductedCollectedWithholdTaxVehicle.getId());
                        }

                        if (withholdTaxVehicleUpdatedRecord.size()>0){
                            taxformServices.deleteTaxformTaxDeductedCollectedWithholdTaxVehicleByTaxformAndNotIn(taxform,withholdTaxVehicleUpdatedRecord);
                        }
                    } else {
                        taxformServices.deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);

                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/taxDeductedCollected/utilities" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformTaxDeductedCollectedUtilities(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");
                    if(taxformBean.getUtilitiesCheck() != null && taxformBean.getUtilitiesCheck() && taxformBean.getTaxformTaxDeductedCollectedUtilitiesBeans() != null && taxformBean.getTaxformTaxDeductedCollectedUtilitiesBeans().size() > 0) {

                        List<Integer> utilitiesUpdatedRecord = new ArrayList<>();
                        for(TaxformTaxDeductedCollectedUtilitiesBean utilitiesBean : taxformBean.getTaxformTaxDeductedCollectedUtilitiesBeans()){

                            Taxform_TaxDeductedCollected_Utilities deductedCollectedUtilities = null;

                            if(utilitiesBean.getId() != null){
                                deductedCollectedUtilities = taxformServices.findOneTaxformTaxDeductedCollectedUtilities(utilitiesBean.getId());
                            }
                            if(deductedCollectedUtilities == null){
                                deductedCollectedUtilities = new Taxform_TaxDeductedCollected_Utilities();
                                deductedCollectedUtilities.setTaxform(taxform);
                            }

                            if (utilitiesBean.getUtilityType() != null && !utilitiesBean.getUtilityType().isEmpty()) {
                                deductedCollectedUtilities.setUtilityType(utilitiesBean.getUtilityType());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Utility Type"), HttpStatus.OK);
                            }

                            if (utilitiesBean.getReferenceOrConsumerNo() != null && !utilitiesBean.getReferenceOrConsumerNo().isEmpty()){
                                deductedCollectedUtilities.setReferenceOrConsumerNo(utilitiesBean.getReferenceOrConsumerNo());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Reference or Column No"), HttpStatus.OK);
                            }

                            if (utilitiesBean.getProvider() != null && !utilitiesBean.getProvider().isEmpty()) {
                                deductedCollectedUtilities.setProvider(utilitiesBean.getProvider());
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Provider"), HttpStatus.OK);
                            }

                            if (utilitiesBean.getTaxDeducted() != null && !utilitiesBean.getTaxDeducted().isEmpty()) {
                                deductedCollectedUtilities.setTaxDeducted(Double.parseDouble(utilitiesBean.getTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Tax Deducted"), HttpStatus.OK);
                            }

                            taxformServices.createTaxformTaxDeductedCollectedUtilities(deductedCollectedUtilities);

                            utilitiesUpdatedRecord.add(deductedCollectedUtilities.getId());
                        }
                        if(utilitiesUpdatedRecord.size() > 0) {
                            taxformServices.deleteTaxformTaxDeductedCollectedUtilitiesByTaxformAndNotIn(taxform,utilitiesUpdatedRecord);
                        }

                    } else {
                        taxformServices.deleteTaxformTaxDeductedCollectedUtilities(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/taxDeductedCollected/others" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformTaxDeductedCollectedOthers(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getTaxDeductedCollectedOtherFieldsCheck() != null && taxformBean.getTaxDeductedCollectedOtherFieldsCheck()){
                        Taxform_TaxDeductedCollected_Other taxDeductedCollectedOther = taxform.getTaxformTaxDeductedCollectedOther();

                        if (taxDeductedCollectedOther == null) {
                            taxDeductedCollectedOther = new Taxform_TaxDeductedCollected_Other();
                            taxDeductedCollectedOther.setTaxform(taxform);
                        }

                       /* if (taxformBean.getPropertyPurchaseOrSaleTaxDeductedCheck() != null && taxformBean.getPropertyPurchaseOrSaleTaxDeductedCheck()) {
                            if (taxformBean.getPropertyPurchaseOrSaleTaxDeducted() != null && !taxformBean.getPropertyPurchaseOrSaleTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setPropertyPurchaseOrSaleTaxDeducted(Double.parseDouble(taxformBean.getPropertyPurchaseOrSaleTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Purchase/Sale Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setPropertyPurchaseOrSaleTaxDeducted(null);
                        }*/

                       //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx


                        if (taxformBean.getPropertyPurchaseTaxDeductedCheck() != null && taxformBean.getPropertyPurchaseTaxDeductedCheck()) {
                            if (taxformBean.getPropertyPurchaseTaxDeducted() != null && !taxformBean.getPropertyPurchaseTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setPropertyPurchaseTaxDeducted(Double.parseDouble(taxformBean.getPropertyPurchaseTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Purchase Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setPropertyPurchaseTaxDeducted(null);
                        }


                        if (taxformBean.getPropertySaleTaxDeductedCheck() != null && taxformBean.getPropertySaleTaxDeductedCheck()) {
                            if (taxformBean.getPropertySaleTaxDeducted() != null && !taxformBean.getPropertySaleTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setPropertySaleTaxDeducted(Double.parseDouble(taxformBean.getPropertySaleTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Property Sale Tax Deducted"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setPropertySaleTaxDeducted(null);
                        }

                        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx


                        if (taxformBean.getEducationFeeTaxDeductedCheck() != null && taxformBean.getEducationFeeTaxDeductedCheck()) {
                            if (taxformBean.getEducationFeeTaxDeducted() != null && !taxformBean.getEducationFeeTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setEducationFeeTaxDeductedTaxDeducted(Double.parseDouble(taxformBean.getEducationFeeTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Education Allowance"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setEducationFeeTaxDeductedTaxDeducted(null);
                        }

                        if (taxformBean.getAirTicketInYearTaxDeductedCheck() != null && taxformBean.getAirTicketInYearTaxDeductedCheck()) {
                            if (taxformBean.getAirTicketInYearTaxDeducted() != null && !taxformBean.getAirTicketInYearTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setAirTicketInYearTaxDeducted(Double.parseDouble(taxformBean.getAirTicketInYearTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Air Ticket in Year"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setAirTicketInYearTaxDeducted(null);
                        }

                        if (taxformBean.getWithdrawalFromPensionFundCheck() != null && taxformBean.getWithdrawalFromPensionFundCheck()) {
                            if (taxformBean.getWithdrawalFromPensionFund() != null && !taxformBean.getWithdrawalFromPensionFund().isEmpty()) {
                                taxDeductedCollectedOther.setWithdrawalFromPensionFund(Double.parseDouble(taxformBean.getWithdrawalFromPensionFund()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Air Ticket in Year"), HttpStatus.OK);
                            }

                            if (taxformBean.getWithdrawalFromPensionFundTaxDeducted() != null && !taxformBean.getWithdrawalFromPensionFundTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setWithdrawalFromPensionFundTaxDeducted(Double.parseDouble(taxformBean.getWithdrawalFromPensionFundTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Air Ticket in Year"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setWithdrawalFromPensionFund(null);
                            taxDeductedCollectedOther.setWithdrawalFromPensionFundTaxDeducted(null);
                        }

                        if (taxformBean.getTaxDeductedCollectedFunctionsAndGatheringsCheck() != null && taxformBean.getTaxDeductedCollectedFunctionsAndGatheringsCheck()) {
                            if (taxformBean.getTaxDeductedCollectedFunctionsAndGatherings() != null && !taxformBean.getTaxDeductedCollectedFunctionsAndGatherings().isEmpty()) {
                                taxDeductedCollectedOther.setFunctionsAndGatherings(Double.parseDouble(taxformBean.getTaxDeductedCollectedFunctionsAndGatherings()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Functions and Gatherings"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setFunctionsAndGatherings(null);
                        }

                        if (taxformBean.getTaxRefundOfPriorYearCheck() != null && taxformBean.getTaxRefundOfPriorYearCheck()) {
                            if (taxformBean.getTaxRefundOfPriorYear() != null && !taxformBean.getTaxRefundOfPriorYear().isEmpty()) {
                                taxDeductedCollectedOther.setTaxRefundOfPriorYear(Double.parseDouble(taxformBean.getTaxRefundOfPriorYear()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Tax refund from prior years"), HttpStatus.OK);
                            }
                        } else {
                            taxDeductedCollectedOther.setTaxRefundOfPriorYear(null);
                        }

                        /*if (taxformBean.getWithdrawalFromPensionFundTaxDeductedCheck()) {
                            if (taxformBean.getWithdrawalFromPensionFundTaxDeducted() != null && !taxformBean.getWithdrawalFromPensionFundTaxDeducted().isEmpty()) {
                                taxDeductedCollectedOther.setWithdrawalFromPensionFundTaxDeducted(Double.parseDouble(taxformBean.getWithdrawalFromPensionFundTaxDeducted()));
                            } else {
                                return new ResponseEntity<>(new Status(0, "Incomplete Data : Air Ticket in Year"), HttpStatus.OK);
                            }
                        }*/

                        taxformServices.createTaxformTaxDeductedCollectedOther(taxDeductedCollectedOther);

                        if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                            taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                        }
                        taxformServices.updateTaxform(taxform);

                        Logger4j.getLogger().info("Fifth INFO");
                        return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                    }
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatement(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if (taxformBean.getWealthStatementPropertyCheck() != null) {
                        if (!taxformBean.getWealthStatementPropertyCheck()){
                            taxformServices.deleteWealthStatementPropertyDetail(taxform);
                        }
                    }

                    if (taxformBean.getBankAccountOrInvestmentCheck() != null) {
                        if (!taxformBean.getBankAccountOrInvestmentCheck()){
                            taxformServices.deleteWealthStatementBankAccountsOrInvestments(taxform);
                        }
                    }

                    if (taxformBean.getOtherReceivableAssets() != null) {
                        if (!taxformBean.getOtherReceivableAssets()){
                            taxformServices.deleteWealthStatementOtherRecrivablesOrAsstes(taxform);
                        }
                    }

                    if (taxformBean.getOwnVehicleCheck() != null) {
                        if (!taxformBean.getOwnVehicleCheck()){
                            taxformServices.deleteWealthStatementOwnVehicle(taxform);
                        }
                    }

                    if (taxformBean.getOtherPossessionsCheck() != null) {
                        if (!taxformBean.getOtherPossessionsCheck()){
                            taxformServices.deleteWealthStatementOtherPossessions(taxform);
                        }
                    }

                    if (taxformBean.getAssetsOutsidePakistanCheck() != null) {
                        if (!taxformBean.getAssetsOutsidePakistanCheck()){
                            taxformServices.deleteWealthStatementAssetsOutSidePakistan(taxform);
                        }
                    }

                    if (taxformBean.getOweAnyLoanOrCreditCheck() != null) {
                        if (!taxformBean.getOweAnyLoanOrCreditCheck()){
                            taxformServices.deleteWealthStatementOweAnyLoansOrCredit(taxform);
                        }
                    }

                    if (taxformBean.getPersonalExpensesCheck() != null) {
                        if (!taxformBean.getPersonalExpensesCheck()){
                            taxformServices.deleteWealthStatementDetailsOfPersonalExpenses(taxform);
                        }
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Status(0, "Incomplete Data : Openning Wealth"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, CommonUtil.getRootCause(e).getMessage()), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/openingWealth" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementOpenningWealth(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getOpeningWealth() != null && !taxformBean.getOpeningWealth().isEmpty()){
                        Taxform_WelthStatement taxformWelthStatement = taxform.getTaxformWelthStatement();

                        if (taxformWelthStatement == null) {
                            taxformWelthStatement = new Taxform_WelthStatement();
                            taxformWelthStatement.setTaxform(taxform);
                        }

                        if (taxformBean.getOpeningWealth() != null && !taxformBean.getOpeningWealth().isEmpty()) {
                            taxformWelthStatement.setOpeningWealth(Double.parseDouble(taxformBean.getOpeningWealth()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Openning Wealth"), HttpStatus.OK);
                        }


                        taxformServices.createTaxformWelthStatement(taxformWelthStatement);

                        if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                            taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                        }
                        taxformServices.updateTaxform(taxform);

                        Logger4j.getLogger().info("Fifth INFO");
                        return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Openning Wealth"), HttpStatus.OK);
                    }
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/property" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementProperty(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getWealthStatementPropertyCheck() != null && taxformBean.getWealthStatementPropertyCheck() && taxformBean.getTaxformWealthStatementPropertyDetailsBeans() != null && taxformBean.getTaxformWealthStatementPropertyDetailsBeans().size() > 0){
                        List<Integer> propertyDetailUpdatedRecords = new ArrayList<>();
                        for(TaxformWealthStatementPropertyDetailsBean wealthStatementPropertyDetailsBean : taxformBean.getTaxformWealthStatementPropertyDetailsBeans()) {

                            Taxform_WelthStatement_PropertyDetail taxformWelthStatementPropertyDetail = null;

                            if (wealthStatementPropertyDetailsBean.getId() != null) {
                                taxformWelthStatementPropertyDetail = taxformServices.findOneTaxformWealthStatementPropertyDetail(wealthStatementPropertyDetailsBean.getId());
                            }

                            if (taxformWelthStatementPropertyDetail == null) {
                                taxformWelthStatementPropertyDetail = new Taxform_WelthStatement_PropertyDetail();
                                taxformWelthStatementPropertyDetail.setTaxform(taxform);
                            }

                            taxformWelthStatementPropertyDetail.setPropertyType(wealthStatementPropertyDetailsBean.getPropertyType());
                            taxformWelthStatementPropertyDetail.setUnitNo(wealthStatementPropertyDetailsBean.getUnitNo());
                            taxformWelthStatementPropertyDetail.setAreaLocalityRoad(wealthStatementPropertyDetailsBean.getAreaLocalityRoad());
                            taxformWelthStatementPropertyDetail.setCity(wealthStatementPropertyDetailsBean.getCity());
                            taxformWelthStatementPropertyDetail.setArea(wealthStatementPropertyDetailsBean.getArea());
                            taxformWelthStatementPropertyDetail.setPropertyCost(Double.parseDouble(wealthStatementPropertyDetailsBean.getPropertyCost()));

                            taxformServices.createTaxformWelthStatementPropertyDetail(taxformWelthStatementPropertyDetail);
                            propertyDetailUpdatedRecords.add(taxformWelthStatementPropertyDetail.getId());
                        }
                        if(propertyDetailUpdatedRecords!= null && propertyDetailUpdatedRecords.size()>0){
                            taxformServices.deleteTaxformWelthStatementPropertyDetailByTaxformAndNotIn(taxform,propertyDetailUpdatedRecords);
                        }
                    } else {
                        taxformServices.deleteWealthStatementPropertyDetail(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/bankAccountsOrInvestment" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementBankAccountsOrInvestments(@RequestBody TaxformBean taxformBean) {
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null) {
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getBankAccountOrInvestmentCheck() != null && taxformBean.getBankAccountOrInvestmentCheck() && taxformBean.getTaxformWealthStatementBankAccountsOrInvestmentBeans() != null && taxformBean.getTaxformWealthStatementBankAccountsOrInvestmentBeans().size() > 0) {

                        List<Integer> bankAccountsOrInvestmentUpdatedRecord = new ArrayList<>();
                        for(TaxformWealthStatementBankAccountsOrInvestmentBean bankAccountsOrInvestmentBean : taxformBean.getTaxformWealthStatementBankAccountsOrInvestmentBeans()){
                            Taxform_WelthStatement_BankAccountsOrInvestments bankAccountsOrInvestments = null;
                            if (bankAccountsOrInvestmentBean.getId() != null) {
                                bankAccountsOrInvestments = taxformServices.findOneTaxformWelthStatementBankAccountsOrInvestments(bankAccountsOrInvestmentBean.getId());
                            }
                            if (bankAccountsOrInvestments == null){
                                bankAccountsOrInvestments = new Taxform_WelthStatement_BankAccountsOrInvestments();
                                bankAccountsOrInvestments.setTaxform(taxform);
                            }
                            bankAccountsOrInvestments.setForm(bankAccountsOrInvestmentBean.getForm());
                            bankAccountsOrInvestments.setAccountOrInstructionNo(bankAccountsOrInvestmentBean.getAccountOrInstructionNo());
                            bankAccountsOrInvestments.setInstitutionNameOrInduvidualCnic(bankAccountsOrInvestmentBean.getInstitutionNameOrInduvidualCnic());
                            bankAccountsOrInvestments.setBranchName(bankAccountsOrInvestmentBean.getBranchName());
                            bankAccountsOrInvestments.setCost(Double.parseDouble(bankAccountsOrInvestmentBean.getCost()));

                            taxformServices.createTaxformWelthStatementBankAccountsOrInvestments(bankAccountsOrInvestments);
                            bankAccountsOrInvestmentUpdatedRecord.add(bankAccountsOrInvestments.getId());
                        }

                        if(bankAccountsOrInvestmentUpdatedRecord != null && bankAccountsOrInvestmentUpdatedRecord.size()>0){
                            taxformServices.deleteTaxformWelthStatementBankAccountsOrInvestmentsByTaxformAndNotIn(taxform,bankAccountsOrInvestmentUpdatedRecord);
                        }

                    } else {
                        taxformServices.deleteWealthStatementBankAccountsOrInvestments(taxform);
                    }
                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/otherReceivableAssets" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementOtherReceivableAssets(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");
                    if(taxformBean.getOtherReceivableAssets() != null && taxformBean.getOtherReceivableAssets() && taxformBean.getTaxformWealthStatementOtherReceivablesOrAssetsBeans() != null && taxformBean.getTaxformWealthStatementOtherReceivablesOrAssetsBeans().size() > 0) {

                        List<Integer> otherAssetsUpdatedRecords = new ArrayList<>();
                        for (TaxformWealthStatementOtherReceivablesOrAssetsBean receivablesOrAssetsBean : taxformBean.getTaxformWealthStatementOtherReceivablesOrAssetsBeans()) {

                            Taxform_WelthStatement_OtherReceivablesOrAssets otherReceivablesOrAssets = null;
                            if (receivablesOrAssetsBean.getId() != null) {
                                otherReceivablesOrAssets = taxformServices.findOneTaxformWelthStatementOtherReceivablesOrAssets(receivablesOrAssetsBean.getId());
                            }
                            if (otherReceivablesOrAssets == null) {
                                otherReceivablesOrAssets = new Taxform_WelthStatement_OtherReceivablesOrAssets();
                                otherReceivablesOrAssets.setTaxform(taxform);
                            }
                            otherReceivablesOrAssets.setForm(receivablesOrAssetsBean.getForm());
                            otherReceivablesOrAssets.setInstitutionNameOrIndividualCnic(receivablesOrAssetsBean.getInstitutionNameOrIndividualCnic());
                            otherReceivablesOrAssets.setValueAtCost(Double.parseDouble(receivablesOrAssetsBean.getValueAtCost()));

                            taxformServices.createTaxformWelthStatementOtherReceivablesOrAssets(otherReceivablesOrAssets);

                            otherAssetsUpdatedRecords.add(otherReceivablesOrAssets.getId());

                        }
                        if(otherAssetsUpdatedRecords != null && otherAssetsUpdatedRecords.size()>0){
                            taxformServices.deleteTaxformWelthStatementOtherReceivablesOrAssetsByTaxformAndNotIn(taxform,otherAssetsUpdatedRecords);
                        }

                    } else {
                        taxformServices.deleteWealthStatementOtherRecrivablesOrAsstes(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/ownVehicle" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementOwnVehicle(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getOwnVehicleCheck() != null && taxformBean.getOwnVehicleCheck() && taxformBean.getTaxformWealthStatementOwnVehicleBeans() != null && !taxformBean.getTaxformWealthStatementOwnVehicleBeans().isEmpty()){
                        List<Integer> OwnVehicleUpdatedRecords = new ArrayList<>();
                        for(TaxformWealthStatementOwnVehicleBean statementOwnVehicleBean : taxformBean.getTaxformWealthStatementOwnVehicleBeans()) {
                            Taxform_WelthStatement_OwnVehicle welthStatementOwnVehicle = null;
                            if(statementOwnVehicleBean.getId()!= null) {
                                welthStatementOwnVehicle = taxformServices.findOneTaxformWelthStatementOwnVehicle(statementOwnVehicleBean.getId());
                            }
                            if(welthStatementOwnVehicle == null){
                                welthStatementOwnVehicle = new Taxform_WelthStatement_OwnVehicle();
                                welthStatementOwnVehicle.setTaxform(taxform);
                            }

                            welthStatementOwnVehicle.setForm(statementOwnVehicleBean.getForm());
                            welthStatementOwnVehicle.setEtdRegistrationNo(statementOwnVehicleBean.getEtdRegistrationNo());
                            welthStatementOwnVehicle.setMaker(statementOwnVehicleBean.getMaker());
                            welthStatementOwnVehicle.setCapacity(statementOwnVehicleBean.getCapacity());
                            welthStatementOwnVehicle.setValueAtCost(Double.parseDouble(statementOwnVehicleBean.getValueAtCost()));

                            taxformServices.createTaxformWelthStatementOwnVehicle(welthStatementOwnVehicle);

                            OwnVehicleUpdatedRecords.add(welthStatementOwnVehicle.getId());
                        }

                        if(OwnVehicleUpdatedRecords != null && OwnVehicleUpdatedRecords.size()>0){
                            taxformServices.deleteTaxformWelthStatementOwnVehicleByTaxformAndNotIn(taxform,OwnVehicleUpdatedRecords);
                        }

                    } else {
                        taxformServices.deleteWealthStatementOwnVehicle(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/otherPossessions" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementOtherPossessions(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getOtherPossessionsCheck() != null && taxformBean.getOtherPossessionsCheck() && taxformBean.getTaxformWealthStatementOtherPossessionsBeans() != null && taxformBean.getTaxformWealthStatementOtherPossessionsBeans().size() > 0) {
                        List<Integer> otherPossessionsUpdatedRecords = new ArrayList<>();
                        for(TaxformWealthStatementOtherPossessionsBean otherPossessionsBean : taxformBean.getTaxformWealthStatementOtherPossessionsBeans()) {
                            Taxform_WelthStatement_OtherPossessions statementOtherPossessions =null;

                            if(otherPossessionsBean != null && otherPossessionsBean.getId()!= null) {
                                statementOtherPossessions = taxformServices.findOneTaxformWelthStatementOtherPossessions(otherPossessionsBean.getId());
                            }
                            if(statementOtherPossessions == null){
                                statementOtherPossessions = new Taxform_WelthStatement_OtherPossessions();
                                statementOtherPossessions.setTaxform(taxform);
                            }
                            statementOtherPossessions.setDescription(otherPossessionsBean.getDescription());
                            statementOtherPossessions.setValueAtCost(Double.parseDouble(otherPossessionsBean.getValueAtCost()));

                            statementOtherPossessions.setPossessionType(otherPossessionsBean.getPossessionType());

                            taxformServices.createTaxformWelthStatementOtherPossessions(statementOtherPossessions);

                            otherPossessionsUpdatedRecords.add(statementOtherPossessions.getId());

                        }

                        if(otherPossessionsUpdatedRecords != null && otherPossessionsUpdatedRecords.size()>0){
                            taxformServices.deleteTaxformWelthStatementOtherPossessionsByTaxformAndNotIn(taxform,otherPossessionsUpdatedRecords);
                        }

                    } else {
                        taxformServices.deleteWealthStatementOtherPossessions(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/assetsOutsidePakistan" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementAssetsOutsidePakistan(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getAssetsOutsidePakistanCheck() != null && taxformBean.getAssetsOutsidePakistanCheck() && taxformBean.getTaxformWealthStatementAssetsOutsidePakistanBeans() != null && taxformBean.getTaxformWealthStatementAssetsOutsidePakistanBeans().size() > 0) {

                        List<Integer> assetsOutsidePakistanUpdatedRecords = new ArrayList<>();

                        for(TaxformWealthStatementAssetsOutsidePakistanBean outsidePakistanBean : taxformBean.getTaxformWealthStatementAssetsOutsidePakistanBeans()) {
                            Taxform_WelthStatement_AssetsOutSidePakistan statementAssetsOutSidePakistan = null;
                            if(outsidePakistanBean!= null && outsidePakistanBean.getId()!= null) {
                                statementAssetsOutSidePakistan = taxformServices.findOneTaxformWelthStatementAssetsOutSidePakistan(outsidePakistanBean.getId());
                            }
                            if(statementAssetsOutSidePakistan == null){
                                statementAssetsOutSidePakistan = new Taxform_WelthStatement_AssetsOutSidePakistan();
                                statementAssetsOutSidePakistan.setTaxform(taxform);
                            }

                            statementAssetsOutSidePakistan.setDescription(outsidePakistanBean.getDescription());
                            statementAssetsOutSidePakistan.setValueAtCost(Double.parseDouble(outsidePakistanBean.getValueAtCost()));

                            taxformServices.createTaxformWelthStatementAssetsOutSidePakistan(statementAssetsOutSidePakistan);

                            assetsOutsidePakistanUpdatedRecords.add(statementAssetsOutSidePakistan.getId());

                        }
                        if(assetsOutsidePakistanUpdatedRecords != null && assetsOutsidePakistanUpdatedRecords.size()>0){
                            taxformServices.deleteTaxformWelthStatementAssetsOutSidePakistanByTaxformAndNotIn(taxform,assetsOutsidePakistanUpdatedRecords);
                        }

                    } else {
                        taxformServices.deleteWealthStatementAssetsOutSidePakistan(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/oweAnyLoanOrCredit" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementOweAnyLoanOrCredit(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if(taxformBean.getOweAnyLoanOrCreditCheck() != null && taxformBean.getOweAnyLoanOrCreditCheck() && taxformBean.getTaxformWealthStatementOweAnyLoanOrCreditBeans() != null && taxformBean.getTaxformWealthStatementOweAnyLoanOrCreditBeans().size() > 0) {
                        List<Integer> oweAnyLoansOrCreditUpdatedRecord = new ArrayList<>();
                        for(TaxformWealthStatementOweAnyLoanOrCreditBean oweAnyLoanOrCreditBean : taxformBean.getTaxformWealthStatementOweAnyLoanOrCreditBeans()) {
                            Taxform_WelthStatement_OweAnyLoansOrCredit oweAnyLoansOrCredit = null;
                            if(oweAnyLoanOrCreditBean.getId()!= null) {
                                oweAnyLoansOrCredit = taxformServices.findOneTaxformWelthStatementOweAnyLoansOrCredit(oweAnyLoanOrCreditBean.getId());
                            }
                            if(oweAnyLoansOrCredit == null) {
                                oweAnyLoansOrCredit = new Taxform_WelthStatement_OweAnyLoansOrCredit();
                                oweAnyLoansOrCredit.setTaxform(taxform);
                            }

                            oweAnyLoansOrCredit.setForm(oweAnyLoanOrCreditBean.getForm());
                            oweAnyLoansOrCredit.setCreatorsNtnOrCnic(oweAnyLoanOrCreditBean.getCreatorsNtnOrCnic());
                            oweAnyLoansOrCredit.setCreatorsName(oweAnyLoanOrCreditBean.getCreatorsName());
                            oweAnyLoansOrCredit.setValueAtCost(Double.parseDouble(oweAnyLoanOrCreditBean.getValueAtCost()));

                            taxformServices.createTaxformWelthStatementOweAnyLoansOrCredit(oweAnyLoansOrCredit);
                            oweAnyLoansOrCreditUpdatedRecord.add(oweAnyLoansOrCredit.getId());
                        }
                        if(oweAnyLoansOrCreditUpdatedRecord.size() > 0) {
                            taxformServices.deleteTaxformWelthStatementOweAnyLoansOrCreditByTaxformAndNotIn(taxform,oweAnyLoansOrCreditUpdatedRecord);
                        }
                    } else {
                        taxformServices.deleteWealthStatementOweAnyLoansOrCredit(taxform);
                    }

                    if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                        taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                    } else {
                        return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                    }
                    taxformServices.updateTaxform(taxform);

                    Logger4j.getLogger().info("Fifth INFO");
                    return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/wealthStatement/personalExpenses" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> saveTaxformWealthStatementPersonalExpenses(@RequestBody TaxformBean taxformBean){
        Logger4j.getLogger().info("First INFO");
        if (taxformBean != null && taxformBean.getTaxformId() != null && taxformBean.getUserId() != null){
            Logger4j.getLogger().info("Second INFO");
            try {
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());

                if(user != null && taxform != null){

                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }

                    Logger4j.getLogger().info("Fourth INFO");

                    if (taxformBean.getPersonalExpensesCheck()) {
                        Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementDetailsOfPersonalExpense = taxform.getTaxformWelthStatementDetailsOfPersonalExpense();

                        if (taxformWelthStatementDetailsOfPersonalExpense == null) {
                            taxformWelthStatementDetailsOfPersonalExpense = new Taxform_WelthStatement_DetailsOfPersonalExpense();
                            taxformWelthStatementDetailsOfPersonalExpense.setTaxform(taxform);
                        }

                        if (taxformBean.getRent() != null && !taxformBean.getRent().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setRent(Double.parseDouble(taxformBean.getRent()));
                        }
                        if (taxformBean.getRatesTaxesChargeCess() != null && !taxformBean.getRatesTaxesChargeCess().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setRatesTaxesChargeCess(Double.parseDouble(taxformBean.getRatesTaxesChargeCess()));
                        }
                        if (taxformBean.getVehicleRunningOrMaintenance() != null && !taxformBean.getVehicleRunningOrMaintenance().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setVehicleRunningOrMaintenance(Double.parseDouble(taxformBean.getVehicleRunningOrMaintenance()));
                        }
                        if (taxformBean.getTravelling() != null && !taxformBean.getTravelling().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setTravelling(Double.parseDouble(taxformBean.getTravelling()));
                        }
                        if (taxformBean.getElectricity() != null && !taxformBean.getElectricity().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setElectricity(Double.parseDouble(taxformBean.getElectricity()));
                        }
                        if (taxformBean.getWater() != null && !taxformBean.getWater().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setWater(Double.parseDouble(taxformBean.getWater()));
                        }
                        if (taxformBean.getGas() != null && !taxformBean.getGas().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setGas(Double.parseDouble(taxformBean.getGas()));
                        }
                        if (taxformBean.getTelephone() != null && !taxformBean.getTelephone().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setTelephone(Double.parseDouble(taxformBean.getTelephone()));
                        }
                        if (taxformBean.getAssetsInsuranceOrSecurity() != null && !taxformBean.getAssetsInsuranceOrSecurity().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setAssetsInsuranceOrSecurity(Double.parseDouble(taxformBean.getAssetsInsuranceOrSecurity()));
                        }
                        if (taxformBean.getMedical() != null && !taxformBean.getMedical().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setMedical(Double.parseDouble(taxformBean.getMedical()));
                        }
                        if (taxformBean.getEducational() != null && !taxformBean.getEducational().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setEducational(Double.parseDouble(taxformBean.getEducational()));
                        }
                        if (taxformBean.getClub() != null && !taxformBean.getClub().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setClub(Double.parseDouble(taxformBean.getClub()));
                        }
                        if (taxformBean.getFunctionsOrGatherings() != null && !taxformBean.getFunctionsOrGatherings().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setFunctionsOrGatherings(Double.parseDouble(taxformBean.getFunctionsOrGatherings()));
                        }
                        if (taxformBean.getDonationZakatAnnuityProfitOnDebutEtc() != null && !taxformBean.getDonationZakatAnnuityProfitOnDebutEtc().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setDonationZakatAnnuityProfitOnDebutEtc(Double.parseDouble(taxformBean.getDonationZakatAnnuityProfitOnDebutEtc()));
                        }
                        if (taxformBean.getOtherPersonalOrHouseholdExpense() != null && !taxformBean.getOtherPersonalOrHouseholdExpense().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setOtherPersonalOrHouseholdExpense(Double.parseDouble(taxformBean.getOtherPersonalOrHouseholdExpense()));
                        }
                        if (taxformBean.getGift() != null && !taxformBean.getGift().isEmpty()) {
                            taxformWelthStatementDetailsOfPersonalExpense.setGift(Double.parseDouble(taxformBean.getGift()));
                        }

                        taxformServices.createTaxformWelthStatementDetailsOfPersonalExpense(taxformWelthStatementDetailsOfPersonalExpense);

                        if (taxformBean.getCurrentScreen() != null && CommonUtil.stringContainsOnlyNumbers(taxformBean.getCurrentScreen())) {
                            taxform.setCurrentScreen(Integer.parseInt(taxformBean.getCurrentScreen()));
                        } else {
                            return new ResponseEntity<>(new Status(0, "Incomplete Data : Cannot Detect Screen Sequence"), HttpStatus.OK);
                        }
                        taxformServices.updateTaxform(taxform);

                        Logger4j.getLogger().info("Fifth INFO");
                        return new ResponseEntity<>(new Status(1, "Successfully Updated"), HttpStatus.OK);
                    }
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TaxformDetailedBean> getTaxform(@PathVariable("id") Integer taxformId) {

        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user1 = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("User Name :::::::: "  + user1.getUsername());*/

        User user = null;
        try {
            if(taxformId != null && taxformId != 0){
                MyPrint.println("Inside First IF : " + taxformId);
                Taxform taxform = taxformServices.findOne(taxformId);
                if(taxform != null && taxform.getTaxformYear() != null){
                    TaxformDetailedBean taxformDetailedBean = new TaxformDetailedBean();

                    if (taxform.getCurrentScreen() != null) {
                        taxformDetailedBean.setCurrentScreen(taxform.getCurrentScreen());
                    }

                    if (taxform.getTaxformYear() != null) {
                        taxformDetailedBean.setYear(taxform.getTaxformYear().getYear() + "");
                    }

                    if(taxform.getResidenceStatus() != null) {
                        taxformDetailedBean.setResidenceStatus(taxform.getResidenceStatus());
                    }

                    if (taxform.getNationality() != null && !taxform.getNationality().isEmpty()) {
                        taxformDetailedBean.setNationality(taxform.getNationality());
                    }

                    if (taxform.getStayInPakistanBecauseOfEmployement() != null) {
                        taxformDetailedBean.setStayInPakistanBecauseOfEmployement(taxform.getStayInPakistanBecauseOfEmployement());
                    }

                    if (taxform.getStayInPakistanMoreThan3Years() != null) {
                        taxformDetailedBean.setStayInPakistanMoreThan3Years(taxform.getStayInPakistanMoreThan3Years());
                    }


                    /*==================PERSONAL INFORMATION*/
                    Taxform_PersonalInformation_Bean taxformPersonalInformationBean = new Taxform_PersonalInformation_Bean();

                    if (taxform.getNameAsPerCnic() != null && !taxform.getNameAsPerCnic().isEmpty()){
                        taxformPersonalInformationBean.setNameAsPerCnic(taxform.getNameAsPerCnic());
                    }

                    if (taxform.getCnic() != null && !taxform.getCnic().isEmpty()){
                        taxformPersonalInformationBean.setCnic(taxform.getCnic());
                    }

                    if (taxform.getDateOfBirth() != null) {
                        taxformPersonalInformationBean.setDateOfBirth(CommonUtil.changeDateToString(taxform.getDateOfBirth()));
                    }

                    if (taxform.getEmail() != null && !taxform.getEmail().isEmpty()) {
                        taxformPersonalInformationBean.setEmail(taxform.getEmail());
                    }

                    if (taxform.getMobileNo() != null && !taxform.getMobileNo().isEmpty()){
                        taxformPersonalInformationBean.setMobileNo(taxform.getMobileNo());
                    }

                    if (taxform.getOccupation() != null && !taxform.getOccupation().isEmpty()) {
                        taxformPersonalInformationBean.setOccupation(taxform.getOccupation());
                    }

                    if (taxform.getResidenceAddress() != null && !taxform.getResidenceAddress().isEmpty()){
                        taxformPersonalInformationBean.setResidenceAddress(taxform.getResidenceAddress());
                    }

                    taxformDetailedBean.setTaxformPersonalInformationBean(taxformPersonalInformationBean);


                    /*==================INCOME TAX -> SALARY */


                    if (taxform.getTaxformIncomeTaxSalary() != null) {

                        taxformDetailedBean.setSalaryCheck(true);

                        Taxform_IncomeTax_Salary taxformIncomeTaxSalary = taxform.getTaxformIncomeTaxSalary();
                        Taxform_IncomeTax_Salary_Bean taxformIncomeTaxSalaryBean = new Taxform_IncomeTax_Salary_Bean();

                        if (taxformIncomeTaxSalary.getBasicSalary() != null) {
                            taxformIncomeTaxSalaryBean.setBasicSalary(taxformIncomeTaxSalary.getBasicSalary() + "");
                        }

                        if (taxformIncomeTaxSalary.getTada() != null) {
                            taxformIncomeTaxSalaryBean.setTadaCheck(true);
                            taxformIncomeTaxSalaryBean.setTada(taxformIncomeTaxSalary.getTada() + "");
                        } else {
                            taxformIncomeTaxSalaryBean.setTadaCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getMedicalAllowance() != null) {
                            taxformIncomeTaxSalaryBean.setMedicalAllowanceCheck(true);
                            taxformIncomeTaxSalaryBean.setMedicalAllowance(taxformIncomeTaxSalary.getMedicalAllowance() + "");
                        } else {
                            taxformIncomeTaxSalaryBean.setMedicalAllowanceCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getProvidentFundByEmployeer() != null) {
                            taxformIncomeTaxSalaryBean.setProvidentFundByEmployeerCheck(true);
                            taxformIncomeTaxSalaryBean.setProvidentFundByEmployeer(taxformIncomeTaxSalary.getProvidentFundByEmployeer() + "");
                        } else {
                            taxformIncomeTaxSalaryBean.setProvidentFundByEmployeerCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getOtherAllowance() != null) {
                            taxformIncomeTaxSalaryBean.setOtherAllowanceCheck(true);
                            taxformIncomeTaxSalaryBean.setOtherAllownace(taxformIncomeTaxSalary.getOtherAllowance() + "");
                        } else {
                            taxformIncomeTaxSalaryBean.setOtherAllowanceCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getCompanyVehicleCost() != null) {
                            taxformIncomeTaxSalaryBean.setCompanyVehcileProvidedCheck(true);
                            taxformIncomeTaxSalaryBean.setCompanyVehicleCost(taxformIncomeTaxSalary.getCompanyVehicleCost() + "" );

                            if (taxformIncomeTaxSalary.getCompanyVehicleReceivedAfterJuly() != null) {
                                taxformIncomeTaxSalaryBean.setCompanyVehicleReceivedAfterJulyCheck(taxformIncomeTaxSalary.getCompanyVehicleReceivedAfterJuly());
                                if (taxformIncomeTaxSalary.getCompanyVehicleReceivedAfterJuly()){
                                    taxformIncomeTaxSalaryBean.setCompanyVehicleReceivedDate(CommonUtil.changeDateToString(taxformIncomeTaxSalary.getCompanyVehicleReceivedDate()));
                                }

                            }
                        } else {
                            taxformIncomeTaxSalaryBean.setCompanyVehcileProvidedCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getProvidentOrGratuityFundReceived() != null) {
                            taxformIncomeTaxSalaryBean.setProvidentOrGratuityFundReceivedCheck(true);
                            taxformIncomeTaxSalaryBean.setProvidentOrGratuityFundReceived(taxformIncomeTaxSalary.getProvidentOrGratuityFundReceived() + "");
                        } else {
                            taxformIncomeTaxSalaryBean.setProvidentOrGratuityFundReceivedCheck(false);
                        }

                        if (taxformIncomeTaxSalary.getSalaryTaxBorneByEmployeerCheck() != null) {
                            taxformIncomeTaxSalaryBean.setSalaryTaxBorneByEmployeerCheck(taxformIncomeTaxSalary.getSalaryTaxBorneByEmployeerCheck());
                        }

                        if (taxformIncomeTaxSalary.getSalaryTaxWithheldByEmployeer() != null){
                            taxformIncomeTaxSalaryBean.setSalaryTaxWithheldByEmployeer(taxformIncomeTaxSalary.getSalaryTaxWithheldByEmployeer() + "");
                        }

                        taxformDetailedBean.setTaxformIncomeTaxSalaryBean(taxformIncomeTaxSalaryBean);

                    } else {
                        taxformDetailedBean.setSalaryCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxSalaryBean(new Taxform_IncomeTax_Salary_Bean());
                    }

                    /*==================INCOME TAX -> PROPERTY*/

                    if (taxform.getTaxformIncomeTaxProperty() != null){
                        Taxform_IncomeTax_Property taxformIncomeTaxProperty = taxform.getTaxformIncomeTaxProperty();
                        Taxform_IncomeTax_Property_Bean taxformIncomeTaxPropertyBean = new Taxform_IncomeTax_Property_Bean();

                        if (taxformIncomeTaxProperty.getRentReceivedFromYourProperty() != null && taxformIncomeTaxProperty.getRentReceivedFromYourProperty() != 0.0){
                            taxformIncomeTaxPropertyBean.setRentReceivedFromYourProperty(taxformIncomeTaxProperty.getRentReceivedFromYourProperty() + "");
                        }

                        if (taxformIncomeTaxProperty.getDoYouDeductAnyTax() != null) {
                            taxformIncomeTaxPropertyBean.setDoYouDeductAnyTax(taxformIncomeTaxProperty.getDoYouDeductAnyTax());
                            if (taxformIncomeTaxProperty.getDoYouDeductAnyTax()) {
                                taxformIncomeTaxPropertyBean.setPropertyTax(taxformIncomeTaxProperty.getPropertyTax() + "");
                            }
                        }

                        taxformDetailedBean.setPropertyCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxPropertyBean(taxformIncomeTaxPropertyBean);

                    } else {
                        taxformDetailedBean.setPropertyCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxPropertyBean(new Taxform_IncomeTax_Property_Bean());
                    }

                    /*==================INCOME TAX -> CAPITAL GAIN -> ON SHARE*/
                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null) {
                        Taxform_IncomeTax_CapitalGain_OnShare taxformIncomeTaxCapitalGainOnShare = taxform.getTaxformIncomeTaxCapitalGainOnShare();
                        Taxform_IncomeTax_CapitalGain_OnShare_Bean taxformIncomeTaxCapitalGainOnShareBean = new Taxform_IncomeTax_CapitalGain_OnShare_Bean();

                        if (taxformIncomeTaxCapitalGainOnShare.getLessThan12MonthsFieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsCheck(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsFieldsCapitalGain(taxformIncomeTaxCapitalGainOnShare.getLessThan12MonthsFieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainOnShare.getLessThan12MonthsTaxDeducted() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsCheck(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsTaxDeducted(taxformIncomeTaxCapitalGainOnShare.getLessThan12MonthsTaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setLessThan12MonthsCheck(false);
                        }



                        if (taxformIncomeTaxCapitalGainOnShare.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsCheck(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(taxformIncomeTaxCapitalGainOnShare.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainOnShare.getMoreThan12ButLessThan24MonthsTaxDeducted() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsCheck(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsTaxDeducted(taxformIncomeTaxCapitalGainOnShare.getMoreThan12ButLessThan24MonthsTaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan12ButLessThan24MonthsCheck(false);
                        }



                        if (taxformIncomeTaxCapitalGainOnShare.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012Check(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(taxformIncomeTaxCapitalGainOnShare.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012Check(false);
                        }

                        if (taxformIncomeTaxCapitalGainOnShare.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012Check(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(taxformIncomeTaxCapitalGainOnShare.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setMoreThan24MonthsButAquiredAfter1July2012Check(false);
                        }

                        if (taxformIncomeTaxCapitalGainOnShare.getAquiredBefore1July2012FieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012Check(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012FieldsCapitalGain(taxformIncomeTaxCapitalGainOnShare.getAquiredBefore1July2012FieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012Check(false);
                        }

                        if (taxformIncomeTaxCapitalGainOnShare.getAquiredBefore1July2012TaxDeducted() != null){
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012Check(true);
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012TaxDeducted(taxformIncomeTaxCapitalGainOnShare.getAquiredBefore1July2012TaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainOnShareBean.setAquiredBefore1July2012Check(false);
                        }

                        /*FOR TAX YEAR 2018*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxformIncomeTaxCapitalGainOnShare.getAcquiredOnOrAfter1JulyCapitalGain() != null){
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyCapitalGain(taxformIncomeTaxCapitalGainOnShare.getAcquiredOnOrAfter1JulyCapitalGain() + "");
                            } else {
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyCheck(false);
                            }

                            if (taxformIncomeTaxCapitalGainOnShare.getAcquiredOnOrAfter1JulyTaxDeducted() != null){
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyTaxDeducted(taxformIncomeTaxCapitalGainOnShare.getAcquiredOnOrAfter1JulyTaxDeducted() + "");
                            } else {
                                taxformIncomeTaxCapitalGainOnShareBean.setAcquiredOnOrAfter1JulyCheck(false);
                            }
                        }

                        taxformDetailedBean.setCapitalGainOnShareCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainOnShareBean(taxformIncomeTaxCapitalGainOnShareBean);
                    } else {
                        taxformDetailedBean.setCapitalGainOnShareCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainOnShareBean(new Taxform_IncomeTax_CapitalGain_OnShare_Bean());
                    }

                    /*==================INCOME TAX -> CAPITAL GAIN -> MUTUAL FUNDS*/
                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null) {
                        Taxform_IncomeTax_CapitalGain_MutualFinds taxformIncomeTaxCapitalGainMutualFinds = taxform.getTaxformIncomeTaxCapitalGainMutualFinds();
                        Taxform_IncomeTax_CapitalGain_MutualFunds_Bean taxformIncomeTaxCapitalGainMutualFundsBean = new Taxform_IncomeTax_CapitalGain_MutualFunds_Bean();

                        if (taxformIncomeTaxCapitalGainMutualFinds.getLessThan12MonthsFieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsCheck(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsFieldsCapitalGain(taxformIncomeTaxCapitalGainMutualFinds.getLessThan12MonthsFieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainMutualFinds.getLessThan12MonthsTaxDeducted() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsCheck(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsTaxDeducted(taxformIncomeTaxCapitalGainMutualFinds.getLessThan12MonthsTaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setLessThan12MonthsCheck(false);
                        }



                        if (taxformIncomeTaxCapitalGainMutualFinds.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsCheck(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsFieldsCapitalGain(taxformIncomeTaxCapitalGainMutualFinds.getMoreThan12ButLessThan24MonthsFieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainMutualFinds.getMoreThan12ButLessThan24MonthsTaxDeducted() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsCheck(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsTaxDeducted(taxformIncomeTaxCapitalGainMutualFinds.getMoreThan12ButLessThan24MonthsTaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan12ButLessThan24MonthsCheck(false);
                        }



                        if (taxformIncomeTaxCapitalGainMutualFinds.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012Check(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain(taxformIncomeTaxCapitalGainMutualFinds.getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012Check(false);
                        }

                        if (taxformIncomeTaxCapitalGainMutualFinds.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012Check(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012TaxDeducted(taxformIncomeTaxCapitalGainMutualFinds.getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setMoreThan24MonthsButAquiredAfter1July2012Check(false);
                        }



                        if (taxformIncomeTaxCapitalGainMutualFinds.getAquiredBefore1July2012FieldsCapitalGain() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012Check(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012FieldsCapitalGain(taxformIncomeTaxCapitalGainMutualFinds.getAquiredBefore1July2012FieldsCapitalGain() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012Check(false);
                        }

                        if (taxformIncomeTaxCapitalGainMutualFinds.getAquiredBefore1July2012TaxDeducted() != null){
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012Check(true);
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012TaxDeducted(taxformIncomeTaxCapitalGainMutualFinds.getAquiredBefore1July2012TaxDeducted() + "");
                        } else {
                            taxformIncomeTaxCapitalGainMutualFundsBean.setAquiredBefore1July2012Check(false);
                        }


                        /*FOR TAX YEAR 2018*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxformIncomeTaxCapitalGainMutualFinds.getAcquiredOnOrAfter1JulyCapitalGain() != null){
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyCapitalGain(taxformIncomeTaxCapitalGainMutualFinds.getAcquiredOnOrAfter1JulyCapitalGain() + "");
                            } else {
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyCheck(false);
                            }

                            if (taxformIncomeTaxCapitalGainMutualFinds.getAcquiredOnOrAfter1JulyTaxDeducted() != null){
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyTaxDeducted(taxformIncomeTaxCapitalGainMutualFinds.getAcquiredOnOrAfter1JulyTaxDeducted() + "");
                            } else {
                                taxformIncomeTaxCapitalGainMutualFundsBean.setAcquiredOnOrAfter1JulyCheck(false);
                            }
                        }

                        taxformDetailedBean.setCapitalGainMutualFundsCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainMutualFundsBean(taxformIncomeTaxCapitalGainMutualFundsBean);
                    } else {
                        taxformDetailedBean.setCapitalGainMutualFundsCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainMutualFundsBean(new Taxform_IncomeTax_CapitalGain_MutualFunds_Bean());
                    }

                    /*==================INCOME TAX -> CAPITAL GAIN -> PROPERTY*/
                    if (taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {
                        Taxform_IncomeTax_CapitalGain_Property taxformIncomeTaxCapitalGainProperty = taxform.getTaxformIncomeTaxCapitalGainProperty();
                        Taxform_IncomeTax_CapitalGain_Property_Bean taxformIncomeTaxCapitalGainPropertyBean = new Taxform_IncomeTax_CapitalGain_Property_Bean();

                        /*BEFORE 1 JULY AND UPTO 3 YEARS*/

                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsPurchaseCost() != null){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsPurchaseCost(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsPurchaseCost() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsSaleCost() != null){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsSaleCost(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsSaleCost() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsLocation() != null && !taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsLocation().isEmpty()){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsLocation(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndUpto3YearsLocation() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndUpto3YearsCheck(false);
                        }

                        /*BEFORE 1 JULY AND MORE THAN 3 YEARS*/
                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsPurchaseCost() != null){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsPurchaseCost(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsPurchaseCost() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsSaleCost() != null){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsSaleCost(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsSaleCost() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(false);
                        }

                        if (taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsLocation() != null && !taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsLocation().isEmpty()){
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(true);
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsLocation(taxformIncomeTaxCapitalGainProperty.getBefore1JulyAndMoreThan3YearsLocation() + "");
                        } else {
                            taxformIncomeTaxCapitalGainPropertyBean.setBefore1JulyAndMoreThan3YearsCheck(false);
                        }

                        /*AFTER 1 JULY*/

                        /*FOR TAX YEAR 2018 - 2019*/
                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {

                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearPurchaseCost() != null){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearPurchaseCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearPurchaseCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearSaleCost() != null){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearSaleCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearSaleCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearLocation() != null && !taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearLocation().isEmpty()){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyUpto1YearLocation(taxformIncomeTaxCapitalGainProperty.getAfter1JulyUpto1YearLocation() + "");
                            }


                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsPurchaseCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsPurchaseCost(taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsPurchaseCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsSaleCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsSaleCost(taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsSaleCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsLocation() != null && !taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsLocation().isEmpty()) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July1To2YearsLocation(taxformIncomeTaxCapitalGainProperty.getAfter1July1To2YearsLocation() + "");
                            }

                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsPurchaseCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsPurchaseCost(taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsPurchaseCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsSaleCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsSaleCost(taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsSaleCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsLocation() != null && !taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsLocation().isEmpty()) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1July2To3YearsLocation(taxformIncomeTaxCapitalGainProperty.getAfter1July2To3YearsLocation() + "");
                            }

                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsPurchaseCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsPurchaseCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsPurchaseCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsSaleCost() != null) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsSaleCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsSaleCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsLocation() != null && !taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsLocation().isEmpty()) {
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyAndMoreThan3YearsLocation(taxformIncomeTaxCapitalGainProperty.getAfter1JulyAndMoreThan3YearsLocation() + "");
                            }

                        } else {
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyPurchaseCost() != null){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyPurchaseCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulyPurchaseCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulySaleCost() != null){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulySaleCost(taxformIncomeTaxCapitalGainProperty.getAfter1JulySaleCost() + "");
                            }
                            if (taxformIncomeTaxCapitalGainProperty.getAfter1JulyLocation() != null && !taxformIncomeTaxCapitalGainProperty.getAfter1JulyLocation().isEmpty()){
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyCheck(true);
                                taxformIncomeTaxCapitalGainPropertyBean.setAfter1JulyLocation(taxformIncomeTaxCapitalGainProperty.getAfter1JulyLocation() + "");
                            }
                        }

                        taxformDetailedBean.setCapitalGainPropertyCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainPropertyBean(taxformIncomeTaxCapitalGainPropertyBean);
                    } else {
                        taxformDetailedBean.setCapitalGainPropertyCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxCapitalGainPropertyBean(new Taxform_IncomeTax_CapitalGain_Property_Bean());
                    }

                    /*==================INCOME TAX -> OTHER SOURCES OF INCOME --> AGRICULTURAL INCOME AND BONUS SHARES AND DIVIDEND*/

                    if (taxform.getTaxformIncomeTaxOtherSources() != null) {
                        Taxform_IncomeTax_OtherSources taxformIncomeTaxOtherSources = taxform.getTaxformIncomeTaxOtherSources();

                        Taxform_IncomeTax_OtherSources_AgriculturalIncome_Bean taxformIncomeTaxOtherSourcesAgriculturalIncomeBean = new Taxform_IncomeTax_OtherSources_AgriculturalIncome_Bean();
                        Taxform_IncomeTax_OtherSources_BonusShares_Bean taxformIncomeTaxOtherSourcesBonusSharesBean = new Taxform_IncomeTax_OtherSources_BonusShares_Bean();
                        Taxform_IncomeTax_OtherSources_Dividends_Bean taxformIncomeTaxOtherSourcesDividendsBean = new Taxform_IncomeTax_OtherSources_Dividends_Bean();
                        Taxform_IncomeTax_OtherSources_OtherInflows_Bean taxformIncomeTaxOtherSourcesOtherInflowsBean = new Taxform_IncomeTax_OtherSources_OtherInflows_Bean();

                        taxformDetailedBean.setBonusSharesAgriculturalIncomeCheck(false);
                        taxformDetailedBean.setBonusSharesCheck(false);
                        taxformDetailedBean.setDividentCheck(false);

                        if (taxformIncomeTaxOtherSources.getAgriculturalIncome() != null && taxformIncomeTaxOtherSources.getAgriculturalIncome().compareTo(0.0) > 0) {
                            taxformDetailedBean.setBonusSharesAgriculturalIncomeCheck(true);
                            taxformIncomeTaxOtherSourcesAgriculturalIncomeBean.setBonusSharesAgriculturalIncome(taxformIncomeTaxOtherSources.getAgriculturalIncome() + "");
                        } else {
                            taxformDetailedBean.setBonusSharesAgriculturalIncomeCheck(false);
                        }

                        if (taxformIncomeTaxOtherSources.getBonusShares() != null && taxformIncomeTaxOtherSources.getBonusShares().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setBonusSharesCheck(true);
                            taxformIncomeTaxOtherSourcesBonusSharesBean.setBonusShares(taxformIncomeTaxOtherSources.getBonusShares() + "");
                        } else {
                            taxformDetailedBean.setBonusSharesCheck(false);
                        }

                        if (taxformIncomeTaxOtherSources.getBonusSharesTaxDeducted() != null && taxformIncomeTaxOtherSources.getBonusSharesTaxDeducted().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setBonusSharesCheck(true);
                            taxformIncomeTaxOtherSourcesBonusSharesBean.setBonusSharesTaxDeducted(taxformIncomeTaxOtherSources.getBonusSharesTaxDeducted() + "");
                        }

                        if (taxformIncomeTaxOtherSources.getDividentByPowerCompanies() != null && taxformIncomeTaxOtherSources.getDividentByPowerCompanies().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByPowerCompanies(taxformIncomeTaxOtherSources.getDividentByPowerCompanies() + "");
                        } else {
                            taxformDetailedBean.setDividentCheck(false);
                        }

                        if (taxformIncomeTaxOtherSources.getDividentByPowerCompaniesTaxDeducted() != null && taxformIncomeTaxOtherSources.getDividentByPowerCompaniesTaxDeducted().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByPowerCompaniesTaxDeducted(taxformIncomeTaxOtherSources.getDividentByPowerCompaniesTaxDeducted() + "");
                        }

                        if (taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFund() != null && taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFund().compareTo(0.0) > 0) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByOtherCompaniesStockFund(taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFund() + "");
                        } else {
                            taxformDetailedBean.setDividentCheck(false);
                        }
                        if (taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFundTaxDeducted() != null && taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFundTaxDeducted().compareTo(0.0) > 0) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByOtherCompaniesStockFundTaxDeducted(taxformIncomeTaxOtherSources.getDividentByOtherCompaniesStockFundTaxDeducted() + "");
                        }

                        if (taxformIncomeTaxOtherSources.getDividentByMutualFunds() != null && taxformIncomeTaxOtherSources.getDividentByMutualFunds().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByMutualFunds(taxformIncomeTaxOtherSources.getDividentByMutualFunds() + "");
                        } else {
                            taxformDetailedBean.setDividentCheck(false);
                        }
                        if (taxformIncomeTaxOtherSources.getDividentByMutualFundsTaxDeducted() != null && taxformIncomeTaxOtherSources.getDividentByMutualFundsTaxDeducted().compareTo(0.0) > 0 ) {
                            taxformDetailedBean.setDividentCheck(true);
                            taxformIncomeTaxOtherSourcesDividendsBean.setDividentByMutualFundsTaxDeducted(taxformIncomeTaxOtherSources.getDividentByMutualFundsTaxDeducted() + "");
                        }

                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesAgriculturalIncomeBean(taxformIncomeTaxOtherSourcesAgriculturalIncomeBean);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesBonusSharesBean(taxformIncomeTaxOtherSourcesBonusSharesBean);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesDividendsBean(taxformIncomeTaxOtherSourcesDividendsBean);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesOtherInflowsBean(taxformIncomeTaxOtherSourcesOtherInflowsBean);
                    } else {
                        taxformDetailedBean.setBonusSharesAgriculturalIncomeCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesAgriculturalIncomeBean(new Taxform_IncomeTax_OtherSources_AgriculturalIncome_Bean());
                        taxformDetailedBean.setBonusSharesCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesBonusSharesBean(new Taxform_IncomeTax_OtherSources_BonusShares_Bean());
                        taxformDetailedBean.setDividentCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesDividendsBean(new Taxform_IncomeTax_OtherSources_Dividends_Bean());
                    }

                    /*==================INCOME TAX -> OTHER SOURCES OF INCOME -> PROFIT ON BANK DEPOSIT --- MULTIPLE VALUES*/

                    if (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null && taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList().size() > 0) {
                        List<Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit> taxformIncomeTaxOtherSourcesProfitOnBankDeposits = taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList();
                        List<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean> taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans = new ArrayList<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean>();

                        for (Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit : taxformIncomeTaxOtherSourcesProfitOnBankDeposits){
                            TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean taxformIncomeTaxOtherSourcesProfitOnBankDepositBean = new TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean();

                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getId() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setId(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getId());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountType() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setAccountType(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountType());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBankName() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setBankName(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBankName());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBranch() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setBranch(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBranch());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountNo() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setAccountNo(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountNo());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getCurrency() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setCurrency(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getCurrency());
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setProfitAmount(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount() + "");
                            }
                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getTaxDeducted() != null) {
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setTaxDeducted(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getTaxDeducted() + "");
                            }

                            taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans.add(taxformIncomeTaxOtherSourcesProfitOnBankDepositBean);

                        }

                        taxformDetailedBean.setBankDepositCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesProfitOnBankDepositBeans(taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans);
                    } else {
                        taxformDetailedBean.setBankDepositCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesProfitOnBankDepositBeans(new ArrayList<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean>());
                    }
                     /*==================INCOME TAX -> OTHER SOURCES OF INCOME -> OthersInflows --- MULTIPLE VALUES*/


                    if (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null && taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList().size() > 0) {
                        List<TaxForm_IncomeTax_OtherSources_OtherInflow> taxformIncomeTaxOtherSourcesOtherInflow = taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList();
                        List<TaxformIncomeTaxOtherSourcesOtherInFlowBean> taxformIncomeTaxOtherSourcesOtherInFlowBeans = new ArrayList<TaxformIncomeTaxOtherSourcesOtherInFlowBean>();

                        for (TaxForm_IncomeTax_OtherSources_OtherInflow taxForm_incomeTax_otherSources_otherInflow : taxformIncomeTaxOtherSourcesOtherInflow){
                           TaxformIncomeTaxOtherSourcesOtherInFlowBean taxformIncomeTaxOtherSourcesOtherInFlowBean = new TaxformIncomeTaxOtherSourcesOtherInFlowBean();

                            if (taxForm_incomeTax_otherSources_otherInflow.getId() != null) {
                                taxformIncomeTaxOtherSourcesOtherInFlowBean.setId(taxForm_incomeTax_otherSources_otherInflow.getId());
                            }
                            if (taxForm_incomeTax_otherSources_otherInflow.getType()!= null) {
                                taxformIncomeTaxOtherSourcesOtherInFlowBean.setType(taxForm_incomeTax_otherSources_otherInflow.getType());
                            }
                            if (taxForm_incomeTax_otherSources_otherInflow.getAmount() != null) {
                                taxformIncomeTaxOtherSourcesOtherInFlowBean.setAmount(taxForm_incomeTax_otherSources_otherInflow.getAmount() + "");
                            }
                            if (taxForm_incomeTax_otherSources_otherInflow.getMemoDescription() != null) {
                                taxformIncomeTaxOtherSourcesOtherInFlowBean.setMemoDescription(taxForm_incomeTax_otherSources_otherInflow.getMemoDescription());
                            }
                            taxformIncomeTaxOtherSourcesOtherInFlowBeans.add(taxformIncomeTaxOtherSourcesOtherInFlowBean);

                        }
                        taxformDetailedBean.setOtherInflowsCheck(true);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesOtherInFlowBeanList(taxformIncomeTaxOtherSourcesOtherInFlowBeans);
                    } else {
                        taxformDetailedBean.setOtherInflowsCheck(false);
                        taxformDetailedBean.setTaxformIncomeTaxOtherSourcesOtherInFlowBeanList(new ArrayList<>());
                    }

                    /*==================DEDUCTIBLE ALLOWANCE AND TAX CREDIT*/

                    if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                        Taxform_DeductibleAllowanceOrCredit taxformDeductibleAllowanceOrCredit = taxform.getTaxformDeductibleAllowanceOrCredit();
                        Taxform_DeductibleAllowanceOrCredit_Bean taxformDeductibleAllowanceOrCreditBean = new Taxform_DeductibleAllowanceOrCredit_Bean();

                        taxformDeductibleAllowanceOrCreditBean.setDonationsToCharityCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setInvestmentInSharesMutualFundsAndLifeInsuranceCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setInvestmentInApprovedPensionFundCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setInterestOrRateOnHouseHoldsCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setHelthInsurancePremiumCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceTutionFeesCheck(false);
                        taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceNoOfChildrensCheck(false);

                        if ((taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check() != null && taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check()) ||
                                (taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check() != null && taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check())   ) {

                            taxformDeductibleAllowanceOrCreditBean.setDonationsToCharityCheck(true);

                            if (taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check() != null &&
                                    taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61Check() &&
                                    taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61() != null) {

                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderSection61Check(true);
                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderSection61(taxformDeductibleAllowanceOrCredit.getDonationsUnderSection61() + "");
                            } else {
                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderSection61Check(false);
                            }

                            if (taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check() != null &&
                                    taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61Check() &&
                                    taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61() != null &&
                                    taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61ApprovedDonee() != null) {

                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61Check(true);
                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61(taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61() + "");
                                if (taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61ApprovedDonee() != null) {
                                    taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61ApprovedDoneeId(taxformDeductibleAllowanceOrCredit.getDonationsUnderClause61ApprovedDonee().getId());
                                } else {
                                    taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61ApprovedDoneeId(null);
                                }
                            } else {
                                taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61Check(false);
                            }

                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setDonationsToCharityCheck(false);
                            taxformDeductibleAllowanceOrCreditBean.setDonationsUnderSection61Check(false);
                            taxformDeductibleAllowanceOrCreditBean.setDonationsUnderClause61Check(false);
                        }

                        if (taxformDeductibleAllowanceOrCredit.getInvestmentInSharesMutualFundsAndLifeInsurance() != null) {
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInSharesMutualFundsAndLifeInsuranceCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInSharesMutualFundsAndLifeInsurance(taxformDeductibleAllowanceOrCredit.getInvestmentInSharesMutualFundsAndLifeInsurance() + "");
                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInSharesMutualFundsAndLifeInsuranceCheck(false);
                        }

                        if (taxformDeductibleAllowanceOrCredit.getInvestmentInApprovedPensionFund() != null) {
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInApprovedPensionFundCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInApprovedPensionFund(taxformDeductibleAllowanceOrCredit.getInvestmentInApprovedPensionFund() + "");
                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setInvestmentInApprovedPensionFundCheck(false);
                        }

                        if (taxformDeductibleAllowanceOrCredit.getInterestOrRateOnHouseHolds() != null) {
                            taxformDeductibleAllowanceOrCreditBean.setInterestOrRateOnHouseHoldsCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setInterestOrRateOnHouseHolds(taxformDeductibleAllowanceOrCredit.getInterestOrRateOnHouseHolds() + "");
                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setInterestOrRateOnHouseHoldsCheck(false);
                        }


                        if (taxformDeductibleAllowanceOrCredit.getHelthInsurancePremium() != null) {
                            taxformDeductibleAllowanceOrCreditBean.setHelthInsurancePremiumCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setHelthInsurancePremium(taxformDeductibleAllowanceOrCredit.getHelthInsurancePremium() + "");
                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setHelthInsurancePremiumCheck(false);
                        }

                        if (taxformDeductibleAllowanceOrCredit.getEducationAllowanceTutionFees() != null || taxformDeductibleAllowanceOrCredit.getEducationAllowanceNoOfChildrens() != null) {
                            taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceTutionFeesCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceNoOfChildrensCheck(true);
                            taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceTutionFees(taxformDeductibleAllowanceOrCredit.getEducationAllowanceTutionFees() + "");
                            if (taxformDeductibleAllowanceOrCredit.getEducationAllowanceNoOfChildrens() != null) {
                                taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceNoOfChildrens(taxformDeductibleAllowanceOrCredit.getEducationAllowanceNoOfChildrens() + "");
                            }
                        } else {
                            taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceTutionFeesCheck(false);
                            taxformDeductibleAllowanceOrCreditBean.setEducationAllowanceNoOfChildrensCheck(false);
                        }


                        taxformDetailedBean.setTaxformDeductibleAllowanceOrCreditBean(taxformDeductibleAllowanceOrCreditBean);
                    }

                    /*==================TAX DEDUCTED OR COLLECTED -> BANKING TRANSACTION --- MULTIPLE VALUES*/
                    if (taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null && taxform.getTaxformTaxDeductedCollectedBankingTransactionList().size() > 0) {
                        List<Taxform_TaxDeductedCollected_BankingTransaction> taxformTaxDeductedCollectedBankingTransactions = taxform.getTaxformTaxDeductedCollectedBankingTransactionList();
                        List<TaxformTaxDeductedCollectedBankingTransactionBean> taxformTaxDeductedCollectedBankingTransactionBeans = new ArrayList<TaxformTaxDeductedCollectedBankingTransactionBean>();

                        for (Taxform_TaxDeductedCollected_BankingTransaction taxformTaxDeductedCollectedBankingTransaction : taxformTaxDeductedCollectedBankingTransactions){
                            TaxformTaxDeductedCollectedBankingTransactionBean taxformTaxDeductedCollectedBankingTransactionBean= new TaxformTaxDeductedCollectedBankingTransactionBean();

                            if (taxformTaxDeductedCollectedBankingTransaction.getId() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setId(taxformTaxDeductedCollectedBankingTransaction.getId());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getTransactionType() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setTransactionType(taxformTaxDeductedCollectedBankingTransaction.getTransactionType());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getAccountType() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountType(taxformTaxDeductedCollectedBankingTransaction.getAccountType());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getBankName() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setBankName(taxformTaxDeductedCollectedBankingTransaction.getBankName());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getBranch() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setBranch(taxformTaxDeductedCollectedBankingTransaction.getBranch());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getAccountNo() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountNo(taxformTaxDeductedCollectedBankingTransaction.getAccountNo());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getCurrency() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setCurrency(taxformTaxDeductedCollectedBankingTransaction.getCurrency());
                            }
                            if (taxformTaxDeductedCollectedBankingTransaction.getTaxDeductedAmount() != null) {
                                taxformTaxDeductedCollectedBankingTransactionBean.setTaxDeductedAmount(taxformTaxDeductedCollectedBankingTransaction.getTaxDeductedAmount() + "");
                            }

                            taxformTaxDeductedCollectedBankingTransactionBeans.add(taxformTaxDeductedCollectedBankingTransactionBean);

                        }

                        taxformDetailedBean.setBankingTransactionCheck(true);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedBankingTransactionBeans(taxformTaxDeductedCollectedBankingTransactionBeans);
                    } else {
                        taxformDetailedBean.setBankingTransactionCheck(false);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedBankingTransactionBeans(new ArrayList<TaxformTaxDeductedCollectedBankingTransactionBean>());
                    }


                    /*==================TAX DEDUCTED OR COLLECTED -> WITHHOLD TAX VEHICLE --- MULTIPLE VALUES*/
                    if (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null && taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList().size() > 0) {
                        List<Taxform_TaxDeductedCollected_WithholdTaxVehicle> taxformTaxDeductedCollectedWithholdTaxVehicles = taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList();
                        List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> taxformTaxDeductedCollectedWithholdVehicleTaxBeans = new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>();

                        for (Taxform_TaxDeductedCollected_WithholdTaxVehicle taxformTaxDeductedCollectedWithholdTaxVehicle : taxformTaxDeductedCollectedWithholdTaxVehicles){
                            TaxformTaxDeductedCollectedWithholdVehicleTaxBean taxformTaxDeductedCollectedWithholdVehicleTaxBean= new TaxformTaxDeductedCollectedWithholdVehicleTaxBean();

                            if (taxformTaxDeductedCollectedWithholdTaxVehicle.getId() != null) {
                                taxformTaxDeductedCollectedWithholdVehicleTaxBean.setId(taxformTaxDeductedCollectedWithholdTaxVehicle.getId());
                            }
                            if (taxformTaxDeductedCollectedWithholdTaxVehicle.getType() != null) {
                                taxformTaxDeductedCollectedWithholdVehicleTaxBean.setType(taxformTaxDeductedCollectedWithholdTaxVehicle.getType());
                            }
                            if (taxformTaxDeductedCollectedWithholdTaxVehicle.getVehicleType() != null) {
                                taxformTaxDeductedCollectedWithholdVehicleTaxBean.setVehicleType(taxformTaxDeductedCollectedWithholdTaxVehicle.getVehicleType());
                            }
                            if (taxformTaxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo() != null) {
                                taxformTaxDeductedCollectedWithholdVehicleTaxBean.setVehicleRegistrationNo(taxformTaxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo());
                            }
                            if (taxformTaxDeductedCollectedWithholdTaxVehicle.getTaxDeducted() != null) {
                                taxformTaxDeductedCollectedWithholdVehicleTaxBean.setTaxDeducted(taxformTaxDeductedCollectedWithholdTaxVehicle.getTaxDeducted() + "");
                            }

                            taxformTaxDeductedCollectedWithholdVehicleTaxBeans.add(taxformTaxDeductedCollectedWithholdVehicleTaxBean);

                        }

                        taxformDetailedBean.setWithholdTaxVehicleCheck(true);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedWithholdVehicleTaxBeans(taxformTaxDeductedCollectedWithholdVehicleTaxBeans);
                    } else {
                        taxformDetailedBean.setWithholdTaxVehicleCheck(false);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedWithholdVehicleTaxBeans(new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>());
                    }

                    /*==================TAX DEDUCTED OR COLLECTED -> UTILITIES --- MULTIPLE VALUES*/

                    if (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null && taxform.getTaxformTaxDeductedCollectedUtilitiesList().size() > 0) {
                        List<Taxform_TaxDeductedCollected_Utilities> taxformTaxDeductedCollectedUtilitiesList= taxform.getTaxformTaxDeductedCollectedUtilitiesList();
                        List<TaxformTaxDeductedCollectedUtilitiesBean> taxformTaxDeductedCollectedUtilitiesBeans = new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>();

                        for (Taxform_TaxDeductedCollected_Utilities taxformTaxDeductedCollectedUtilities : taxformTaxDeductedCollectedUtilitiesList){
                            TaxformTaxDeductedCollectedUtilitiesBean taxformTaxDeductedCollectedUtilitiesBean= new TaxformTaxDeductedCollectedUtilitiesBean();

                            if (taxformTaxDeductedCollectedUtilities.getId() != null) {
                                taxformTaxDeductedCollectedUtilitiesBean.setId(taxformTaxDeductedCollectedUtilities.getId());
                            }
                            if (taxformTaxDeductedCollectedUtilities.getUtilityType() != null) {
                                taxformTaxDeductedCollectedUtilitiesBean.setUtilityType(taxformTaxDeductedCollectedUtilities.getUtilityType());
                            }
                            if (taxformTaxDeductedCollectedUtilities.getProvider() != null) {
                                taxformTaxDeductedCollectedUtilitiesBean.setProvider(taxformTaxDeductedCollectedUtilities.getProvider());
                            }
                            if (taxformTaxDeductedCollectedUtilities.getReferenceOrConsumerNo() != null) {
                                taxformTaxDeductedCollectedUtilitiesBean.setReferenceOrConsumerNo(taxformTaxDeductedCollectedUtilities.getReferenceOrConsumerNo());
                            }
                            if (taxformTaxDeductedCollectedUtilities.getTaxDeducted() != null) {
                                taxformTaxDeductedCollectedUtilitiesBean.setTaxDeducted(taxformTaxDeductedCollectedUtilities.getTaxDeducted() + "");
                            }

                            taxformTaxDeductedCollectedUtilitiesBeans.add(taxformTaxDeductedCollectedUtilitiesBean);

                        }

                        taxformDetailedBean.setUtilitiesCheck(true);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedUtilitiesBeans(taxformTaxDeductedCollectedUtilitiesBeans);
                    } else {
                        taxformDetailedBean.setUtilitiesCheck(false);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedUtilitiesBeans(new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>());
                    }

                    /*==================TAX DEDUCTED OR COLLECTED -> OTHER FIELDS*/

                    if (taxform.getTaxformTaxDeductedCollectedOther() != null) {
                        Taxform_TaxDeductedCollected_Other taxformTaxDeductedCollectedOther= taxform.getTaxformTaxDeductedCollectedOther();
                        Taxform_TaxDeductedCollected_OtherFields_Bean taxformTaxDeductedCollectedOtherFieldsBean = new Taxform_TaxDeductedCollected_OtherFields_Bean();
/*
                        taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseOrSaleTaxDeductedCheck(false);*/

                        taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseTaxDeductedCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setPropertySaleTaxDeductedCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setEducationFeeTaxDeductedCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setAirTicketInYearTaxDeductedCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFundCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFundTaxDeductedCheck(false);
                        taxformTaxDeductedCollectedOtherFieldsBean.setTaxDeductedCollectedFunctionsAndGatheringsCheck(false);
/*
                        if (taxformTaxDeductedCollectedOther.getPropertyPurchaseOrSaleTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseOrSaleTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseOrSaleTaxDeducted(taxformTaxDeductedCollectedOther.getPropertyPurchaseOrSaleTaxDeducted() + "");
                        }*/


                        if (taxformTaxDeductedCollectedOther.getPropertyPurchaseTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertyPurchaseTaxDeducted(taxformTaxDeductedCollectedOther.getPropertyPurchaseTaxDeducted() + "");
                        }

                        if (taxformTaxDeductedCollectedOther.getPropertySaleTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertySaleTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setPropertySaleTaxDeducted(taxformTaxDeductedCollectedOther.getPropertySaleTaxDeducted() + "");
                        }

                        if (taxformTaxDeductedCollectedOther.getEducationFeeTaxDeductedTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setEducationFeeTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setEducationFeeTaxDeducted(taxformTaxDeductedCollectedOther.getEducationFeeTaxDeductedTaxDeducted() + "");
                        }
                        if (taxformTaxDeductedCollectedOther.getAirTicketInYearTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setAirTicketInYearTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setAirTicketInYearTaxDeducted(taxformTaxDeductedCollectedOther.getAirTicketInYearTaxDeducted() + "");
                        }
                        if (taxformTaxDeductedCollectedOther.getWithdrawalFromPensionFund() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFundCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFund(taxformTaxDeductedCollectedOther.getWithdrawalFromPensionFund() + "");
                        }
                        if (taxformTaxDeductedCollectedOther.getWithdrawalFromPensionFundTaxDeducted() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFundTaxDeductedCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setWithdrawalFromPensionFundTaxDeducted(taxformTaxDeductedCollectedOther.getWithdrawalFromPensionFundTaxDeducted() + "");
                        }
                        if (taxformTaxDeductedCollectedOther.getFunctionsAndGatherings() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setTaxDeductedCollectedFunctionsAndGatheringsCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setTaxDeductedCollectedFunctionsAndGatherings(taxformTaxDeductedCollectedOther.getFunctionsAndGatherings() + "");
                        }
                        if (taxformTaxDeductedCollectedOther.getTaxRefundOfPriorYear() != null) {
                            taxformTaxDeductedCollectedOtherFieldsBean.setTaxRefundOfPriorYearCheck(true);
                            taxformTaxDeductedCollectedOtherFieldsBean.setTaxRefundOfPriorYear(taxformTaxDeductedCollectedOther.getTaxRefundOfPriorYear() + "");
                        } else {
                            taxformTaxDeductedCollectedOtherFieldsBean.setTaxRefundOfPriorYearCheck(false);
                        }

                        taxformDetailedBean.setTaxDeductedCollectedOtherFieldsCheck(true);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedOtherFieldsBean(taxformTaxDeductedCollectedOtherFieldsBean);
                    } else {
                        taxformDetailedBean.setTaxDeductedCollectedOtherFieldsCheck(false);
                        taxformDetailedBean.setTaxformTaxDeductedCollectedOtherFieldsBean(new Taxform_TaxDeductedCollected_OtherFields_Bean());
                    }

                    /*==================WELTH STATEMENT -> Openning Wealth */

                    if (taxform.getTaxformWelthStatement() != null) {
                        Taxform_WelthStatement welthStatement = taxform.getTaxformWelthStatement();

                        if (welthStatement.getOpeningWealth() != null) {
                            taxformDetailedBean.setOpeningWealth(welthStatement.getOpeningWealth() + "");
                        }
                    }
                    /*==================WELTH STATEMENT -> Openning Wealth Suggestion from Previous Year */

                    Taxform lastTaxform = taxformServices.findOneByTaxformYearAndUser(taxformYearsServices.findOnePreviousYear(taxform.getTaxformYear()), taxform.getUser());
                    if (lastTaxform != null) {
                        taxformDetailedBean.setOpeningWealthSuggestion(TaxformCalculator.taxformCalculator(lastTaxform).getWealthStatementClosingWealth());
                    }

                    /*==================WELTH STATEMENT -> PROPERTY DETAIL */
                    if (taxform.getTaxformWelthStatementPropertyDetailList() != null && taxform.getTaxformWelthStatementPropertyDetailList().size() > 0) {
                        List<Taxform_WelthStatement_PropertyDetail> taxformWelthStatementPropertyDetails = taxform.getTaxformWelthStatementPropertyDetailList();
                        List<TaxformWealthStatementPropertyDetailsBean> taxformWealthStatementPropertyDetailsBeans = new ArrayList<TaxformWealthStatementPropertyDetailsBean>();

                        for (Taxform_WelthStatement_PropertyDetail taxformWelthStatementPropertyDetail : taxformWelthStatementPropertyDetails){
                            TaxformWealthStatementPropertyDetailsBean taxformWealthStatementPropertyDetailsBean = new TaxformWealthStatementPropertyDetailsBean();

                            if (taxformWelthStatementPropertyDetail.getId() != null) {
                                taxformWealthStatementPropertyDetailsBean.setId(taxformWelthStatementPropertyDetail.getId());
                            }

                            if (taxformWelthStatementPropertyDetail.getPropertyType() != null) {
                                taxformWealthStatementPropertyDetailsBean.setPropertyType(taxformWelthStatementPropertyDetail.getPropertyType());
                            }

                            if (taxformWelthStatementPropertyDetail.getUnitNo() != null) {
                                taxformWealthStatementPropertyDetailsBean.setUnitNo(taxformWelthStatementPropertyDetail.getUnitNo());
                            }
                            if (taxformWelthStatementPropertyDetail.getAreaLocalityRoad() != null) {
                                taxformWealthStatementPropertyDetailsBean.setAreaLocalityRoad(taxformWelthStatementPropertyDetail.getAreaLocalityRoad());
                            }
                            if (taxformWelthStatementPropertyDetail.getCity() != null) {
                                taxformWealthStatementPropertyDetailsBean.setCity(taxformWelthStatementPropertyDetail.getCity());
                            }
                            if (taxformWelthStatementPropertyDetail.getArea() != null) {
                                taxformWealthStatementPropertyDetailsBean.setArea(taxformWelthStatementPropertyDetail.getArea());
                            }

                            if (taxformWelthStatementPropertyDetail.getPropertyCost() != null) {
                                taxformWealthStatementPropertyDetailsBean.setPropertyCost(taxformWelthStatementPropertyDetail.getPropertyCost() + "");
                            }

                            taxformWealthStatementPropertyDetailsBeans.add(taxformWealthStatementPropertyDetailsBean);

                        }

                        taxformDetailedBean.setWealthStatementPropertyCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementPropertyDetailsBeans(taxformWealthStatementPropertyDetailsBeans);
                    } else {
                        taxformDetailedBean.setWealthStatementPropertyCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementPropertyDetailsBeans(new ArrayList<>());
                    }

                    /*==================WELTH STATEMENT -> BANK ACCOUNTS OR INVESTMENTS */
                    if (taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList() != null && taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList().size() > 0) {
                        List<Taxform_WelthStatement_BankAccountsOrInvestments> taxformWelthStatementBankAccountsOrInvestmentsList = taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList();
                        List<TaxformWealthStatementBankAccountsOrInvestmentBean> taxformWealthStatementBankAccountsOrInvestmentBeans = new ArrayList<>();

                        for (Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementBankAccountsOrInvestments : taxformWelthStatementBankAccountsOrInvestmentsList){
                            TaxformWealthStatementBankAccountsOrInvestmentBean taxformWealthStatementBankAccountsOrInvestmentBean = new TaxformWealthStatementBankAccountsOrInvestmentBean();

                            if (taxformWelthStatementBankAccountsOrInvestments.getId() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setId(taxformWelthStatementBankAccountsOrInvestments.getId());
                            }
                            if (taxformWelthStatementBankAccountsOrInvestments.getForm() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setForm(taxformWelthStatementBankAccountsOrInvestments.getForm());
                            }
                            if (taxformWelthStatementBankAccountsOrInvestments.getAccountOrInstructionNo() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setAccountOrInstructionNo(taxformWelthStatementBankAccountsOrInvestments.getAccountOrInstructionNo());
                            }
                            if (taxformWelthStatementBankAccountsOrInvestments.getInstitutionNameOrInduvidualCnic() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setInstitutionNameOrInduvidualCnic(taxformWelthStatementBankAccountsOrInvestments.getInstitutionNameOrInduvidualCnic());
                            }
                            if (taxformWelthStatementBankAccountsOrInvestments.getBranchName() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setBranchName(taxformWelthStatementBankAccountsOrInvestments.getBranchName());
                            }
                            if (taxformWelthStatementBankAccountsOrInvestments.getCost() != null) {
                                taxformWealthStatementBankAccountsOrInvestmentBean.setCost(taxformWelthStatementBankAccountsOrInvestments.getCost() + "");
                            }

                            taxformWealthStatementBankAccountsOrInvestmentBeans.add(taxformWealthStatementBankAccountsOrInvestmentBean);

                        }

                        taxformDetailedBean.setBankAccountOrInvestmentCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementBankAccountsOrInvestmentBeans(taxformWealthStatementBankAccountsOrInvestmentBeans);
                    } else {
                        taxformDetailedBean.setBankAccountOrInvestmentCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementBankAccountsOrInvestmentBeans(new ArrayList<TaxformWealthStatementBankAccountsOrInvestmentBean>());
                    }


                    /*==================WELTH STATEMENT -> OTHER RECEIVABLES OR ASSETS*/
                    if (taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList() != null && taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList().size() > 0) {
                        List<Taxform_WelthStatement_OtherReceivablesOrAssets> taxformWelthStatementOtherReceivablesOrAssetsList = taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList();
                        List<TaxformWealthStatementOtherReceivablesOrAssetsBean> taxformWealthStatementOtherReceivablesOrAssetsBeans = new ArrayList<TaxformWealthStatementOtherReceivablesOrAssetsBean>();

                        for (Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementOtherReceivablesOrAssets : taxformWelthStatementOtherReceivablesOrAssetsList){
                            TaxformWealthStatementOtherReceivablesOrAssetsBean taxformWealthStatementOtherReceivablesOrAssetsBean = new TaxformWealthStatementOtherReceivablesOrAssetsBean();

                            if (taxformWelthStatementOtherReceivablesOrAssets.getId() != null) {
                                taxformWealthStatementOtherReceivablesOrAssetsBean.setId(taxformWelthStatementOtherReceivablesOrAssets.getId());
                            }
                            if (taxformWelthStatementOtherReceivablesOrAssets.getForm() != null) {
                                taxformWealthStatementOtherReceivablesOrAssetsBean.setForm(taxformWelthStatementOtherReceivablesOrAssets.getForm());
                            }
                            if (taxformWelthStatementOtherReceivablesOrAssets.getInstitutionNameOrIndividualCnic() != null) {
                                taxformWealthStatementOtherReceivablesOrAssetsBean.setInstitutionNameOrIndividualCnic(taxformWelthStatementOtherReceivablesOrAssets.getInstitutionNameOrIndividualCnic());
                            }
                            if (taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() != null) {
                                taxformWealthStatementOtherReceivablesOrAssetsBean.setValueAtCost(taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() + "");
                            }

                            taxformWealthStatementOtherReceivablesOrAssetsBeans.add(taxformWealthStatementOtherReceivablesOrAssetsBean);
                        }
                        taxformDetailedBean.setOtherReceivableAssets(true);
                        taxformDetailedBean.setTaxformWealthStatementOtherReceivablesOrAssetsBeans(taxformWealthStatementOtherReceivablesOrAssetsBeans);
                    } else {
                        taxformDetailedBean.setOtherReceivableAssets(false);
                        taxformDetailedBean.setTaxformWealthStatementOtherReceivablesOrAssetsBeans(new ArrayList<TaxformWealthStatementOtherReceivablesOrAssetsBean>());
                    }

                    /*==================WELTH STATEMENT -> OWN VEHICLE */
                    if (taxform.getTaxformWelthStatementOwnVehicleList() != null && taxform.getTaxformWelthStatementOwnVehicleList().size() > 0) {
                        List<Taxform_WelthStatement_OwnVehicle> taxformWelthStatementOwnVehicles = taxform.getTaxformWelthStatementOwnVehicleList();
                        List<TaxformWealthStatementOwnVehicleBean> taxformWealthStatementOwnVehicleBeans = new ArrayList<TaxformWealthStatementOwnVehicleBean>();

                        for (Taxform_WelthStatement_OwnVehicle taxformWelthStatementOwnVehicle : taxformWelthStatementOwnVehicles){
                            TaxformWealthStatementOwnVehicleBean taxformWealthStatementOwnVehicleBean = new TaxformWealthStatementOwnVehicleBean();

                            if (taxformWelthStatementOwnVehicle.getId() != null) {
                                taxformWealthStatementOwnVehicleBean.setId(taxformWelthStatementOwnVehicle.getId());
                            }
                            if (taxformWelthStatementOwnVehicle.getForm() != null) {
                                taxformWealthStatementOwnVehicleBean.setForm(taxformWelthStatementOwnVehicle.getForm());
                            }
                            if (taxformWelthStatementOwnVehicle.getEtdRegistrationNo() != null) {
                                taxformWealthStatementOwnVehicleBean.setEtdRegistrationNo(taxformWelthStatementOwnVehicle.getEtdRegistrationNo());
                            }
                            if (taxformWelthStatementOwnVehicle.getMaker() != null) {
                                taxformWealthStatementOwnVehicleBean.setMaker(taxformWelthStatementOwnVehicle.getMaker());
                            }
                            if (taxformWelthStatementOwnVehicle.getCapacity() != null) {
                                taxformWealthStatementOwnVehicleBean.setCapacity(taxformWelthStatementOwnVehicle.getCapacity());
                            }
                            if (taxformWelthStatementOwnVehicle.getValueAtCost() != null) {
                                taxformWealthStatementOwnVehicleBean.setValueAtCost(taxformWelthStatementOwnVehicle.getValueAtCost() + "");
                            }

                            taxformWealthStatementOwnVehicleBeans.add(taxformWealthStatementOwnVehicleBean);
                        }
                        taxformDetailedBean.setOwnVehicleCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementOwnVehicleBeans(taxformWealthStatementOwnVehicleBeans);
                    } else {
                        taxformDetailedBean.setOwnVehicleCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementOwnVehicleBeans(new ArrayList<TaxformWealthStatementOwnVehicleBean>());
                    }

                    /*==================WELTH STATEMENT -> OTHER POSSESSIONS */

                    if (taxform.getTaxformWelthStatementOtherPossessionsList() != null && taxform.getTaxformWelthStatementOtherPossessionsList().size() > 0) {
                        List<Taxform_WelthStatement_OtherPossessions> taxformWelthStatementOtherPossessionsList = taxform.getTaxformWelthStatementOtherPossessionsList();
                        List<TaxformWealthStatementOtherPossessionsBean> taxformWealthStatementOtherPossessionsBeans = new ArrayList<TaxformWealthStatementOtherPossessionsBean>();

                        for (Taxform_WelthStatement_OtherPossessions taxformWelthStatementOtherPossessions : taxformWelthStatementOtherPossessionsList){
                            TaxformWealthStatementOtherPossessionsBean taxformWealthStatementOtherPossessionsBean = new TaxformWealthStatementOtherPossessionsBean();

                            if (taxformWelthStatementOtherPossessions.getId() != null) {
                                taxformWealthStatementOtherPossessionsBean.setId(taxformWelthStatementOtherPossessions.getId());
                            }
                            if (taxformWelthStatementOtherPossessions.getDescription() != null) {
                                taxformWealthStatementOtherPossessionsBean.setDescription(taxformWelthStatementOtherPossessions.getDescription());
                            }
                            if (taxformWelthStatementOtherPossessions.getPossessionType() != null) {
                                taxformWealthStatementOtherPossessionsBean.setPossessionType(taxformWelthStatementOtherPossessions.getPossessionType());
                            }
                            if (taxformWelthStatementOtherPossessions.getValueAtCost() != null) {
                                taxformWealthStatementOtherPossessionsBean.setValueAtCost(taxformWelthStatementOtherPossessions.getValueAtCost() + "");
                            }

                            taxformWealthStatementOtherPossessionsBeans.add(taxformWealthStatementOtherPossessionsBean);
                        }
                        taxformDetailedBean.setOtherPossessionsCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementOtherPossessionsBeans(taxformWealthStatementOtherPossessionsBeans);
                    } else {
                        taxformDetailedBean.setOtherPossessionsCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementOtherPossessionsBeans(new ArrayList<TaxformWealthStatementOtherPossessionsBean>());
                    }

                    /*==================WELTH STATEMENT -> ASSETS OUTSIDE PAKISTAN */

                    if (taxform.getTaxformWelthStatementAssetsOutSidePakistanList() != null && taxform.getTaxformWelthStatementAssetsOutSidePakistanList().size() > 0) {
                        List<Taxform_WelthStatement_AssetsOutSidePakistan> taxformWelthStatementAssetsOutSidePakistans = taxform.getTaxformWelthStatementAssetsOutSidePakistanList();
                        List<TaxformWealthStatementAssetsOutsidePakistanBean> taxformWealthStatementAssetsOutsidePakistanBeans = new ArrayList<TaxformWealthStatementAssetsOutsidePakistanBean>();

                        for (Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementAssetsOutSidePakistan : taxformWelthStatementAssetsOutSidePakistans){
                            TaxformWealthStatementAssetsOutsidePakistanBean taxformWealthStatementAssetsOutsidePakistanBean = new TaxformWealthStatementAssetsOutsidePakistanBean();

                            if (taxformWelthStatementAssetsOutSidePakistan.getId() != null) {
                                taxformWealthStatementAssetsOutsidePakistanBean.setId(taxformWelthStatementAssetsOutSidePakistan.getId());
                            }
                            if (taxformWelthStatementAssetsOutSidePakistan.getDescription() != null) {
                                taxformWealthStatementAssetsOutsidePakistanBean.setDescription(taxformWelthStatementAssetsOutSidePakistan.getDescription());
                            }
                            if (taxformWelthStatementAssetsOutSidePakistan.getValueAtCost() != null) {
                                taxformWealthStatementAssetsOutsidePakistanBean.setValueAtCost(taxformWelthStatementAssetsOutSidePakistan.getValueAtCost() + "");
                            }

                            taxformWealthStatementAssetsOutsidePakistanBeans.add(taxformWealthStatementAssetsOutsidePakistanBean);
                        }
                        taxformDetailedBean.setAssetsOutsidePakistanCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementAssetsOutsidePakistanBeans(taxformWealthStatementAssetsOutsidePakistanBeans);
                    } else {
                        taxformDetailedBean.setAssetsOutsidePakistanCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementAssetsOutsidePakistanBeans(new ArrayList<TaxformWealthStatementAssetsOutsidePakistanBean>());
                    }

                    /*==================WELTH STATEMENT -> OWE ANY LOANS OR CREDIT */

                    if (taxform.getTaxformWelthStatementOweAnyLoansOrCreditList() != null && taxform.getTaxformWelthStatementOweAnyLoansOrCreditList().size() > 0) {
                        List<Taxform_WelthStatement_OweAnyLoansOrCredit> taxformWelthStatementOweAnyLoansOrCredits = taxform.getTaxformWelthStatementOweAnyLoansOrCreditList();
                        List<TaxformWealthStatementOweAnyLoanOrCreditBean> taxformWealthStatementOweAnyLoanOrCreditBeans = new ArrayList<TaxformWealthStatementOweAnyLoanOrCreditBean>();

                        for (Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementOweAnyLoansOrCredit : taxformWelthStatementOweAnyLoansOrCredits){
                            TaxformWealthStatementOweAnyLoanOrCreditBean taxformWealthStatementOweAnyLoanOrCreditBean = new TaxformWealthStatementOweAnyLoanOrCreditBean();

                            if (taxformWelthStatementOweAnyLoansOrCredit.getId() != null) {
                                taxformWealthStatementOweAnyLoanOrCreditBean.setId(taxformWelthStatementOweAnyLoansOrCredit.getId());
                            }
                            if (taxformWelthStatementOweAnyLoansOrCredit.getForm() != null) {
                                taxformWealthStatementOweAnyLoanOrCreditBean.setForm(taxformWelthStatementOweAnyLoansOrCredit.getForm());
                            }
                            if (taxformWelthStatementOweAnyLoansOrCredit.getCreatorsNtnOrCnic() != null) {
                                taxformWealthStatementOweAnyLoanOrCreditBean.setCreatorsNtnOrCnic(taxformWelthStatementOweAnyLoansOrCredit.getCreatorsNtnOrCnic());
                            }
                            if (taxformWelthStatementOweAnyLoansOrCredit.getCreatorsName() != null) {
                                taxformWealthStatementOweAnyLoanOrCreditBean.setCreatorsName(taxformWelthStatementOweAnyLoansOrCredit.getCreatorsName());
                            }
                            if (taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost() != null) {
                                taxformWealthStatementOweAnyLoanOrCreditBean.setValueAtCost(taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost() + "");
                            }

                            taxformWealthStatementOweAnyLoanOrCreditBeans.add(taxformWealthStatementOweAnyLoanOrCreditBean);
                        }
                        taxformDetailedBean.setOweAnyLoanOrCreditCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementOweAnyLoanOrCreditBeans(taxformWealthStatementOweAnyLoanOrCreditBeans);
                    } else {
                        taxformDetailedBean.setOweAnyLoanOrCreditCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementOweAnyLoanOrCreditBeans(new ArrayList<TaxformWealthStatementOweAnyLoanOrCreditBean>());
                    }

                    /*==================WELTH STATEMENT -> DETAILS OF PERSONAL EXPENSES */

                    if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense() != null) {
                        Taxform_WelthStatement_DetailsOfPersonalExpense taxformWelthStatementDetailsOfPersonalExpense= taxform.getTaxformWelthStatementDetailsOfPersonalExpense();
                        Taxform_WealthStatement_PersonalExpenses_Bean taxformWealthStatementPersonalExpensesBean = new Taxform_WealthStatement_PersonalExpenses_Bean();

                        if (taxformWelthStatementDetailsOfPersonalExpense.getRent() != null) {
                            taxformWealthStatementPersonalExpensesBean.setRent(taxformWelthStatementDetailsOfPersonalExpense.getRent() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getRatesTaxesChargeCess() != null/* && taxformWelthStatementDetailsOfPersonalExpense.getRatesTaxesChargeCess().compareTo(0.00) >= 0*/) {
                            taxformWealthStatementPersonalExpensesBean.setRatesTaxesChargeCess(taxformWelthStatementDetailsOfPersonalExpense.getRatesTaxesChargeCess() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getVehicleRunningOrMaintenance() != null) {
                            taxformWealthStatementPersonalExpensesBean.setVehicleRunningOrMaintenance(taxformWelthStatementDetailsOfPersonalExpense.getVehicleRunningOrMaintenance() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getTravelling() != null) {
                            taxformWealthStatementPersonalExpensesBean.setTravelling(taxformWelthStatementDetailsOfPersonalExpense.getTravelling() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getElectricity() != null) {
                            taxformWealthStatementPersonalExpensesBean.setElectricity(taxformWelthStatementDetailsOfPersonalExpense.getElectricity() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getWater() != null) {
                            taxformWealthStatementPersonalExpensesBean.setWater(taxformWelthStatementDetailsOfPersonalExpense.getWater() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getGas() != null) {
                            taxformWealthStatementPersonalExpensesBean.setGas(taxformWelthStatementDetailsOfPersonalExpense.getGas() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getTelephone() != null) {
                            taxformWealthStatementPersonalExpensesBean.setTelephone(taxformWelthStatementDetailsOfPersonalExpense.getTelephone() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getAssetsInsuranceOrSecurity() != null) {
                            taxformWealthStatementPersonalExpensesBean.setAssetsInsuranceOrSecurity(taxformWelthStatementDetailsOfPersonalExpense.getAssetsInsuranceOrSecurity() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getMedical() != null) {
                            taxformWealthStatementPersonalExpensesBean.setMedical(taxformWelthStatementDetailsOfPersonalExpense.getMedical() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getEducational() != null) {
                            taxformWealthStatementPersonalExpensesBean.setEducational(taxformWelthStatementDetailsOfPersonalExpense.getEducational() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getClub() != null) {
                            taxformWealthStatementPersonalExpensesBean.setClub(taxformWelthStatementDetailsOfPersonalExpense.getClub() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getFunctionsOrGatherings() != null) {
                            taxformWealthStatementPersonalExpensesBean.setFunctionsOrGatherings(taxformWelthStatementDetailsOfPersonalExpense.getFunctionsOrGatherings() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getDonationZakatAnnuityProfitOnDebutEtc() != null) {
                            taxformWealthStatementPersonalExpensesBean.setDonationZakatAnnuityProfitOnDebutEtc(taxformWelthStatementDetailsOfPersonalExpense.getDonationZakatAnnuityProfitOnDebutEtc() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getOtherPersonalOrHouseholdExpense() != null) {
                            taxformWealthStatementPersonalExpensesBean.setOtherPersonalOrHouseholdExpense(taxformWelthStatementDetailsOfPersonalExpense.getOtherPersonalOrHouseholdExpense() + "");
                        }
                        if (taxformWelthStatementDetailsOfPersonalExpense.getGift() != null) {
                            taxformWealthStatementPersonalExpensesBean.setGift(taxformWelthStatementDetailsOfPersonalExpense.getGift() + "");
                        }

                        taxformDetailedBean.setPersonalExpensesCheck(true);
                        taxformDetailedBean.setTaxformWealthStatementPersonalExpensesBean(taxformWealthStatementPersonalExpensesBean);
                    } else {
                        taxformDetailedBean.setPersonalExpensesCheck(false);
                        taxformDetailedBean.setTaxformWealthStatementPersonalExpensesBean(new Taxform_WealthStatement_PersonalExpenses_Bean());
                    }

                    taxformDetailedBean.setCode(1);
                    taxformDetailedBean.setMessage("Data Sent Successfully");

                    return new ResponseEntity<TaxformDetailedBean>(taxformDetailedBean, HttpStatus.OK);
                } else {
                    MyPrint.print("Inside First Else");
                    return new ResponseEntity<TaxformDetailedBean>(new TaxformDetailedBean(0, "Incomplete Data"), HttpStatus.OK);
                }
            } else {
                MyPrint.println("Inside Second Else");
                return new ResponseEntity<TaxformDetailedBean>(new TaxformDetailedBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<TaxformDetailedBean>(new TaxformDetailedBean(0, "Incomplete Data"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/resetTaxform" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> resetTaxform(@RequestBody TaxformBean taxformBean) {
        try {

            User user = null;
            if (taxformBean.getUserId() != null) {
                user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);
            }

            if (taxformBean.getTaxformId() != null) {
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());
                if (taxform != null) {
                    if (taxform.getStatus().getId() != 0) {
                        return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                    }
                    taxformServices.deleteTaxformDeductibleAllowanceOrCredit(taxform);
                    taxformServices.deleteTaxformIncomeTaxSalary(taxform);
                    taxformServices.deleteTaxformIncomeTaxProperty(taxform);

                    taxformServices.deleteTaxformIncomeTaxCapitalGain(taxform);
                    /*taxformServices.deleteTaxformIncomeTaxCapitalGainOnShare(taxform);
                    taxformServices.deleteTaxformIncomeTaxCapitalGainMutualFunds(taxform);
                    taxformServices.deleteTaxformIncomeTaxCapitalGainProperty(taxform);*/

                    taxformServices.deleteTaxformIncomeTaxOtherSources(taxform);

                    taxformServices.deleteAllTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(taxform);
                    taxform.setTaxformIncomeTaxOtherSourcesProfitOnBankDepositList(new ArrayList<>());

                    taxformServices.deleteAllTaxformIncomeTaxOtherSourcesInflow(taxform);
                    taxform.setTaxformIncomeTaxOtherSourcesOtherInFlowsList(new ArrayList<>());

                    taxformServices.deleteTaxformTaxDeductedCollectedBankingTransactions(taxform);
                    taxform.setTaxformTaxDeductedCollectedBankingTransactionList(new ArrayList<>());
                    taxformServices.deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(taxform);
                    taxform.setTaxformTaxDeductedCollectedWithholdTaxVehicleList(new ArrayList<>());
                    taxformServices.deleteTaxformTaxDeductedCollectedUtilities(taxform);
                    taxform.setTaxformTaxDeductedCollectedUtilitiesList(new ArrayList<>());
                    taxformServices.deleteTaxformTaxDeductedCollectedOthers(taxform);

                    taxformServices.deleteWealthStatement(taxform);

                    taxform.setTaxformWelthStatementPropertyDetailList(new ArrayList<>());
                    taxform.setTaxformWelthStatementBankAccountsOrInvestmentsList(new ArrayList<>());
                    taxform.setTaxformWelthStatementOtherReceivablesOrAssetsList(new ArrayList<>());
                    taxform.setTaxformWelthStatementOwnVehicleList(new ArrayList<>());
                    taxform.setTaxformWelthStatementOtherPossessionsList(new ArrayList<>());
                    taxform.setTaxformWelthStatementAssetsOutSidePakistanList(new ArrayList<>());
                    taxform.setTaxformWelthStatementOweAnyLoansOrCreditList(new ArrayList<>());
                    taxform.setNameAsPerCnic(null);
                    taxform.setCnic(null);
                    taxform.setDateOfBirth(null);
                    taxform.setEmail(null);
                    taxform.setMobileNo(null);
                    taxform.setOccupation(null);
                    taxform.setResidenceAddress(null);
                    taxform.setNationality(null);
                    taxform.setResidenceStatus(null);
                    taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

                    taxform.setCurrentScreen(-1);
                    taxformServices.updateTaxform(taxform);
                    return new ResponseEntity<>(new Status(1, "Your form is successfully reset."), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new Status(0, "Taxform is not created"), HttpStatus.OK);
                }
            } else {
                List<Taxform> taxformList = taxformServices.findAllByUserAndActiveTaxformYears(user);
                if (taxformList != null && taxformList.size() > 0) {
                    for (Taxform taxform : taxformList) {
                        if (taxform.getStatus().getId() != 0) {
                            return new ResponseEntity<>(new Status(0, "Taxform Already Submited"), HttpStatus.OK);
                        }
                        taxformServices.deleteTaxformDeductibleAllowanceOrCredit(taxform);
                        taxformServices.deleteTaxformIncomeTaxSalary(taxform);
                        taxformServices.deleteTaxformIncomeTaxProperty(taxform);
                        taxformServices.deleteTaxformIncomeTaxCapitalGain(taxform);
                        /*taxformServices.deleteTaxformIncomeTaxCapitalGainOnShare(taxform);
                        taxformServices.deleteTaxformIncomeTaxCapitalGainMutualFunds(taxform);
                        taxformServices.deleteTaxformIncomeTaxCapitalGainProperty(taxform);*/
                        taxformServices.deleteTaxformIncomeTaxOtherSources(taxform);

                        taxformServices.deleteAllTaxformIncomeTaxOtherSourcesProfitOnBankDeposit(taxform);
                        taxform.setTaxformIncomeTaxOtherSourcesProfitOnBankDepositList(new ArrayList<>());

                        taxformServices.deleteAllTaxformIncomeTaxOtherSourcesInflow(taxform);
                        taxform.setTaxformIncomeTaxOtherSourcesOtherInFlowsList(new ArrayList<>());

                        taxformServices.deleteTaxformTaxDeductedCollectedBankingTransactions(taxform);
                        taxform.setTaxformTaxDeductedCollectedBankingTransactionList(new ArrayList<>());
                        taxformServices.deleteTaxformTaxDeductedCollectedWithholdTaxVehicle(taxform);
                        taxform.setTaxformTaxDeductedCollectedWithholdTaxVehicleList(new ArrayList<>());
                        taxformServices.deleteTaxformTaxDeductedCollectedUtilities(taxform);
                        taxform.setTaxformTaxDeductedCollectedUtilitiesList(new ArrayList<>());
                        taxformServices.deleteTaxformTaxDeductedCollectedOthers(taxform);

                        taxformServices.deleteWealthStatement(taxform);

                        taxform.setTaxformWelthStatementPropertyDetailList(new ArrayList<>());
                        taxform.setTaxformWelthStatementBankAccountsOrInvestmentsList(new ArrayList<>());
                        taxform.setTaxformWelthStatementOtherReceivablesOrAssetsList(new ArrayList<>());
                        taxform.setTaxformWelthStatementOwnVehicleList(new ArrayList<>());
                        taxform.setTaxformWelthStatementOtherPossessionsList(new ArrayList<>());
                        taxform.setTaxformWelthStatementAssetsOutSidePakistanList(new ArrayList<>());
                        taxform.setTaxformWelthStatementOweAnyLoansOrCreditList(new ArrayList<>());
                        taxform.setNameAsPerCnic(null);
                        taxform.setCnic(null);
                        taxform.setDateOfBirth(null);
                        taxform.setEmail(null);
                        taxform.setMobileNo(null);
                        taxform.setOccupation(null);
                        taxform.setResidenceAddress(null);
                        taxform.setNationality(null);
                        taxform.setResidenceStatus(null);
                        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());

                        taxform.setCurrentScreen(-1);
                        taxformServices.updateTaxform(taxform);
                    }
                }
                return new ResponseEntity<>(new Status(1, "Taxforms are successfully reset."), HttpStatus.OK);
            }
        }catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, e.getMessage()), HttpStatus.OK);
        }
    }


    // TODO delete this service in next build
    /*@SuppressWarnings("unchecked")
    @RequestMapping(value = "/byTaxformStatus" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxformsByStatus(@RequestBody InputBean inputBean){
        if (inputBean.getUserId() != null && inputBean.getTaxformStatusId() != null){
            try {
                *//*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
                System.out.println("User Name :::::::: "  + user.getUsername());*//*

                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                Taxform_Status taxformStatus = taxformServices.findOneByTaxformStatusId(inputBean.getTaxformStatusId());
                if(user != null && taxformStatus != null) {
                    StatusBean statusBean = new StatusBean(1, "Successfull");
                    List<TaxformBeanForList> taxformBeanForListList = new ArrayList<TaxformBeanForList>();
                    List<Taxform> taxformList = taxformServices.findAllByUserRoleAndStatus(user);
                    for (Taxform taxform : taxformList) {
                        TaxformBeanForList taxformBeanForList = new TaxformBeanForList();

                        taxformBeanForList.setId(taxform.getId());
                        taxformBeanForList.setTaxformYear(taxform.getTaxformYear().getYear() + "");
                        taxformBeanForList.setUserName(taxform.getNameAsPerCnic());
                        taxformBeanForList.setUserCnic(taxform.getCnic());
                        taxformBeanForList.setUserEmail(taxform.getEmail());

                        taxformBeanForListList.add(taxformBeanForList);
                    }
                    statusBean.setResponse(taxformBeanForListList);
                    return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
                }

            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<StatusBean>(new StatusBean(0, "Exception"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<StatusBean>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }*/

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/detailViewForFbr" ,produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getTaxformForLawer(@RequestBody InputBean inputBean){
        if (inputBean.getUserId() != null && inputBean.getTaxformId() != null){
            try {

                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(inputBean.getTaxformId());

                if(user != null && taxform != null && taxform.getTaxformYear() != null && taxform.getTaxformYear().getYear() != null) {
                    StatusBean statusBean = new StatusBean(1, "Successfull");
                    Map<String, TaxformFbrDetailedBean> taxformFbrDetailedBeanMap = new HashMap<String, TaxformFbrDetailedBean>();

                    FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(taxform.getUser());
                    if (fbrUserAccountInfo != null) {

                        if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.REGISTERED ||
                                fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.LAWYER_CLOSE) {

                            TaxformFbrDetailedBean taxformFbrDetailedBeanCode = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanCode.setTotalAmount("1");
                            taxformFbrDetailedBeanMap.put("fbrCode", taxformFbrDetailedBeanCode);

                            TaxformFbrDetailedBean taxformFbrDetailedBeanPassword = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanPassword.setTotalAmount(fbrUserAccountInfo.getFbrPassword());
                            taxformFbrDetailedBeanMap.put("fbrPassword", taxformFbrDetailedBeanPassword);

                            TaxformFbrDetailedBean taxformFbrDetailedBeanPin = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanPin.setTotalAmount(fbrUserAccountInfo.getFbrPin());
                            taxformFbrDetailedBeanMap.put("fbrPin", taxformFbrDetailedBeanPin);
                        } else if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.NOT_REGISTERED) {
                            TaxformFbrDetailedBean taxformFbrDetailedBeanCode = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanCode.setTotalAmount("2");
                            taxformFbrDetailedBeanMap.put("fbrCode", taxformFbrDetailedBeanCode);
                        } else if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.LAWYER_ASSIGN) {
                            TaxformFbrDetailedBean taxformFbrDetailedBeanCode = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanCode.setTotalAmount("3");
                            taxformFbrDetailedBeanMap.put("fbrCode", taxformFbrDetailedBeanCode);

                            Assign assign = assignServices.findOneByFbrUserAccountInfo(fbrUserAccountInfo);
                            TaxformFbrDetailedBean taxformFbrDetailedBeanAssignId = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanAssignId.setTotalAmount(assign.getId() + "");
                            taxformFbrDetailedBeanMap.put("assignId", taxformFbrDetailedBeanAssignId);
                        } else if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.LAWYER_OPEN) {
                            TaxformFbrDetailedBean taxformFbrDetailedBeanCode = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanCode.setTotalAmount("4");
                            taxformFbrDetailedBeanMap.put("fbrCode", taxformFbrDetailedBeanCode);

                            Assign assign = assignServices.findOneByFbrUserAccountInfo(fbrUserAccountInfo);
                            TaxformFbrDetailedBean taxformFbrDetailedBeanAssignId = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBeanAssignId.setTotalAmount(assign.getId() + "");
                            taxformFbrDetailedBeanMap.put("assignId", taxformFbrDetailedBeanAssignId);
                        }
                    } else {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount("0");
                        taxformFbrDetailedBeanMap.put("fbrCode", taxformFbrDetailedBean);
                    }

                    if (StringUtils.isNotEmpty(taxform.getNameAsPerCnic())) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getNameAsPerCnic());
                        taxformFbrDetailedBeanMap.put("nameAsPerCnic", taxformFbrDetailedBean);
                    }

                    if (StringUtils.isNotEmpty(taxform.getCnic())) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getCnic());
                        taxformFbrDetailedBeanMap.put("cnicNo", taxformFbrDetailedBean);
                    }

                    if (taxform.getDateOfBirth() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(CommonUtil.changeDateToString(taxform.getDateOfBirth()));
                        taxformFbrDetailedBeanMap.put("dateOfBirth", taxformFbrDetailedBean);
                    }

                    if (StringUtils.isNotEmpty(taxform.getEmail())) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getEmail());
                        taxformFbrDetailedBeanMap.put("emailAddress", taxformFbrDetailedBean);
                    }

                    if (StringUtils.isNotEmpty(taxform.getMobileNo())) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getMobileNo());
                        taxformFbrDetailedBeanMap.put("mobileNo", taxformFbrDetailedBean);
                    }

                    if (StringUtils.isNotEmpty(taxform.getResidenceAddress())) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getResidenceAddress());
                        taxformFbrDetailedBeanMap.put("residentAddress", taxformFbrDetailedBean);
                    }

                    TaxformCalculationBean taxformCalculationBean = TaxformCalculator.taxformCalculator(taxform);

                    if (taxform.getResidenceStatus() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getResidenceStatus());

                        taxformFbrDetailedBeanMap.put("residentStatus", taxformFbrDetailedBean);
                    }

                    if (taxform.getOccupation() != null && !taxform.getOccupation().isEmpty()) {

                        if (taxform.getOccupation().trim().equalsIgnoreCase("Federal Govt. Employee")) {
                            if (taxformCalculationBean != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                if (taxformCalculationBean.getSalaryTaxableIncome() != null) {
                                    taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getSalaryTaxableIncome());
                                } else {
                                    taxformFbrDetailedBean.setTotalAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryWithholdingTax() != null) {
                                    taxformFbrDetailedBean.setExemptAmount(taxformCalculationBean.getSalaryWithholdingTax());
                                } else {
                                    taxformFbrDetailedBean.setExemptAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryTaxCharge() != null) {
                                    taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getSalaryTaxCharge());
                                } else {
                                    taxformFbrDetailedBean.setThirdAmount("0");
                                }
                                taxformFbrDetailedBeanMap.put("64020001", taxformFbrDetailedBean);
                            }
                        } else if (taxform.getOccupation().trim().equalsIgnoreCase("Provincial Govt")) {
                            if (taxformCalculationBean != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                if (taxformCalculationBean.getSalaryTaxableIncome() != null) {
                                    taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getSalaryTaxableIncome());
                                } else {
                                    taxformFbrDetailedBean.setTotalAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryWithholdingTax() != null) {
                                    taxformFbrDetailedBean.setExemptAmount(taxformCalculationBean.getSalaryWithholdingTax());
                                } else {
                                    taxformFbrDetailedBean.setExemptAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryTaxCharge() != null) {
                                    taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getSalaryTaxCharge());
                                } else {
                                    taxformFbrDetailedBean.setThirdAmount("0");
                                }
                                taxformFbrDetailedBeanMap.put("64020002", taxformFbrDetailedBean);
                            }
                        } else if (taxform.getOccupation().trim().equalsIgnoreCase("Corporate Sector Employee")) {
                            if (taxformCalculationBean != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                if (taxformCalculationBean.getSalaryTaxableIncome() != null) {
                                    taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getSalaryTaxableIncome());
                                } else {
                                    taxformFbrDetailedBean.setTotalAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryWithholdingTax() != null) {
                                    taxformFbrDetailedBean.setExemptAmount(taxformCalculationBean.getSalaryWithholdingTax());
                                } else {
                                    taxformFbrDetailedBean.setExemptAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryTaxCharge() != null) {
                                    taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getSalaryTaxCharge());
                                } else {
                                    taxformFbrDetailedBean.setThirdAmount("0");
                                }
                                taxformFbrDetailedBeanMap.put("64020003", taxformFbrDetailedBean);
                            }
                        } else {
                            if (taxformCalculationBean != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                if (taxformCalculationBean.getSalaryTaxableIncome() != null) {
                                    taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getSalaryTaxableIncome());
                                } else {
                                    taxformFbrDetailedBean.setTotalAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryWithholdingTax() != null) {
                                    taxformFbrDetailedBean.setExemptAmount(taxformCalculationBean.getSalaryWithholdingTax());
                                } else {
                                    taxformFbrDetailedBean.setExemptAmount("0");
                                }
                                if (taxformCalculationBean.getSalaryTaxCharge() != null) {
                                    taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getSalaryTaxCharge());
                                } else {
                                    taxformFbrDetailedBean.setThirdAmount("0");
                                }
                                taxformFbrDetailedBeanMap.put("64020004", taxformFbrDetailedBean);
                            }
                        }
                    }

                    if (taxformCalculationBean.getPropertyTaxableIncome() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getPropertyTaxableIncome());

                        taxformFbrDetailedBeanMap.put("2001", taxformFbrDetailedBean);
                    }

                    if (taxformCalculationBean.getPropertyTaxableIncome() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getPropertyTaxableIncome());
                        taxformFbrDetailedBean.setExemptAmount(taxformCalculationBean.getPropertyWithholdingTax());
                        taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getPropertyTaxCharge());

                        taxformFbrDetailedBeanMap.put("64080052", taxformFbrDetailedBean);
                    }

                    Double annualSalaryTaxAmount = 0.0;
                    if (taxform.getTaxformIncomeTaxSalary() != null && taxform.getTaxformIncomeTaxSalary().getBasicSalary() != null) {
                        annualSalaryTaxAmount += taxform.getTaxformIncomeTaxSalary().getBasicSalary();
                    }
                    /*if (taxformCalculationBean != null && taxformCalculationBean.getTaxOnTax() != null) {
                        annualSalaryTaxAmount += taxformCalculationBean.getTaxOnTax();
                    }*/
                    TaxformFbrDetailedBean taxformFbrDetailedBeanAnnualSalary = new TaxformFbrDetailedBean();
                    taxformFbrDetailedBeanAnnualSalary.setTotalAmount(annualSalaryTaxAmount + "");
                    taxformFbrDetailedBeanMap.put("1009", taxformFbrDetailedBeanAnnualSalary);

                    if (taxformCalculationBean != null && taxformCalculationBean.getVehicleAllowance() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getVehicleAllowance() + "");

                        taxformFbrDetailedBeanMap.put("1089", taxformFbrDetailedBean);
                    }

                    if (taxform.getTaxformIncomeTaxSalary() != null) {

                        Double allowancesTaxAmount = 0.0;
                        Double allowancesExempt = 0.0;

                        if (taxform.getTaxformIncomeTaxSalary().getMedicalAllowance() != null) {
                            allowancesTaxAmount += taxform.getTaxformIncomeTaxSalary().getMedicalAllowance();
                            if (taxform.getTaxformIncomeTaxSalary().getBasicSalary() != null) {
                                if (taxform.getTaxformIncomeTaxSalary().getMedicalAllowance() > CommonUtil.dashPercentOfValue(10.0,taxform.getTaxformIncomeTaxSalary().getBasicSalary())) {
                                    allowancesExempt += CommonUtil.dashPercentOfValue(10.0, taxformCalculationBean.getBasicSalary());
                                } else {
                                    allowancesExempt += taxform.getTaxformIncomeTaxSalary().getMedicalAllowance();
                                }
                            } else {
                                allowancesExempt += taxform.getTaxformIncomeTaxSalary().getMedicalAllowance();
                            }
                        } else {
                            allowancesExempt += 0;
                        }

                        if (taxform.getTaxformIncomeTaxSalary().getTada() != null) {
                            allowancesExempt += taxform.getTaxformIncomeTaxSalary().getTada();
                        }

                        if (taxform.getTaxformIncomeTaxSalary().getOtherAllowance() != null) {
                            allowancesTaxAmount += taxform.getTaxformIncomeTaxSalary().getOtherAllowance();
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(allowancesTaxAmount + "");
                        taxformFbrDetailedBean.setExemptAmount(allowancesExempt + "");

                        taxformFbrDetailedBeanMap.put("1049", taxformFbrDetailedBean);

                        Double profitsTotalAmount = 0.0;
                        Double profitsExempt = 0.0;

                        if (taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer() != null) {
                            if (taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer() > 0.0 && taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer() > 150000) {
                                profitsTotalAmount += taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer();
                                profitsExempt += taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer() - 150000;
                            } else {
                                profitsTotalAmount = taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer();
                                profitsExempt += taxform.getTaxformIncomeTaxSalary().getProvidentFundByEmployeer();
                            }
                        }

                        if (taxform.getTaxformIncomeTaxSalary().getProvidentOrGratuityFundReceived() != null) {
                            profitsExempt += taxform.getTaxformIncomeTaxSalary().getProvidentOrGratuityFundReceived();
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForProfits = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForProfits.setTotalAmount(profitsTotalAmount + "");
                        taxformFbrDetailedBeanForProfits.setExemptAmount(profitsExempt + "");

                        taxformFbrDetailedBeanMap.put("1099", taxformFbrDetailedBeanForProfits);
                    }

                    if (taxform.getTaxformIncomeTaxOtherSources() != null) {
                        if (taxform.getTaxformIncomeTaxOtherSources().getDividentByPowerCompanies() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByPowerCompanies() + "");
                            taxformFbrDetailedBean.setExemptAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByPowerCompaniesTaxDeducted() + "");
                            taxformFbrDetailedBean.setThirdAmount(CommonUtil.dashPercentOfValue(7.5, taxform.getTaxformIncomeTaxOtherSources().getDividentByPowerCompanies()) + "");

                            taxformFbrDetailedBeanMap.put("64030052", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFunds()!= null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFunds() + "");
                            taxformFbrDetailedBean.setExemptAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFundsTaxDeducted() + "");
                            taxformFbrDetailedBean.setThirdAmount(CommonUtil.dashPercentOfValue(10.0, taxform.getTaxformIncomeTaxOtherSources().getDividentByMutualFunds()) + "");

                            taxformFbrDetailedBeanMap.put("64030053", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformIncomeTaxOtherSources().getDividentByOtherCompaniesStockFund()!= null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByOtherCompaniesStockFund() + "");
                            taxformFbrDetailedBean.setExemptAmount(taxform.getTaxformIncomeTaxOtherSources().getDividentByOtherCompaniesStockFundTaxDeducted() + "");
                            taxformFbrDetailedBean.setThirdAmount(CommonUtil.dashPercentOfValue(12.5, taxform.getTaxformIncomeTaxOtherSources().getDividentByOtherCompaniesStockFund()) + "");

                            taxformFbrDetailedBeanMap.put("64030054", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformIncomeTaxOtherSources().getBonusShares() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxOtherSources().getBonusShares() + "");
                            taxformFbrDetailedBean.setExemptAmount(taxform.getTaxformIncomeTaxOtherSources().getBonusSharesTaxDeducted() + "");
                            taxformFbrDetailedBean.setThirdAmount(CommonUtil.dashPercentOfValue(5.0, taxform.getTaxformIncomeTaxOtherSources().getBonusShares())+"");

                            taxformFbrDetailedBeanMap.put("64151351", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformIncomeTaxOtherSources().getAgriculturalIncome() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxOtherSources().getAgriculturalIncome() + "");
                            taxformFbrDetailedBean.setExemptAmount(taxform.getTaxformIncomeTaxOtherSources().getAgriculturalIncome() + "");

                            taxformFbrDetailedBeanMap.put("6100", taxformFbrDetailedBean);
                        }
                    }

                    if (taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList() != null) {

                        Double remittance = 0.0;
                        Double gift = 0.0;
                        Double inheritance = 0.0;
                        Double pension = 0.0;

                        for (TaxForm_IncomeTax_OtherSources_OtherInflow taxFormIncomeTaxOtherSourcesOtherInflow : taxform.getTaxformIncomeTaxOtherSourcesOtherInFlowsList()) {

                            if (taxFormIncomeTaxOtherSourcesOtherInflow != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType() != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType().equalsIgnoreCase("remittance")) {
                                if (taxFormIncomeTaxOtherSourcesOtherInflow.getAmount() != null) {
                                    remittance += taxFormIncomeTaxOtherSourcesOtherInflow.getAmount();
                                }
                            } else if (taxFormIncomeTaxOtherSourcesOtherInflow != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType() != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType().equalsIgnoreCase("gift")) {
                                if (taxFormIncomeTaxOtherSourcesOtherInflow.getAmount() != null) {
                                    gift += taxFormIncomeTaxOtherSourcesOtherInflow.getAmount();
                                }
                            } else if (taxFormIncomeTaxOtherSourcesOtherInflow != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType() != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType().equalsIgnoreCase("inheritance")) {
                                if (taxFormIncomeTaxOtherSourcesOtherInflow.getAmount() != null) {
                                    inheritance += taxFormIncomeTaxOtherSourcesOtherInflow.getAmount();
                                }
                            } else if (taxFormIncomeTaxOtherSourcesOtherInflow != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType() != null && taxFormIncomeTaxOtherSourcesOtherInflow.getType().equalsIgnoreCase("pension")) {
                                if (taxFormIncomeTaxOtherSourcesOtherInflow.getAmount() != null) {
                                    pension += taxFormIncomeTaxOtherSourcesOtherInflow.getAmount();
                                }
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanRemittance = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanRemittance.setTotalAmount(remittance + "");
                        taxformFbrDetailedBeanMap.put("7035", taxformFbrDetailedBeanRemittance);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanGift = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanGift.setTotalAmount(gift + "");
                        taxformFbrDetailedBeanMap.put("7037", taxformFbrDetailedBeanGift);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanInheritance = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanInheritance.setTotalAmount(inheritance + "");
                        taxformFbrDetailedBeanMap.put("7036", taxformFbrDetailedBeanInheritance);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanPension = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanPension.setTotalAmount(pension + "");
                        taxformFbrDetailedBeanMap.put("70XX", taxformFbrDetailedBeanPension);
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainProperty() != null) {

                        Double capitalGainPropertyFor0Percent = 0.0;

                        if (taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndMoreThan3YearsPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndMoreThan3YearsSaleCost() != null) {
                            capitalGainPropertyFor0Percent += (taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndMoreThan3YearsSaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndMoreThan3YearsPurchaseCost());
                        }

                        if (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyAndMoreThan3YearsPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyAndMoreThan3YearsSaleCost() != null) {
                            capitalGainPropertyFor0Percent += (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyAndMoreThan3YearsSaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyAndMoreThan3YearsPurchaseCost());
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanFor0Percent = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanFor0Percent.setTotalAmount(capitalGainPropertyFor0Percent + "");

                        taxformFbrDetailedBeanMap.put("64220051", taxformFbrDetailedBeanFor0Percent);

                        Double capitalGainPropertyFor5Percent = 0.0;

                        if (taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndUpto3YearsPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndUpto3YearsSaleCost() != null) {
                            capitalGainPropertyFor5Percent += (taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndUpto3YearsSaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getBefore1JulyAndUpto3YearsPurchaseCost());
                        }

                        if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1July2To3YearsPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1July2To3YearsSaleCost() != null) {
                                capitalGainPropertyFor5Percent += (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1July2To3YearsSaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1July2To3YearsPurchaseCost());
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanFor5Percent = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanFor5Percent.setTotalAmount(capitalGainPropertyFor5Percent + "");

                        taxformFbrDetailedBeanMap.put("64220053", taxformFbrDetailedBeanFor5Percent);


                        if ((taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                            if (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyUpto1YearPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyUpto1YearSaleCost() != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                taxformFbrDetailedBean.setTotalAmount((taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyUpto1YearSaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyUpto1YearPurchaseCost()) + "");

                                taxformFbrDetailedBeanMap.put("64220055", taxformFbrDetailedBean);
                            }
                        } else {
                            if (taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyPurchaseCost() != null && taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulySaleCost() != null) {
                                TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                                taxformFbrDetailedBean.setTotalAmount((taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulySaleCost() - taxform.getTaxformIncomeTaxCapitalGainProperty().getAfter1JulyPurchaseCost()) + "");

                                taxformFbrDetailedBeanMap.put("64220055", taxformFbrDetailedBean);
                            }
                        }
                    }

                    List<Double> percentagesForCapitalGainOnShare = CommonUtil.getCapitalGainOnDisposalOfSecurities(taxform.getTaxformYear().getYear() + "");

                    Double capitalGainOnSecurities15First = 0.0;
                    Double capitalGainOnSecurities15Second = 0.0;
                    Double capitalGainOnSecurities15Third = 0.0;

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getLessThan12MonthsFieldsCapitalGain() != null) {
                        capitalGainOnSecurities15First += taxform.getTaxformIncomeTaxCapitalGainOnShare().getLessThan12MonthsFieldsCapitalGain();
                        capitalGainOnSecurities15Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), taxform.getTaxformIncomeTaxCapitalGainOnShare().getLessThan12MonthsFieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getLessThan12MonthsTaxDeducted() != null) {
                        capitalGainOnSecurities15Second += taxform.getTaxformIncomeTaxCapitalGainOnShare().getLessThan12MonthsTaxDeducted();
                    }

                    if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                        if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getAcquiredOnOrAfter1JulyCapitalGain() != null) {
                            capitalGainOnSecurities15First += taxform.getTaxformIncomeTaxCapitalGainOnShare().getAcquiredOnOrAfter1JulyCapitalGain();
                            capitalGainOnSecurities15Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), taxform.getTaxformIncomeTaxCapitalGainOnShare().getAcquiredOnOrAfter1JulyCapitalGain());
                        }

                        if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getAcquiredOnOrAfter1JulyTaxDeducted() != null) {
                            capitalGainOnSecurities15Second += taxform.getTaxformIncomeTaxCapitalGainOnShare().getAcquiredOnOrAfter1JulyTaxDeducted();
                        }
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsFieldsCapitalGain() != null) {
                        capitalGainOnSecurities15First += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsFieldsCapitalGain();
                        capitalGainOnSecurities15Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsFieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsTaxDeducted() != null) {
                        capitalGainOnSecurities15Second += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsTaxDeducted();
                    }

                    if (taxform.getTaxformYear() != null && (taxform.getTaxformYear().getYear() == 2018 || taxform.getTaxformYear().getYear() == 2019)) {
                        if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAcquiredOnOrAfter1JulyCapitalGain() != null) {
                            capitalGainOnSecurities15First += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAcquiredOnOrAfter1JulyCapitalGain();
                            capitalGainOnSecurities15Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(0), taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAcquiredOnOrAfter1JulyCapitalGain());
                        }

                        if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAcquiredOnOrAfter1JulyTaxDeducted() != null) {
                            capitalGainOnSecurities15Second += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAcquiredOnOrAfter1JulyTaxDeducted();
                        }
                    }

                    TaxformFbrDetailedBean taxformFbrDetailedBeanCapitalGainOnSecurity15 = new TaxformFbrDetailedBean();
                    taxformFbrDetailedBeanCapitalGainOnSecurity15.setTotalAmount(capitalGainOnSecurities15First + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity15.setExemptAmount(capitalGainOnSecurities15Second + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity15.setThirdAmount(capitalGainOnSecurities15Third + "");

                    taxformFbrDetailedBeanMap.put("64220156", taxformFbrDetailedBeanCapitalGainOnSecurity15);


                    Double capitalGainOnSecurities12First = 0.0;
                    Double capitalGainOnSecurities12Second = 0.0;
                    Double capitalGainOnSecurities12Third = 0.0;

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null) {
                        capitalGainOnSecurities12First += taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan12ButLessThan24MonthsFieldsCapitalGain();
                        capitalGainOnSecurities12Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(1), taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan12ButLessThan24MonthsFieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan12ButLessThan24MonthsTaxDeducted() != null) {
                        capitalGainOnSecurities12Second += taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan12ButLessThan24MonthsTaxDeducted();
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null) {
                        capitalGainOnSecurities12First += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan12ButLessThan24MonthsFieldsCapitalGain();
                        capitalGainOnSecurities12Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(1), taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan12ButLessThan24MonthsFieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsTaxDeducted() != null) {
                        capitalGainOnSecurities12Second += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsTaxDeducted();
                    }

                    TaxformFbrDetailedBean taxformFbrDetailedBeanCapitalGainOnSecurity12 = new TaxformFbrDetailedBean();
                    taxformFbrDetailedBeanCapitalGainOnSecurity12.setTotalAmount(capitalGainOnSecurities12First + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity12.setExemptAmount(capitalGainOnSecurities12Second + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity12.setThirdAmount(capitalGainOnSecurities12Third + "");

                    taxformFbrDetailedBeanMap.put("64220155", taxformFbrDetailedBeanCapitalGainOnSecurity12);


                    Double capitalGainOnSecurities7First = 0.0;
                    Double capitalGainOnSecurities7Second = 0.0;
                    Double capitalGainOnSecurities7Third = 0.0;

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null) {
                        capitalGainOnSecurities7First += taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain();
                        capitalGainOnSecurities7Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(2), taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null) {
                        capitalGainOnSecurities7Second += taxform.getTaxformIncomeTaxCapitalGainOnShare().getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted();
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null) {
                        capitalGainOnSecurities7First += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain();
                        capitalGainOnSecurities7Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(2), taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null &&  taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted() != null) {
                        capitalGainOnSecurities7Second += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012TaxDeducted();
                    }

                    TaxformFbrDetailedBean taxformFbrDetailedBeanCapitalGainOnSecurity7 = new TaxformFbrDetailedBean();
                    taxformFbrDetailedBeanCapitalGainOnSecurity7.setTotalAmount(capitalGainOnSecurities7First + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity7.setExemptAmount(capitalGainOnSecurities7Second + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity7.setThirdAmount(capitalGainOnSecurities7Third + "");

                    taxformFbrDetailedBeanMap.put("64220157", taxformFbrDetailedBeanCapitalGainOnSecurity7);


                    Double capitalGainOnSecurities0First = 0.0;
                    Double capitalGainOnSecurities0Second = 0.0;
                    Double capitalGainOnSecurities0Third = 0.0;


                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getAquiredBefore1July2012FieldsCapitalGain() != null) {
                        capitalGainOnSecurities0First += taxform.getTaxformIncomeTaxCapitalGainOnShare().getAquiredBefore1July2012FieldsCapitalGain();
                        capitalGainOnSecurities0Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(3), taxform.getTaxformIncomeTaxCapitalGainOnShare().getAquiredBefore1July2012FieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainOnShare() != null && taxform.getTaxformIncomeTaxCapitalGainOnShare().getAquiredBefore1July2012TaxDeducted() != null) {
                        capitalGainOnSecurities0Second += taxform.getTaxformIncomeTaxCapitalGainOnShare().getAquiredBefore1July2012TaxDeducted();
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAquiredBefore1July2012FieldsCapitalGain() != null) {
                        capitalGainOnSecurities0First += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAquiredBefore1July2012FieldsCapitalGain();
                        capitalGainOnSecurities0Third += CommonUtil.dashPercentOfValue(percentagesForCapitalGainOnShare.get(3), taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAquiredBefore1July2012FieldsCapitalGain());
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds() != null && taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAquiredBefore1July2012TaxDeducted() != null) {
                        capitalGainOnSecurities0Second += taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getAquiredBefore1July2012TaxDeducted();
                    }

                    TaxformFbrDetailedBean taxformFbrDetailedBeanCapitalGainOnSecurity0 = new TaxformFbrDetailedBean();
                    taxformFbrDetailedBeanCapitalGainOnSecurity0.setTotalAmount(capitalGainOnSecurities0First + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity0.setExemptAmount(capitalGainOnSecurities0Second + "");
                    taxformFbrDetailedBeanCapitalGainOnSecurity0.setThirdAmount(capitalGainOnSecurities0Third + "");

                    taxformFbrDetailedBeanMap.put("64220151", taxformFbrDetailedBeanCapitalGainOnSecurity0);



                    /*if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan24MonthsButAquiredAfter1July2012FieldsCapitalGain() + "");

                        taxformFbrDetailedBeanMap.put("64220157", taxformFbrDetailedBean);
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan12ButLessThan24MonthsFieldsCapitalGain() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getMoreThan12ButLessThan24MonthsFieldsCapitalGain() + "");

                        taxformFbrDetailedBeanMap.put("64220155", taxformFbrDetailedBean);
                    }

                    if (taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsFieldsCapitalGain() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformIncomeTaxCapitalGainMutualFinds().getLessThan12MonthsFieldsCapitalGain() + "");

                        taxformFbrDetailedBeanMap.put("64220156", taxformFbrDetailedBean);
                    }*/

                    if (taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList() != null) {
                        Double profitAmount = 0.0;
                        Double taxDeducted = 0.0;
                        Double thirdAmount = 0.0;

                        List<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean> taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans = new ArrayList<TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean>();

                        for (Taxform_IncomeTax_OtherSources_ProfitOnBankDeposit taxformIncomeTaxOtherSourcesProfitOnBankDeposit  : taxform.getTaxformIncomeTaxOtherSourcesProfitOnBankDepositList()) {

                            if (taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount() != null && taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getTaxDeducted() != null) {
                                profitAmount += taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount();
                                taxDeducted += taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getTaxDeducted();
                                thirdAmount += CommonUtil.dashPercentOfValue(10.0, taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount());

                                TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean taxformIncomeTaxOtherSourcesProfitOnBankDepositBean = new TaxformIncomeTaxOtherSourcesProfitOnBankDepositBean();

                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setId(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getId());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setAccountType(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountType());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setBankName(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBankName());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setBranch(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getBranch());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setAccountNo(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getAccountNo());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setCurrency(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getCurrency());
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setProfitAmount(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getProfitAmount() + "");
                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBean.setTaxDeducted(taxformIncomeTaxOtherSourcesProfitOnBankDeposit.getTaxDeducted() + "");

                                taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans.add(taxformIncomeTaxOtherSourcesProfitOnBankDepositBean);
                            }

                        }
                        TaxformFbrDetailedBean taxformFbrDetailedBeanProfitOnBankDeposit = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanProfitOnBankDeposit.setTotalAmount(profitAmount + "");
                        taxformFbrDetailedBeanProfitOnBankDeposit.setExemptAmount(taxDeducted + "");
                        taxformFbrDetailedBeanProfitOnBankDeposit.setThirdAmount(thirdAmount + "");
                        taxformFbrDetailedBeanProfitOnBankDeposit.setList(taxformIncomeTaxOtherSourcesProfitOnBankDepositBeans);
                        taxformFbrDetailedBeanMap.put("64310056", taxformFbrDetailedBeanProfitOnBankDeposit);
                    }

                    if (taxform.getTaxformDeductibleAllowanceOrCredit() != null) {
                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderSection61() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();

                            if (taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderSection61() < CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation())) {
                                taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderSection61() + "");
                                taxformFbrDetailedBean.setExemptAmount("0");
                            } else {
                                taxformFbrDetailedBean.setTotalAmount(CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation()) + "");
                                taxformFbrDetailedBean.setExemptAmount((taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderSection61() - CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation())) + "");
                            }

                            taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getDonationsTaxCredit());

                            taxformFbrDetailedBeanMap.put("9311", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderClause61() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();

                            if (taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderClause61() < CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation())) {
                                taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderClause61() + "");
                                taxformFbrDetailedBean.setExemptAmount("0");
                            } else {
                                taxformFbrDetailedBean.setTotalAmount(CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation()) + "");
                                taxformFbrDetailedBean.setExemptAmount((taxform.getTaxformDeductibleAllowanceOrCredit().getDonationsUnderClause61() - CommonUtil.dashPercentOfValue(30.0, taxformCalculationBean.getTaxableIncomeForCalculation())) + "");
                            }

                            /*taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getDonationsTaxCredit());*/

                            taxformFbrDetailedBeanMap.put("9004", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInSharesMutualFundsAndLifeInsurance() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            Double smallest = CommonUtil.findSmallestNumberBetween3(1500000.0, CommonUtil.dashPercentOfValue(20.0, taxformCalculationBean.getTaxableIncomeForCalculation()), taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInSharesMutualFundsAndLifeInsurance());
                            taxformFbrDetailedBean.setTotalAmount(smallest + "");
                            if (smallest >= taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInSharesMutualFundsAndLifeInsurance()) {
                                taxformFbrDetailedBean.setExemptAmount((smallest - taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInSharesMutualFundsAndLifeInsurance()) + "");
                            } else {
                                taxformFbrDetailedBean.setExemptAmount("0");
                            }
                            taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getInvestementsTaxCredit());
                            taxformFbrDetailedBeanMap.put("9312", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            Double smallest = CommonUtil.findSmallestNumberBetween3(taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium(), CommonUtil.dashPercentOfValue(5.0, taxformCalculationBean.getTaxableIncomeForCalculation()), 100000.0);
                            taxformFbrDetailedBean.setTotalAmount(smallest + "");
                            if (smallest >= taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium()) {
                                taxformFbrDetailedBean.setExemptAmount(smallest - taxform.getTaxformDeductibleAllowanceOrCredit().getHelthInsurancePremium() + "");
                            } else {
                                taxformFbrDetailedBean.setExemptAmount("0");
                            }

                            taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getHealthInsuranceTaxCredit());
                            taxformFbrDetailedBeanMap.put("93121", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInApprovedPensionFund() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date d = sdf.parse("30/06/" + taxform.getTaxformYear().getYear());
                            Double age = CommonUtil.calculateAge(taxform.getDateOfBirth(), d);
                            Double allowedTaxPercentage = CommonUtil.allowedTaxableIncomePercentageOnPensionFunds(age);
                            MyPrint.println("Age::" + age + ":::" + "Allowed Tax Percentage:::" + allowedTaxPercentage);
                            Double smallest = CommonUtil.findSmallestNumberBetween2(taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInApprovedPensionFund(), CommonUtil.dashPercentOfValue(allowedTaxPercentage, taxformCalculationBean.getTaxableIncomeForCalculation()));
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInApprovedPensionFund() + "");
                            if (smallest >= taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInApprovedPensionFund()) {
                                taxformFbrDetailedBean.setExemptAmount((smallest - taxform.getTaxformDeductibleAllowanceOrCredit().getInvestmentInApprovedPensionFund()) + "");
                            } else {
                                taxformFbrDetailedBean.setExemptAmount("0");
                            }
                            taxformFbrDetailedBean.setThirdAmount(taxformCalculationBean.getPensionFundsTaxCredit());
                            taxformFbrDetailedBeanMap.put("9313", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getInterestOrRateOnHouseHolds() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getInterestOrRateOnHouseHolds() + "");

                            taxformFbrDetailedBeanMap.put("9007", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceTutionFees() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceTutionFees() + "");

                            taxformFbrDetailedBeanMap.put("9008", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceNoOfChildrens() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformDeductibleAllowanceOrCredit().getEducationAllowanceNoOfChildrens() + "");

                            taxformFbrDetailedBeanMap.put("noOfChildrens", taxformFbrDetailedBean);
                        }
                    }

                    if (taxform.getTaxformTaxDeductedCollectedOther() != null) {

                        if (taxform.getTaxformTaxDeductedCollectedOther().getTaxRefundOfPriorYear() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getTaxRefundOfPriorYear() + "");

                            taxformFbrDetailedBeanMap.put("92101", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformTaxDeductedCollectedOther().getWithdrawalFromPensionFund() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getWithdrawalFromPensionFund() + "");

                            taxformFbrDetailedBeanMap.put("64090201", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformTaxDeductedCollectedOther().getAirTicketInYearTaxDeducted() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getAirTicketInYearTaxDeducted() + "");

                            taxformFbrDetailedBeanMap.put("64150201", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformTaxDeductedCollectedOther().getFunctionsAndGatherings() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getFunctionsAndGatherings() + "");

                            taxformFbrDetailedBeanMap.put("64150401", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformTaxDeductedCollectedOther().getFunctionsAndGatherings() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getFunctionsAndGatherings() + "");

                            taxformFbrDetailedBeanMap.put("64150401", taxformFbrDetailedBean);
                        }
                        //=====================================Purchase==================================================

                        if (taxform.getTaxformTaxDeductedCollectedOther().getPropertyPurchaseTaxDeducted() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getPropertyPurchaseTaxDeducted() + "");

                            taxformFbrDetailedBeanMap.put("64151101", taxformFbrDetailedBean);
                        }

                        //=======================================Sale================================================

                        if (taxform.getTaxformTaxDeductedCollectedOther().getPropertySaleTaxDeducted() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getPropertySaleTaxDeducted() + "");

                            taxformFbrDetailedBeanMap.put("64150301", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformTaxDeductedCollectedOther().getEducationFeeTaxDeductedTaxDeducted() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformTaxDeductedCollectedOther().getEducationFeeTaxDeductedTaxDeducted() + "");

                            taxformFbrDetailedBeanMap.put("64150901", taxformFbrDetailedBean);
                        }

                    }

                    if (taxform.getTaxformTaxDeductedCollectedBankingTransactionList() != null) {

                        Double cash = 0.0;
                        Double transfer = 0.0;

                        List<TaxformTaxDeductedCollectedBankingTransactionBean> listCash = new ArrayList<TaxformTaxDeductedCollectedBankingTransactionBean>();

                        List<TaxformTaxDeductedCollectedBankingTransactionBean> transferList = new ArrayList<TaxformTaxDeductedCollectedBankingTransactionBean>();

                        for (Taxform_TaxDeductedCollected_BankingTransaction taxDeductedCollectedBankingTransaction : taxform.getTaxformTaxDeductedCollectedBankingTransactionList()) {
                            if (taxDeductedCollectedBankingTransaction.getTransactionType().trim().equalsIgnoreCase("cash")) {
                                if (taxDeductedCollectedBankingTransaction.getTaxDeductedAmount() != null) {
                                    cash += taxDeductedCollectedBankingTransaction.getTaxDeductedAmount();
                                }
                                TaxformTaxDeductedCollectedBankingTransactionBean taxformTaxDeductedCollectedBankingTransactionBean = new TaxformTaxDeductedCollectedBankingTransactionBean();
                                taxformTaxDeductedCollectedBankingTransactionBean.setId(taxDeductedCollectedBankingTransaction.getId());
                                taxformTaxDeductedCollectedBankingTransactionBean.setTransactionType(taxDeductedCollectedBankingTransaction.getTransactionType());
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountType(taxDeductedCollectedBankingTransaction.getAccountType());
                                taxformTaxDeductedCollectedBankingTransactionBean.setBankName(taxDeductedCollectedBankingTransaction.getBankName());
                                taxformTaxDeductedCollectedBankingTransactionBean.setBranch(taxDeductedCollectedBankingTransaction.getBranch());
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountNo(taxDeductedCollectedBankingTransaction.getAccountNo());
                                taxformTaxDeductedCollectedBankingTransactionBean.setTaxDeductedAmount(taxDeductedCollectedBankingTransaction.getTaxDeductedAmount()+"");
                                taxformTaxDeductedCollectedBankingTransactionBean.setCurrency(taxDeductedCollectedBankingTransaction.getCurrency());
                                listCash.add(taxformTaxDeductedCollectedBankingTransactionBean);
                            } else if (taxDeductedCollectedBankingTransaction.getTransactionType().trim().equalsIgnoreCase("transfer")) {
                                if (taxDeductedCollectedBankingTransaction.getTaxDeductedAmount() != null) {
                                    transfer += taxDeductedCollectedBankingTransaction.getTaxDeductedAmount();
                                }

                                TaxformTaxDeductedCollectedBankingTransactionBean taxformTaxDeductedCollectedBankingTransactionBean = new TaxformTaxDeductedCollectedBankingTransactionBean();
                                taxformTaxDeductedCollectedBankingTransactionBean.setId(taxDeductedCollectedBankingTransaction.getId());
                                taxformTaxDeductedCollectedBankingTransactionBean.setTransactionType(taxDeductedCollectedBankingTransaction.getTransactionType());
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountType(taxDeductedCollectedBankingTransaction.getAccountType());
                                taxformTaxDeductedCollectedBankingTransactionBean.setBankName(taxDeductedCollectedBankingTransaction.getBankName());
                                taxformTaxDeductedCollectedBankingTransactionBean.setBranch(taxDeductedCollectedBankingTransaction.getBranch());
                                taxformTaxDeductedCollectedBankingTransactionBean.setAccountNo(taxDeductedCollectedBankingTransaction.getAccountNo());
                                taxformTaxDeductedCollectedBankingTransactionBean.setTaxDeductedAmount(taxDeductedCollectedBankingTransaction.getTaxDeductedAmount()+"");
                                taxformTaxDeductedCollectedBankingTransactionBean.setCurrency(taxDeductedCollectedBankingTransaction.getCurrency());
                                transferList.add(taxformTaxDeductedCollectedBankingTransactionBean);
                            }


                        }
                        TaxformFbrDetailedBean taxformFbrDetailedBeanCash = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanCash.setTotalAmount(cash + "");
                        taxformFbrDetailedBeanCash.setList(listCash);
                        /*taxformFbrDetailedBeanCash.setList(transferList);*/
                        taxformFbrDetailedBeanMap.put("64100101", taxformFbrDetailedBeanCash);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanTransfer = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanTransfer.setTotalAmount(transfer + "");
                        taxformFbrDetailedBeanTransfer.setList(transferList);
                        /*taxformFbrDetailedBeanCash.setList(transferList);*/
                        taxformFbrDetailedBeanMap.put("64151501", taxformFbrDetailedBeanTransfer);
                    }

                    if (taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList() != null) {
                        Double registrations = 0.0;
                        Double transfer = 0.0;
                        Double sale = 0.0;
                        Double tax = 0.0;

                        List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> registerationVehicle = new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>();
                        List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> transferVehicle = new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>();
                        List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> saleVehicle = new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>();
                        List<TaxformTaxDeductedCollectedWithholdVehicleTaxBean> taxVehicle = new ArrayList<TaxformTaxDeductedCollectedWithholdVehicleTaxBean>();


                        for (Taxform_TaxDeductedCollected_WithholdTaxVehicle taxDeductedCollectedWithholdTaxVehicle : taxform.getTaxformTaxDeductedCollectedWithholdTaxVehicleList()){
                            if (taxDeductedCollectedWithholdTaxVehicle.getType().trim().equalsIgnoreCase("registeration")) {
                                registrations += taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted();

                                TaxformTaxDeductedCollectedWithholdVehicleTaxBean registerationVehicleBean = new TaxformTaxDeductedCollectedWithholdVehicleTaxBean();

                                registerationVehicleBean.setId(taxDeductedCollectedWithholdTaxVehicle.getId());
                                registerationVehicleBean.setType(taxDeductedCollectedWithholdTaxVehicle.getType());
                                registerationVehicleBean.setTaxDeducted(taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted()+"");
                                registerationVehicleBean.setVehicleRegistrationNo(taxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo());
                                registerationVehicleBean.setVehicleType(taxDeductedCollectedWithholdTaxVehicle.getVehicleType());
                                registerationVehicle.add(registerationVehicleBean);

                            } else if (taxDeductedCollectedWithholdTaxVehicle.getType().trim().equalsIgnoreCase("transfer")){
                                transfer += taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted();

                                TaxformTaxDeductedCollectedWithholdVehicleTaxBean transferVehicleBean = new TaxformTaxDeductedCollectedWithholdVehicleTaxBean();

                                transferVehicleBean.setId(taxDeductedCollectedWithholdTaxVehicle.getId());
                                transferVehicleBean.setType(taxDeductedCollectedWithholdTaxVehicle.getType());
                                transferVehicleBean.setTaxDeducted(taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted()+"");
                                transferVehicleBean.setVehicleRegistrationNo(taxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo());
                                transferVehicleBean.setVehicleType(taxDeductedCollectedWithholdTaxVehicle.getVehicleType());
                                transferVehicle.add(transferVehicleBean);

                            } else if (taxDeductedCollectedWithholdTaxVehicle.getType().trim().equalsIgnoreCase("sale")){
                                sale += taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted();

                                TaxformTaxDeductedCollectedWithholdVehicleTaxBean saleVehicleBean = new TaxformTaxDeductedCollectedWithholdVehicleTaxBean();

                                saleVehicleBean.setId(taxDeductedCollectedWithholdTaxVehicle.getId());
                                saleVehicleBean.setType(taxDeductedCollectedWithholdTaxVehicle.getType());
                                saleVehicleBean.setTaxDeducted(taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted()+"");
                                saleVehicleBean.setVehicleRegistrationNo(taxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo());
                                saleVehicleBean.setVehicleType(taxDeductedCollectedWithholdTaxVehicle.getVehicleType());
                                saleVehicle.add(saleVehicleBean);

                            } else if (taxDeductedCollectedWithholdTaxVehicle.getType().trim().equalsIgnoreCase("tax")){
                                tax += taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted();

                                TaxformTaxDeductedCollectedWithholdVehicleTaxBean taxVehicleBean = new TaxformTaxDeductedCollectedWithholdVehicleTaxBean();

                                taxVehicleBean.setId(taxDeductedCollectedWithholdTaxVehicle.getId());
                                taxVehicleBean.setType(taxDeductedCollectedWithholdTaxVehicle.getType());
                                taxVehicleBean.setTaxDeducted(taxDeductedCollectedWithholdTaxVehicle.getTaxDeducted()+"");
                                taxVehicleBean.setVehicleRegistrationNo(taxDeductedCollectedWithholdTaxVehicle.getVehicleRegistrationNo());
                                taxVehicleBean.setVehicleType(taxDeductedCollectedWithholdTaxVehicle.getVehicleType());
                                taxVehicle.add(taxVehicleBean);
                            }


                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForRegistrations = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForRegistrations.setTotalAmount(registrations + "");
                        taxformFbrDetailedBeanForRegistrations.setList(registerationVehicle);
                        taxformFbrDetailedBeanMap.put("64100301", taxformFbrDetailedBeanForRegistrations);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForTransfer = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForTransfer.setTotalAmount(transfer + "");
                        taxformFbrDetailedBeanForTransfer.setList(transferVehicle);
                        taxformFbrDetailedBeanMap.put("64100302", taxformFbrDetailedBeanForTransfer);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForSale = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForSale.setTotalAmount(sale + "");
                        taxformFbrDetailedBeanForSale.setList(saleVehicle);
                        taxformFbrDetailedBeanMap.put("64100303", taxformFbrDetailedBeanForSale);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForTax = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForTax.setTotalAmount(tax + "");
                        taxformFbrDetailedBeanForTax.setList(taxVehicle);
                        taxformFbrDetailedBeanMap.put("64130003", taxformFbrDetailedBeanForTax);
                    }

                    if (taxform.getTaxformTaxDeductedCollectedUtilitiesList() != null) {
                        /*Double electricity = 0.0;*/
                        Double telephone = 0.0;
                        Double cellPhone = 0.0;
                        Double internet = 0.0;

                        /*List<TaxformTaxDeductedCollectedUtilitiesBean> electricityUtilities = new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>();*/
                        List<TaxformTaxDeductedCollectedUtilitiesBean> telephoneUtilities = new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>();
                        List<TaxformTaxDeductedCollectedUtilitiesBean> cellphoneUtilities = new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>();
                        List<TaxformTaxDeductedCollectedUtilitiesBean> internetUtilities = new ArrayList<TaxformTaxDeductedCollectedUtilitiesBean>();

                        for (Taxform_TaxDeductedCollected_Utilities taxDeductedCollectedUtilities : taxform.getTaxformTaxDeductedCollectedUtilitiesList()) {
                            /*if (taxDeductedCollectedUtilities.getUtilityType().trim().equalsIgnoreCase("Electricity")) {
                                electricity += taxDeductedCollectedUtilities.getTaxDeducted();

                                TaxformTaxDeductedCollectedUtilitiesBean utilitiesBean = new TaxformTaxDeductedCollectedUtilitiesBean();
                                utilitiesBean.setId(taxDeductedCollectedUtilities.getId()+"");
                                utilitiesBean.setUtilityType(taxDeductedCollectedUtilities.getUtilityType());
                                utilitiesBean.setTaxDeducted(taxDeductedCollectedUtilities.getTaxDeducted()+"");
                                utilitiesBean.setReferenceOrConsumerNo(taxDeductedCollectedUtilities.getReferenceOrConsumerNo());
                                utilitiesBean.setProvider(taxDeductedCollectedUtilities.getProvider());
                                electricityUtilities.add(utilitiesBean);

                            } else*/
                            if (taxDeductedCollectedUtilities.getUtilityType().trim().equalsIgnoreCase("Telephone")) {
                                telephone += taxDeductedCollectedUtilities.getTaxDeducted();

                                TaxformTaxDeductedCollectedUtilitiesBean utilitiesBean = new TaxformTaxDeductedCollectedUtilitiesBean();
                                utilitiesBean.setId(taxDeductedCollectedUtilities.getId());
                                utilitiesBean.setUtilityType(taxDeductedCollectedUtilities.getUtilityType());
                                utilitiesBean.setTaxDeducted(taxDeductedCollectedUtilities.getTaxDeducted()+"");
                                utilitiesBean.setReferenceOrConsumerNo(taxDeductedCollectedUtilities.getReferenceOrConsumerNo());
                                utilitiesBean.setProvider(taxDeductedCollectedUtilities.getProvider());
                                telephoneUtilities.add(utilitiesBean);

                            } else if (taxDeductedCollectedUtilities.getUtilityType().trim().equalsIgnoreCase("Cellphone")) {
                                cellPhone += taxDeductedCollectedUtilities.getTaxDeducted();

                                TaxformTaxDeductedCollectedUtilitiesBean utilitiesBean = new TaxformTaxDeductedCollectedUtilitiesBean();
                                utilitiesBean.setId(taxDeductedCollectedUtilities.getId());
                                utilitiesBean.setUtilityType(taxDeductedCollectedUtilities.getUtilityType());
                                utilitiesBean.setTaxDeducted(taxDeductedCollectedUtilities.getTaxDeducted()+"");
                                utilitiesBean.setReferenceOrConsumerNo(taxDeductedCollectedUtilities.getReferenceOrConsumerNo());
                                utilitiesBean.setProvider(taxDeductedCollectedUtilities.getProvider());

                                cellphoneUtilities.add(utilitiesBean);

                            } else if (taxDeductedCollectedUtilities.getUtilityType().trim().equalsIgnoreCase("Internet")) {
                                internet += taxDeductedCollectedUtilities.getTaxDeducted();

                                TaxformTaxDeductedCollectedUtilitiesBean utilitiesBean = new TaxformTaxDeductedCollectedUtilitiesBean();
                                utilitiesBean.setId(taxDeductedCollectedUtilities.getId());
                                utilitiesBean.setUtilityType(taxDeductedCollectedUtilities.getUtilityType());
                                utilitiesBean.setTaxDeducted(taxDeductedCollectedUtilities.getTaxDeducted()+"");
                                utilitiesBean.setReferenceOrConsumerNo(taxDeductedCollectedUtilities.getReferenceOrConsumerNo());
                                utilitiesBean.setProvider(taxDeductedCollectedUtilities.getProvider());

                                internetUtilities.add(utilitiesBean);
                            }
                        }

                        /*TaxformFbrDetailedBean taxformFbrDetailedBeanForElectricity = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForElectricity.setTotalAmount(electricity + "");
                        taxformFbrDetailedBeanForElectricity.setList(electricityUtilities);
                        taxformFbrDetailedBeanMap.put("64140101", taxformFbrDetailedBeanForElectricity);*/

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForTelephone = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForTelephone.setTotalAmount(telephone + "");
                        taxformFbrDetailedBeanForTelephone.setList(telephoneUtilities);
                        taxformFbrDetailedBeanMap.put("64150001", taxformFbrDetailedBeanForTelephone);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForCellphone = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForCellphone.setTotalAmount(cellPhone + "");
                        taxformFbrDetailedBeanForCellphone.setList(cellphoneUtilities);
                        taxformFbrDetailedBeanMap.put("64150002", taxformFbrDetailedBeanForCellphone);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanForInternet = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanForInternet.setTotalAmount(internet + "");
                        taxformFbrDetailedBeanForInternet.setList(internetUtilities);
                        taxformFbrDetailedBeanMap.put("64150005", taxformFbrDetailedBeanForInternet);
                    }

                    if (taxformCalculationBean != null && taxformCalculationBean.getWealthStatementClosingWealth() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getWealthStatementClosingWealth() + "");

                        taxformFbrDetailedBeanMap.put("703001", taxformFbrDetailedBean);
                    }

                    if (taxformCalculationBean != null && taxformCalculationBean.getWealthStatementOpeningWealth() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getWealthStatementOpeningWealth() + "");

                        taxformFbrDetailedBeanMap.put("703002", taxformFbrDetailedBean);
                    }

                    if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense() != null) {

                        Double personalExpenseOtherThanGift = 0.0;

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRent() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRent();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRent() + "");

                            taxformFbrDetailedBeanMap.put("7051", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRatesTaxesChargeCess() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRatesTaxesChargeCess();

                            Double ratesTaxesChargesCess = taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getRatesTaxesChargeCess();
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            if (taxformCalculationBean != null && taxformCalculationBean.getWithholdingTax() != null) {
                                ratesTaxesChargesCess += Double.parseDouble(taxformCalculationBean.getWithholdingTax());
                                personalExpenseOtherThanGift += Double.parseDouble(taxformCalculationBean.getWithholdingTax());
                            }
                            taxformFbrDetailedBean.setTotalAmount(ratesTaxesChargesCess + "");

                            taxformFbrDetailedBeanMap.put("7052", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getVehicleRunningOrMaintenance() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getVehicleRunningOrMaintenance();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getVehicleRunningOrMaintenance() + "");

                            taxformFbrDetailedBeanMap.put("7055", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTravelling() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTravelling();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTravelling() + "");

                            taxformFbrDetailedBeanMap.put("7056", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getElectricity() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getElectricity();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getElectricity() + "");

                            taxformFbrDetailedBeanMap.put("7058", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getWater() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getWater();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getWater() + "");

                            taxformFbrDetailedBeanMap.put("7059", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getGas() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getGas();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getGas() + "");

                            taxformFbrDetailedBeanMap.put("7060", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTelephone() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTelephone();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getTelephone() + "");

                            taxformFbrDetailedBeanMap.put("7061", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getMedical() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getMedical();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getMedical() + "");

                            taxformFbrDetailedBeanMap.put("7070", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getEducational() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getEducational();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getEducational() + "");

                            taxformFbrDetailedBeanMap.put("7071", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getClub() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getClub();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getClub() + "");

                            taxformFbrDetailedBeanMap.put("7072", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getFunctionsOrGatherings() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getFunctionsOrGatherings();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getFunctionsOrGatherings() + "");

                            taxformFbrDetailedBeanMap.put("7073", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getDonationZakatAnnuityProfitOnDebutEtc() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getDonationZakatAnnuityProfitOnDebutEtc();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getDonationZakatAnnuityProfitOnDebutEtc() + "");

                            taxformFbrDetailedBeanMap.put("7076", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getOtherPersonalOrHouseholdExpense() != null) {

                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getOtherPersonalOrHouseholdExpense();

                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getOtherPersonalOrHouseholdExpense() + "");

                            taxformFbrDetailedBeanMap.put("7087", taxformFbrDetailedBean);
                        }

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getAssetsInsuranceOrSecurity() != null) {
                            personalExpenseOtherThanGift += taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getAssetsInsuranceOrSecurity();
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanPersonalExpenseOtherThanGift = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanPersonalExpenseOtherThanGift.setTotalAmount(personalExpenseOtherThanGift + "");

                        taxformFbrDetailedBeanMap.put("7089", taxformFbrDetailedBeanPersonalExpenseOtherThanGift);

                        if (taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getGift() != null) {
                            TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                            taxformFbrDetailedBean.setTotalAmount(taxform.getTaxformWelthStatementDetailsOfPersonalExpense().getGift() + "");

                            taxformFbrDetailedBeanMap.put("7091", taxformFbrDetailedBean);
                        }
                    }

                    if (taxformCalculationBean != null && taxformCalculationBean.getWealthStatementTaxableIncome() != null) {
                        TaxformFbrDetailedBean taxformFbrDetailedBean = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBean.setTotalAmount(taxformCalculationBean.getWealthStatementTaxableIncome() + "");

                        taxformFbrDetailedBeanMap.put("7049", taxformFbrDetailedBean);
                    }

                    if (taxform.getTaxformWelthStatementPropertyDetailList() != null) {
                        Double agricultural = 0.0;
                        Double other = 0.0;
                        List<TaxformWealthStatementPropertyDetailsBean> agricultureList = new ArrayList<TaxformWealthStatementPropertyDetailsBean>() ;
                        List<TaxformWealthStatementPropertyDetailsBean> otherList = new ArrayList<TaxformWealthStatementPropertyDetailsBean>() ;

                        for (Taxform_WelthStatement_PropertyDetail taxformWelthStatementPropertyDetail : taxform.getTaxformWelthStatementPropertyDetailList()) {
                            if (taxformWelthStatementPropertyDetail.getPropertyType().trim().equalsIgnoreCase("Agriculture")) {
                                if (taxformWelthStatementPropertyDetail.getPropertyCost() != null) {
                                    agricultural += taxformWelthStatementPropertyDetail.getPropertyCost();
                                    TaxformWealthStatementPropertyDetailsBean agricultureBean = new TaxformWealthStatementPropertyDetailsBean();
                                    agricultureBean.setId(taxformWelthStatementPropertyDetail.getId());
                                    agricultureBean.setPropertyType(taxformWelthStatementPropertyDetail.getPropertyType());
                                    agricultureBean.setUnitNo(taxformWelthStatementPropertyDetail.getUnitNo());
                                    agricultureBean.setAreaLocalityRoad(taxformWelthStatementPropertyDetail.getAreaLocalityRoad());
                                    agricultureBean.setCity(taxformWelthStatementPropertyDetail.getCity());
                                    agricultureBean.setArea(taxformWelthStatementPropertyDetail.getArea());
                                    agricultureBean.setPropertyCost(taxformWelthStatementPropertyDetail.getPropertyCost()+"");
                                    agricultureList.add(agricultureBean);
                                }
                            } else {
                                if (taxformWelthStatementPropertyDetail.getPropertyCost() != null) {
                                    other += taxformWelthStatementPropertyDetail.getPropertyCost();

                                    TaxformWealthStatementPropertyDetailsBean agricultureBean = new TaxformWealthStatementPropertyDetailsBean();
                                    agricultureBean.setId(taxformWelthStatementPropertyDetail.getId());
                                    agricultureBean.setPropertyType(taxformWelthStatementPropertyDetail.getPropertyType());
                                    agricultureBean.setUnitNo(taxformWelthStatementPropertyDetail.getUnitNo());
                                    agricultureBean.setAreaLocalityRoad(taxformWelthStatementPropertyDetail.getAreaLocalityRoad());
                                    agricultureBean.setCity(taxformWelthStatementPropertyDetail.getCity());
                                    agricultureBean.setArea(taxformWelthStatementPropertyDetail.getArea());
                                    agricultureBean.setPropertyCost(taxformWelthStatementPropertyDetail.getPropertyCost()+"");
                                    otherList.add(agricultureBean);
                                }
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanAgricultural = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanAgricultural.setTotalAmount(agricultural + "");
                        taxformFbrDetailedBeanAgricultural.setList(agricultureList);
                        taxformFbrDetailedBeanMap.put("7001", taxformFbrDetailedBeanAgricultural);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanOther = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanOther.setTotalAmount(other + "");
                        taxformFbrDetailedBeanOther.setList(otherList);
                        taxformFbrDetailedBeanMap.put("7002", taxformFbrDetailedBeanOther);
                    }

                    if (taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList() != null) {
                        Double cost = 0.0;

                        List<TaxformWealthStatementBankAccountsOrInvestmentBean> costlist = new ArrayList<TaxformWealthStatementBankAccountsOrInvestmentBean>();
                        for (Taxform_WelthStatement_BankAccountsOrInvestments taxformWelthStatementBankAccountsOrInvestments : taxform.getTaxformWelthStatementBankAccountsOrInvestmentsList()) {
                            if (taxformWelthStatementBankAccountsOrInvestments.getCost() != null) {
                                cost += taxformWelthStatementBankAccountsOrInvestments.getCost();
                                TaxformWealthStatementBankAccountsOrInvestmentBean costBean = new TaxformWealthStatementBankAccountsOrInvestmentBean();
                                costBean.setId(taxformWelthStatementBankAccountsOrInvestments.getId());
                                costBean.setForm(taxformWelthStatementBankAccountsOrInvestments.getForm());
                                costBean.setAccountOrInstructionNo(taxformWelthStatementBankAccountsOrInvestments.getAccountOrInstructionNo());
                                costBean.setInstitutionNameOrInduvidualCnic(taxformWelthStatementBankAccountsOrInvestments.getInstitutionNameOrInduvidualCnic());
                                costBean.setBranchName(taxformWelthStatementBankAccountsOrInvestments.getBranchName());
                                costBean.setCost(taxformWelthStatementBankAccountsOrInvestments.getCost()+"");

                                costlist.add(costBean);
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanCost = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanCost.setTotalAmount(cost + "");
                        taxformFbrDetailedBeanCost.setList(costlist);
                        taxformFbrDetailedBeanMap.put("7006", taxformFbrDetailedBeanCost);
                    }

                    if (taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList() != null) {
                        Double cash = 0.0;
                        Double employeeContribution = 0.0;
                        Double other = 0.0;

                        List<TaxformWealthStatementOtherReceivablesOrAssetsBean> cashList = new ArrayList<TaxformWealthStatementOtherReceivablesOrAssetsBean>();
                        List<TaxformWealthStatementOtherReceivablesOrAssetsBean> fundList = new ArrayList<TaxformWealthStatementOtherReceivablesOrAssetsBean>();
                        List<TaxformWealthStatementOtherReceivablesOrAssetsBean> otherList = new ArrayList<TaxformWealthStatementOtherReceivablesOrAssetsBean>();

                        for (Taxform_WelthStatement_OtherReceivablesOrAssets taxformWelthStatementOtherReceivablesOrAssets : taxform.getTaxformWelthStatementOtherReceivablesOrAssetsList()) {

                            if (taxformWelthStatementOtherReceivablesOrAssets.getForm().trim().equalsIgnoreCase("Cash")) {
                                if (taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() != null) {
                                    cash += taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost();

                                    TaxformWealthStatementOtherReceivablesOrAssetsBean cashBean = new TaxformWealthStatementOtherReceivablesOrAssetsBean();
                                    cashBean.setId(taxformWelthStatementOtherReceivablesOrAssets.getId());
                                    cashBean.setForm(taxformWelthStatementOtherReceivablesOrAssets.getForm());
                                    cashBean.setInstitutionNameOrIndividualCnic(taxformWelthStatementOtherReceivablesOrAssets.getInstitutionNameOrIndividualCnic());
                                    cashBean.setValueAtCost(taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost()+"");
                                    cashList.add(cashBean);

                                }
                            } else if (taxformWelthStatementOtherReceivablesOrAssets.getForm().trim().equalsIgnoreCase("Employee Contribution to Provident Fund")) {
                                if (taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() != null) {
                                    employeeContribution += taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost();

                                    TaxformWealthStatementOtherReceivablesOrAssetsBean cashBean = new TaxformWealthStatementOtherReceivablesOrAssetsBean();
                                    cashBean.setId(taxformWelthStatementOtherReceivablesOrAssets.getId());
                                    cashBean.setForm(taxformWelthStatementOtherReceivablesOrAssets.getForm());
                                    cashBean.setInstitutionNameOrIndividualCnic(taxformWelthStatementOtherReceivablesOrAssets.getInstitutionNameOrIndividualCnic());
                                    cashBean.setValueAtCost(taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost()+"");
                                    fundList.add(cashBean);
                                }
                            } else {
                                if (taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost() != null) {
                                    other += taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost();

                                    TaxformWealthStatementOtherReceivablesOrAssetsBean cashBean = new TaxformWealthStatementOtherReceivablesOrAssetsBean();
                                    cashBean.setId(taxformWelthStatementOtherReceivablesOrAssets.getId());
                                    cashBean.setForm(taxformWelthStatementOtherReceivablesOrAssets.getForm());
                                    cashBean.setInstitutionNameOrIndividualCnic(taxformWelthStatementOtherReceivablesOrAssets.getInstitutionNameOrIndividualCnic());
                                    cashBean.setValueAtCost(taxformWelthStatementOtherReceivablesOrAssets.getValueAtCost()+"");
                                    otherList.add(cashBean);
                                }
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanCash = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanCash.setTotalAmount(cash + "");
                        taxformFbrDetailedBeanCash.setList(cashList);
                        taxformFbrDetailedBeanMap.put("7012", taxformFbrDetailedBeanCash);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanEmployeeContribution = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanEmployeeContribution.setTotalAmount(employeeContribution + "");
                        taxformFbrDetailedBeanEmployeeContribution.setList(fundList);

                        taxformFbrDetailedBeanMap.put("7013", taxformFbrDetailedBeanEmployeeContribution);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanEmployeeOther = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanEmployeeOther.setTotalAmount(other + "");
                        taxformFbrDetailedBeanEmployeeOther.setList(otherList);

                        taxformFbrDetailedBeanMap.put("7007", taxformFbrDetailedBeanEmployeeOther);
                    }

                    //==================================Own Vehicle==================================================
                    if (taxform.getTaxformWelthStatementOwnVehicleList() != null) {
                        Double ownVehicle = 0.0;
                        List<TaxformWealthStatementOwnVehicleBean> taxformWelthStatementOwnVehicles = new ArrayList<TaxformWealthStatementOwnVehicleBean>();

                        for (Taxform_WelthStatement_OwnVehicle taxformWelthStatementOwnVehicle : taxform.getTaxformWelthStatementOwnVehicleList()) {
                            if (taxformWelthStatementOwnVehicle.getValueAtCost() != null) {
                                ownVehicle += taxformWelthStatementOwnVehicle.getValueAtCost();

                                TaxformWealthStatementOwnVehicleBean taxformWealthStatementOwnVehicleBean = new TaxformWealthStatementOwnVehicleBean();

                                taxformWealthStatementOwnVehicleBean.setForm(taxformWelthStatementOwnVehicle.getForm());
                                taxformWealthStatementOwnVehicleBean.setEtdRegistrationNo(taxformWelthStatementOwnVehicle.getEtdRegistrationNo());
                                taxformWealthStatementOwnVehicleBean.setMaker(taxformWelthStatementOwnVehicle.getMaker());
                                taxformWealthStatementOwnVehicleBean.setCapacity(taxformWelthStatementOwnVehicle.getCapacity());
                                taxformWealthStatementOwnVehicleBean.setValueAtCost(taxformWelthStatementOwnVehicle.getValueAtCost() + "");

                                taxformWelthStatementOwnVehicles.add(taxformWealthStatementOwnVehicleBean);
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanOwnVehicle = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanOwnVehicle.setTotalAmount(ownVehicle + "");
                        taxformFbrDetailedBeanOwnVehicle.setList(taxformWelthStatementOwnVehicles);

                        taxformFbrDetailedBeanMap.put("7008", taxformFbrDetailedBeanOwnVehicle);
                    }


                    //==================================Possession==================================================
                    if(taxform.getTaxformWelthStatementOtherPossessionsList() != null) {
                        Double preciousPossession = 0.0;
                        Double householdEffect = 0.0;
                        Double personalItem = 0.0;

                        List<TaxformWealthStatementOtherPossessionsBean> preciousPossessionList = new ArrayList<TaxformWealthStatementOtherPossessionsBean>();
                        List<TaxformWealthStatementOtherPossessionsBean> householdEffectList = new ArrayList<TaxformWealthStatementOtherPossessionsBean>();
                        List<TaxformWealthStatementOtherPossessionsBean> personalItemList = new ArrayList<TaxformWealthStatementOtherPossessionsBean>();

                        for (Taxform_WelthStatement_OtherPossessions taxform_welthStatement_otherPossessions : taxform.getTaxformWelthStatementOtherPossessionsList()){
                            if (taxform_welthStatement_otherPossessions.getPossessionType() != null && taxform_welthStatement_otherPossessions.getPossessionType().trim().equalsIgnoreCase("Precious Possession")) {

                                if (taxform_welthStatement_otherPossessions.getValueAtCost() != null) {
                                    preciousPossession += taxform_welthStatement_otherPossessions.getValueAtCost();

                                    TaxformWealthStatementOtherPossessionsBean taxformWealthStatementOtherPossessionsBean = new TaxformWealthStatementOtherPossessionsBean();

                                    taxformWealthStatementOtherPossessionsBean.setId(taxform_welthStatement_otherPossessions.getId());
                                    taxformWealthStatementOtherPossessionsBean.setPossessionType(taxform_welthStatement_otherPossessions.getPossessionType());
                                    taxformWealthStatementOtherPossessionsBean.setDescription(taxform_welthStatement_otherPossessions.getDescription());
                                    taxformWealthStatementOtherPossessionsBean.setValueAtCost(taxform_welthStatement_otherPossessions.getValueAtCost() + "");
                                    preciousPossessionList.add(taxformWealthStatementOtherPossessionsBean);
                                }
                            }
                             else if (taxform_welthStatement_otherPossessions.getPossessionType() != null && taxform_welthStatement_otherPossessions.getPossessionType().trim().equalsIgnoreCase("Household Effect")) {

                                if (taxform_welthStatement_otherPossessions.getValueAtCost() != null) {
                                    householdEffect += taxform_welthStatement_otherPossessions.getValueAtCost();

                                    TaxformWealthStatementOtherPossessionsBean taxformWealthStatementOtherPossessionsBean = new TaxformWealthStatementOtherPossessionsBean();

                                    taxformWealthStatementOtherPossessionsBean.setId(taxform_welthStatement_otherPossessions.getId());
                                    taxformWealthStatementOtherPossessionsBean.setPossessionType(taxform_welthStatement_otherPossessions.getPossessionType());
                                    taxformWealthStatementOtherPossessionsBean.setDescription(taxform_welthStatement_otherPossessions.getDescription());
                                    taxformWealthStatementOtherPossessionsBean.setValueAtCost(taxform_welthStatement_otherPossessions.getValueAtCost() + "");
                                    householdEffectList.add(taxformWealthStatementOtherPossessionsBean);
                                }
                            }
                            else if (taxform_welthStatement_otherPossessions.getPossessionType() != null && taxform_welthStatement_otherPossessions.getPossessionType().trim().equalsIgnoreCase("Personal Item")) {

                                if (taxform_welthStatement_otherPossessions.getValueAtCost() != null) {
                                    personalItem += taxform_welthStatement_otherPossessions.getValueAtCost();

                                    TaxformWealthStatementOtherPossessionsBean taxformWealthStatementOtherPossessionsBean = new TaxformWealthStatementOtherPossessionsBean();

                                    taxformWealthStatementOtherPossessionsBean.setId(taxform_welthStatement_otherPossessions.getId());
                                    taxformWealthStatementOtherPossessionsBean.setPossessionType(taxform_welthStatement_otherPossessions.getPossessionType());
                                    taxformWealthStatementOtherPossessionsBean.setDescription(taxform_welthStatement_otherPossessions.getDescription());
                                    taxformWealthStatementOtherPossessionsBean.setValueAtCost(taxform_welthStatement_otherPossessions.getValueAtCost() + "");
                                    personalItemList.add(taxformWealthStatementOtherPossessionsBean);
                                }
                            }

                        } // for

                        TaxformFbrDetailedBean taxformFbrDetailedBeanPreciousPossession = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanPreciousPossession.setTotalAmount(preciousPossession + "");
                        taxformFbrDetailedBeanPreciousPossession.setList(preciousPossessionList);
                        taxformFbrDetailedBeanMap.put("7009", taxformFbrDetailedBeanPreciousPossession);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanHouseHoldEffect = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanHouseHoldEffect.setTotalAmount(householdEffect + "");
                        taxformFbrDetailedBeanHouseHoldEffect.setList(householdEffectList);
                        taxformFbrDetailedBeanMap.put("7010", taxformFbrDetailedBeanHouseHoldEffect);

                        TaxformFbrDetailedBean taxformFbrDetailedBeanPersonalItem = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanPersonalItem.setTotalAmount(personalItem + "");
                        taxformFbrDetailedBeanPersonalItem.setList(personalItemList);
                        taxformFbrDetailedBeanMap.put("7011", taxformFbrDetailedBeanPersonalItem);


                    }// Possession if

                    if (taxform.getTaxformWelthStatementAssetsOutSidePakistanList() != null) {
                        Double valueAtCost = 0.0;

                        List<TaxformWealthStatementAssetsOutsidePakistanBean> outsidePakistanList = new ArrayList<TaxformWealthStatementAssetsOutsidePakistanBean>();

                        for (Taxform_WelthStatement_AssetsOutSidePakistan taxformWelthStatementAssetsOutSidePakistan : taxform.getTaxformWelthStatementAssetsOutSidePakistanList()) {
                            if (taxformWelthStatementAssetsOutSidePakistan.getValueAtCost() != null) {
                                valueAtCost += taxformWelthStatementAssetsOutSidePakistan.getValueAtCost();
                                TaxformWealthStatementAssetsOutsidePakistanBean outsidePakistanBean = new TaxformWealthStatementAssetsOutsidePakistanBean();
                                outsidePakistanBean.setId(taxformWelthStatementAssetsOutSidePakistan.getId());
                                outsidePakistanBean.setValueAtCost(taxformWelthStatementAssetsOutSidePakistan.getValueAtCost()+"");
                                outsidePakistanBean.setDescription(taxformWelthStatementAssetsOutSidePakistan.getDescription());
                                outsidePakistanList.add(outsidePakistanBean);
                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanValueAtCost = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanValueAtCost.setTotalAmount(valueAtCost + "");
                        taxformFbrDetailedBeanValueAtCost.setList(outsidePakistanList);
                        taxformFbrDetailedBeanMap.put("7016", taxformFbrDetailedBeanValueAtCost);
                    }

                    if (taxform.getTaxformWelthStatementOweAnyLoansOrCreditList() != null) {
                        Double valueAtCost = 0.0;

                        List<TaxformWealthStatementOweAnyLoanOrCreditBean> valueAtCostList = new ArrayList<TaxformWealthStatementOweAnyLoanOrCreditBean>();

                        for (Taxform_WelthStatement_OweAnyLoansOrCredit taxformWelthStatementOweAnyLoansOrCredit : taxform.getTaxformWelthStatementOweAnyLoansOrCreditList()) {
                            if (taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost() != null) {
                                valueAtCost += taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost();
                                TaxformWealthStatementOweAnyLoanOrCreditBean taxformWealthStatementOweAnyLoanOrCreditBean = new TaxformWealthStatementOweAnyLoanOrCreditBean();
                                taxformWealthStatementOweAnyLoanOrCreditBean.setId(taxformWelthStatementOweAnyLoansOrCredit.getId());
                                taxformWealthStatementOweAnyLoanOrCreditBean.setValueAtCost(taxformWelthStatementOweAnyLoansOrCredit.getValueAtCost()+"");
                                taxformWealthStatementOweAnyLoanOrCreditBean.setCreatorsName(taxformWelthStatementOweAnyLoansOrCredit.getCreatorsName());
                                taxformWealthStatementOweAnyLoanOrCreditBean.setForm(taxformWelthStatementOweAnyLoansOrCredit.getForm());
                                taxformWealthStatementOweAnyLoanOrCreditBean.setCreatorsNtnOrCnic(taxformWelthStatementOweAnyLoansOrCredit.getCreatorsNtnOrCnic());
                                valueAtCostList.add(taxformWealthStatementOweAnyLoanOrCreditBean);

                            }
                        }

                        TaxformFbrDetailedBean taxformFbrDetailedBeanValueAtCost = new TaxformFbrDetailedBean();
                        taxformFbrDetailedBeanValueAtCost.setTotalAmount(valueAtCost + "");
                        taxformFbrDetailedBeanValueAtCost.setList(valueAtCostList);
                        taxformFbrDetailedBeanMap.put("7021", taxformFbrDetailedBeanValueAtCost);
                    }


                    statusBean.setResponseMap(taxformFbrDetailedBeanMap);

                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                }

            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " , e);
                return new ResponseEntity<>(new StatusBean(0, "Exception"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/updateStatus", produces = "application/json", method = RequestMethod.POST)
    public Status updateStatus(@RequestBody InputBean inputBean) {

        if (inputBean != null && inputBean.getTaxformId() != null && inputBean.getUserId() != null && inputBean.getTaxformStatusId() != null) {
            try {
                /*User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(inputBean.getUserId(), UserStatus.ACTIVE);
                Taxform taxform = taxformServices.findOne(inputBean.getTaxformId());
                Taxform_Status taxform_status = taxform_status_repository.findById(inputBean.getTaxformStatusId()).orElse(null);
                String status = "";
                if (user != null && taxform != null && taxform_status != null) {
                    taxform.setStatus(taxform_status);
                    /*status = CommonUtil.sendMail(taxform);
                    MyPrint.println(":::if:::::::::::::::::sending email :::::::::::"+status);*/
                    taxformServices.updateTaxform(taxform);
                    return new Status(1, " update");
                } else {
                    /*MyPrint.println(":::::::else:::::::::::::sending email :::::::::::"+status);*/
                    return new Status(0, " not update");
                }
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                e.printStackTrace();
            }
        }
        return new Status(0, "not update");
    }

    //======================================verifyCorporateEmployee============================================
    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/verifyCorporateEmployee", produces = "application/json", method = RequestMethod.POST)
    public Status verifyCorporateEmployee(@RequestBody TaxformBean taxformBean) {
        if (taxformBean != null && taxformBean.getUserId() != null && taxformBean.getTaxformId() != null && taxformBean.getVerifyCorporateEmpoyee() != null) {
            try {
                Taxform taxform = taxformServices.findOne(taxformBean.getTaxformId());
                /*User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(taxformBean.getUserId(), UserStatus.ACTIVE);
                if(taxform !=null && user != null) {
                    if (taxformBean.getVerifyCorporateEmpoyee()) {
                        taxform.setVerifyCorporateEmployee(1);
                        //taxform.setStatus(taxformServices.findTaxfromStatusById(2));
                        taxform.setStatus(statusServices.findOneByFBRStatus());
                    }
                    else {
                        taxform.setVerifyCorporateEmployee(0);
                        //taxform.setStatus(taxformServices.findTaxfromStatusById(2));
                        taxform.setStatus(statusServices.findOneByFBRStatus());
                    }
                    taxformServices.updateTaxform(taxform);
                    return new Status(1, "success");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Status(0, "fail");
    }
}