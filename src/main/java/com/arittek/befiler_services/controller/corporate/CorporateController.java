package com.arittek.befiler_services.controller.corporate;

import com.arittek.befiler_services.beans.CorporateBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.settings.corporate.Category;
import com.arittek.befiler_services.model.user.Corporate;
import com.arittek.befiler_services.model.user.CorporateEmployee;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.services.*;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.setting.corporate.CategoryServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/corporate")
public class CorporateController {

    private CorporateServices corporateServices;
    private UsersServices usersServices;
    private CategoryServices corporateCategoryServices;
    private CorporateEmployeeServices corporateEmployeeServices;
    private PaymentServices paymentServices;

    @Autowired
    public CorporateController(CorporateServices corporateServices, UsersServices usersServices, CategoryServices corporateCategoryServices, CorporateEmployeeServices corporateEmployeeServices, PaymentServices paymentServices) {
        this.corporateServices = corporateServices;
        this.usersServices = usersServices;
        this.corporateCategoryServices = corporateCategoryServices;
        this.corporateEmployeeServices = corporateEmployeeServices;
        this.paymentServices = paymentServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> saveOrUpdateCorporate(@RequestBody CorporateBean corporateBean) throws Exception {
        if (corporateBean != null) {
            try {
                User authorizer;
                if (corporateBean.getUserId() != null) {
                    /*authorizer = usersServices.findOneByIdAndStatus(corporateBean.getUserId(), usersServices.findUserStatusById(1));*/
                    authorizer = usersServices.findOneByIdAndStatus(corporateBean.getUserId(), UserStatus.ACTIVE);
                    if (authorizer == null) {
                        return new ResponseEntity<Status>(new Status(0, "Session expired"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<Status>(new Status(0, "Session expired"), HttpStatus.OK);
                }

                Corporate corporate;
                if (corporateBean.getId() != null) {
                    corporate = corporateServices.findOneByIdAndStatus(corporateBean.getId(), AppStatus.ACTIVE);
                    if (corporate == null) {
                        corporate = new Corporate();
                    }
                } else {
                    corporate = new Corporate();
                }

                if(corporateBean.getCorporateCategoryId() != null){
                    Category corporateCategory = corporateCategoryServices.findOne(corporateBean.getCorporateCategoryId());
                    if(corporateCategory != null){
                        corporate.setCorporateCategory(corporateCategory);
                    }
                    else {
                        return new ResponseEntity<>(new Status(0, "Please Select Category"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Please Select Category"), HttpStatus.OK);
                }

                corporate.setCorporateInfo(corporateBean.getCorporateInfo());
                corporate.setCorporateLogo(corporateBean.getCorporateLogo());
                corporate.setCorporateName(corporateBean.getCorporateName());
                corporate.setNtnCnic(corporateBean.getNtnCnic());
                corporate.setCorporateLandLineNo(corporateBean.getCorporateLandLine());
                corporate.setCorporateContactNo(corporateBean.getCorporateContact());
                corporate.setWebAddress(corporateBean.getWebAddress());
                corporate.setCorporateAddress(corporateBean.getCorporateAddress());
                corporate.setBenificiaryName(corporateBean.getBenificiaryName());
                corporate.setPersonContactNo(corporateBean.getPersonContact());
                corporate.setPersonLandLineNo(corporateBean.getPersonLandLine());
                corporate.setDesignation(corporateBean.getDesignation());
                corporate.setEmailAddress(corporateBean.getEmailAddress());
                corporate.setStatus(AppStatus.ACTIVE);

                corporateServices.saveOrUpdate(corporate);

                return new ResponseEntity<>(new Status(1, "Record updated successfully"), HttpStatus.OK);
            } catch (Exception e) {
                Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
                Logger4j.getLogger().error("Exception : " + e);
                return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<StatusBean> getListCorporate() {
        try {
            List<Corporate> corporateList = corporateServices.findAllByStatus(AppStatus.ACTIVE);
            if (corporateList != null) {

                List<CorporateBean> corporateBeanList = new ArrayList<CorporateBean>();
                for (Corporate corporate : corporateList) {
                    double totalAmount = 0;

                    List<CorporateEmployee> corporateEmployeeList = corporateEmployeeServices.findAllByCorporateAndStatus(corporate, AppStatus.ACTIVE);
                    if (corporateEmployeeList != null) {
                        for (CorporateEmployee corporateEmployee : corporateEmployeeList) {

                            List<Payment> paymentList = paymentServices.findAllByUser(corporateEmployee.getUser());
                            for (Payment payment : paymentList) {
                                totalAmount += payment.getSettingPayment().getAmount();
                            }
                        }
                    }

                    System.out.println("total amount corporate: "+totalAmount);
                    CorporateBean bean = new CorporateBean();
                    bean.setTotalAmount(totalAmount);
                    bean.setCorporateName(corporate.getCorporateName());
                    bean.setNtnCnic(corporate.getNtnCnic());
                    bean.setCorporateLandLine(corporate.getCorporateLandLineNo());
                    bean.setCorporateContact(corporate.getCorporateContactNo());
                    bean.setWebAddress(corporate.getWebAddress());
                    bean.setCorporateAddress(corporate.getCorporateAddress());
                    bean.setBenificiaryName(corporate.getBenificiaryName());
                    bean.setPersonContact(corporate.getPersonContactNo());
                    bean.setPersonLandLine(corporate.getPersonLandLineNo());
                    bean.setDesignation(corporate.getDesignation());
                    bean.setEmailAddress(corporate.getEmailAddress());

                    corporateBeanList.add(bean);
                }

                StatusBean statusBean = new StatusBean(1, "Successfully");
                statusBean.setResponse(corporateBeanList);

                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete data"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getByUser", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<StatusBean> getCorporateByUser(@RequestBody CorporateBean corporateBean) throws Exception {
        try {
            if (corporateBean != null && corporateBean.getUserId() != null) {
                /*User user = usersServices.findOneByIdAndStatus(corporateBean.getUserId(), usersServices.findUserStatusById(1));*/
                User user = usersServices.findOneByIdAndStatus(corporateBean.getUserId(), UserStatus.ACTIVE);
                if (user != null && user.getCnicNo() != null) {
                    CorporateEmployee corporateEmployee = corporateEmployeeServices.findCorporateEmployeeByCnicNo(user.getCnicNo());
                    try {
                        if (corporateEmployee != null && corporateEmployee.getCorporate() != null) {
                            Corporate corporate = corporateEmployee.getCorporate();
                            if (corporate != null) {
                                CorporateBean bean = new CorporateBean();
                                bean.setCorporateName(corporate.getCorporateName());
                                bean.setNtnCnic(corporate.getNtnCnic());
                                bean.setCorporateCategoryId(corporate.getCorporateCategory().getId());
                                bean.setCorporateLandLine(corporate.getCorporateLandLineNo());
                                bean.setCorporateContact(corporate.getCorporateContactNo());
                                bean.setWebAddress(corporate.getWebAddress());
                                bean.setCorporateAddress(corporate.getCorporateAddress());
                                bean.setBenificiaryName(corporate.getBenificiaryName());
                                bean.setPersonContact(corporate.getPersonContactNo());
                                bean.setPersonLandLine(corporate.getPersonLandLineNo());
                                bean.setDesignation(corporate.getDesignation());
                                bean.setEmailAddress(corporate.getEmailAddress());

                                bean.setId(corporate.getId());
                                if (corporate.getStatus() != null) {
                                    bean.setStatus(corporate.getStatus().getId());
                                }
                                bean.setUserId(user.getId());

                                List<CorporateBean> corporateBeanList = new ArrayList<CorporateBean>();
                                corporateBeanList.add(bean);
                                StatusBean status = new StatusBean(1, "Successfully");
                                status.setResponse(corporateBeanList);

                                return new ResponseEntity<>(status, HttpStatus.OK);
                            }
                        } else {
                            return new ResponseEntity<>(new StatusBean(0, "Invalid Corporate "), HttpStatus.OK);
                        }
                    }catch (Exception e){e.printStackTrace();}
                } else {
                    return new ResponseEntity<>(new StatusBean(0, "Invalid user"), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }


/*
/ corporateId -->corporateEmployee --> userId --> taxforms
    @RequestMapping(value = "/getTaxforms", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<?> findTaxform(@RequestBody CorporateBean corporateBean) {
        if (corporateBean != null && corporateBean.getId() != null) {
            Corporate corporate1 = corporateServices.findOne(corporateBean.getId());

            if (corporate1 != null) {
                StatusBean statusBean = new StatusBean(1, "Successfully");
                List<TaxformBean> listTaxform = new ArrayList<>();
                List<CorporateEmployee> listOfCorporateEmployee = corporateEmployeeRepository.findAllByCorporateId(corporate1.getId());

                for (CorporateEmployee corporateEmployee : listOfCorporateEmployee) {
                    if (corporateEmployee.getUser() != null) {
                        try {
                            List<Taxform> listOfTaxform = taxformServices.findAllByUserId(corporateEmployee.getUser().getId());

                            for (Taxform taxform : listOfTaxform) {
                                TaxformBean taxformBean = new TaxformBean();
                                taxformBean.setEmail(taxform.getEmail());
                                taxformBean.setCnic(taxform.getCnic());
                                taxformBean.setStatusId(taxform.getStatus().getId());
                                taxformBean.setYear(taxform.getYear());
                                listTaxform.add(taxformBean);
                            }

                            statusBean.setResponse(listTaxform);
                            return new ResponseEntity<StatusBean>(statusBean, HttpStatus.OK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } // user
                } // for each loop for corporateEmployee
            }
            return new ResponseEntity<StatusBean>(new StatusBean(0, "Corporate not found!"), HttpStatus.OK);
        }

        return new ResponseEntity<StatusBean>(new StatusBean(0, "Incomplete Data!"), HttpStatus.OK);
    }




    //  upload service

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/uploadCorporateEmployeeList", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<List<CorporateEmployeeBean>> uploadCorporateEmployeeList(@RequestBody CorporateEmployeeBean corporateEmployeeBean) throws Exception {
        List<CorporateEmployeeBean> list = new ArrayList<CorporateEmployeeBean>();
        XSSFRow row;
        if (corporateEmployeeBean != null) {
            if (corporateEmployeeBean.getUserId() != null && corporateEmployeeBean.getCorporateId() != null) {
                File file = new File("D:\\CorporateEmployee.xlsx");
                if (file.isFile() && file.exists()) {
                    FileInputStream in = new FileInputStream(file);
                    XSSFWorkbook workbook = new XSSFWorkbook(in);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    if (sheet != null) {
                        Iterator<Row> rowIterator = sheet.iterator();
                        while (rowIterator.hasNext()) {
                            row = (XSSFRow) rowIterator.next();
                            // if(!row.getCell(3).equals(null)){}
                            // read from execl file  2007 or later and save into database


CorporateEmployee corporateEmployee = new CorporateEmployee();
                            corporateEmployee.setFirstName(""+row.getCell(0));
                            corporateEmployee.setLastName(""+row.getCell(1));
                            corporateEmployee.setEmailAddress(""+row.getCell(2));
                            corporateEmployee.setCnicNo(""+row.getCell(3));
                            // corporateEmployeeBean1.setCurrentDate((Timestamp) row.getCell(4).getDateCellValue());
                            MyPrint.println(""+row.getCell(0)+"\t"+row.getCell(1)+"\t"+row.getCell(2)+"\t"+row.getCell(3)+"\t"+row.getCell(4));
                            corporateEmployeeRepository.save(corporateEmployee);

                            CorporateEmployeeBean bean = new CorporateEmployeeBean();
                            bean.setFirstName("" + row.getCell(0));
                            bean.setLastName("" + row.getCell(1));
                            bean.setEmailAddress("" + row.getCell(2));
                            bean.setCnicNo("" + row.getCell(3));
                            list.add(bean);
                        }
                    } else {
                        MyPrint.println("data not found in this file");
                    }
                } else {
                    MyPrint.println("File not found !");
                }
            } else {
                MyPrint.println("UserId or CorporateId is null");
            }
        } // bean
        return new ResponseEntity<List<CorporateEmployeeBean>>(list, HttpStatus.OK);
    }*/
}
