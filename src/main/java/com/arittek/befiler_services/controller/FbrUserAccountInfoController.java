package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.FbrUserAccountInfoBean;
import com.arittek.befiler_services.beans.FbrUserAccountInfoDocumentsBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.model.enums.AppStatus;
import com.arittek.befiler_services.model.enums.FbrUserAccountInfoStatus;
import com.arittek.befiler_services.model.enums.ProductType;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfo;
import com.arittek.befiler_services.model.ntn.FbrUserAccountInfoDocuments;
import com.arittek.befiler_services.model.taxform.Taxform;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.settings.SettingPayment;
import com.arittek.befiler_services.services.payment.settings.SettingPaymentServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.TaxformStatusServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoDocumentsServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.util.Base64Util;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.MyPrint;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/fbrUserAccountInfo")
public class FbrUserAccountInfoController {

    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private TaxformServices taxformServices;
    private UsersServices usersServices;
    private TaxformStatusServices taxformStatusServices;
    private PaymentServices paymentServices;
    private FbrUserAccountInfoDocumentsServices fbrUserAccountInfoDocumentsServices;
    private PaymentCartServices paymentCartServices;
    private SettingPaymentServices settingPaymentServices;

    @Value("${befiler.url}")
    private String serverUrl;

    @Value("${static.content.path}")
    private String staticContentPath;

    @Value("${static.content.fbr.cnic.path}")
    private String staticContentFbrCnicPath;

    @Autowired
    FbrUserAccountInfoController(FbrUserAccountInfoServices fbrUserAccountInfoServices, TaxformServices taxformServices,UsersServices usersServices, TaxformStatusServices taxformStatusServices, FbrUserAccountInfoDocumentsServices fbrUserAccountInfoDocumentsServices, PaymentCartServices paymentCartServices, PaymentServices paymentServices, SettingPaymentServices settingPaymentServices){
        this.fbrUserAccountInfoServices=fbrUserAccountInfoServices;
        this.taxformServices=taxformServices;
        this.usersServices=usersServices;
        this.taxformStatusServices = taxformStatusServices;
        this.fbrUserAccountInfoDocumentsServices = fbrUserAccountInfoDocumentsServices;
        this.paymentCartServices = paymentCartServices;
        this.paymentServices = paymentServices;
        this.settingPaymentServices = settingPaymentServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> createFbrUserAccountInfo(@RequestBody FbrUserAccountInfoBean fbrUserAccountInfoBean) throws Exception {

        try {

            User user;
            if (fbrUserAccountInfoBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(fbrUserAccountInfoBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(fbrUserAccountInfoBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
            }

            if (fbrUserAccountInfoBean.getRegistredWithFbr() == null) {
                return new ResponseEntity<>(new Status(0,"Please select Yes/No"), HttpStatus.OK);
            }

            if (fbrUserAccountInfoBean.getRegistredWithFbr()) {

                /*if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrNtnNumber())) {
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: NTN Number"),HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrUsername())) {
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: Registration Number"),HttpStatus.OK);
                }*/

                if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPassword())) {
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Password"), HttpStatus.OK);
                }

                if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPin())) {
                    return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Pin"), HttpStatus.OK);
                }

                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo == null) {
                    fbrUserAccountInfo = new FbrUserAccountInfo();
                }

                /*fbrUserAccountInfo.setRegisteredWithFbr(1);*/
                if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() != FbrUserAccountInfoStatus.LAWYER_CLOSE) {
                    fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.REGISTERED);
                }

                /*fbrUserAccountInfo.setFbrNtnNumber(fbrUserAccountInfoBean.getFbrNtnNumber());
                fbrUserAccountInfo.setFbrUsername(fbrUserAccountInfoBean.getFbrUsername());*/
                fbrUserAccountInfo.setFbrPassword(fbrUserAccountInfoBean.getFbrPassword());
                fbrUserAccountInfo.setFbrPin(fbrUserAccountInfoBean.getFbrPin());
                fbrUserAccountInfo.setUser(user);
                fbrUserAccountInfoServices.saveOrUpdate(fbrUserAccountInfo);

                if (fbrUserAccountInfoBean.getTaxformId() != null) {
                    Taxform taxform = taxformServices.findOne(fbrUserAccountInfoBean.getTaxformId());
                    if (taxform != null) {

                        SettingPayment settingPayment = settingPaymentServices.findPaymentByYearForForTaxform(taxform.getTaxformYear());
                        if (settingPayment == null) {
                            return new ResponseEntity<>(new Status(0, "Taxform payment is not defined"), HttpStatus.OK);
                        }

                        taxform.setStatus(taxformStatusServices.findOneByPaymentStatus());
                        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
                        taxformServices.saveOrUpdate(taxform);

                        PaymentCart paymentCart = paymentCartServices.findOneByTaxformAndUser(taxform, user);
                        if (paymentCart == null) {
                            paymentCart = new PaymentCart();
                            paymentCart.setStatus(1);
                            paymentCart.setProductType(ProductType.TAXFORM);
                            paymentCart.setUser(user);
                            paymentCart.setTaxform(taxform);
                            paymentCart.setSettingPayment(settingPayment);

                            paymentCartServices.saveOrUpdateToActiveStatus(paymentCart);
                        }
                    }
                }
                return new ResponseEntity<>(new Status(1, "Successfully Saved"), HttpStatus.OK);

            } else {
                SettingPayment settingPayment = settingPaymentServices.findPaymentForNTNRegistration();
                /*SettingFbrPayment settingFbrPayment = settingFbrPaymentServices.findOneByStatus(AppStatus.ACTIVE);*/
                if (settingPayment == null) {
                    return new ResponseEntity<>(new Status(0, "NTN registration payment is not defined"), HttpStatus.OK);
                }

                FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
                if (fbrUserAccountInfo == null) {
                    fbrUserAccountInfo = new FbrUserAccountInfo();
                }
                /*fbrUserAccountInfo.setRegisteredWithFbr(0);*/
                fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.NOT_REGISTERED);
                fbrUserAccountInfo.setUser(user);
                fbrUserAccountInfoServices.saveOrUpdate(fbrUserAccountInfo);

                List<Integer> fbrUserAccountInfoDocumentsUpdatedList = new ArrayList<>();
                /*if (fbrUserAccountInfoBean.getProofOfResidence() != null && fbrUserAccountInfoBean.getProofOfResidence().size() > 0) {

                    *//*List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsListOld = fbrUserAccountInfoDocumentsServices.findAllActiveRecordsByUserAndDocumentType(user,"Residence");*//*
                    for (FbrUserAccountInfoDocumentsBean fbrUserAccountInfoBean1 : fbrUserAccountInfoBean.getProofOfResidence()) {

                        FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments = null;

                        if (fbrUserAccountInfoBean1 != null && fbrUserAccountInfoBean1.getId() != null) {
                            fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.findOneActiveRecordById(fbrUserAccountInfoBean1.getId());
                            if (fbrUserAccountInfoDocuments != null) {
                                fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                            }
                        }

                        if (fbrUserAccountInfoDocuments == null) {
                            if (fbrUserAccountInfoBean1.getFilename() != null && fbrUserAccountInfoBean1.getFiletype() != null && fbrUserAccountInfoBean1.getBase64() != null) {

                                fbrUserAccountInfoDocuments = new FbrUserAccountInfoDocuments();

                                fbrUserAccountInfoDocuments.setUser(user);
                                fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByActiveStatus());
                                fbrUserAccountInfoDocuments.setCurrentDate(CommonUtil.getCurrentTimestamp());

                                fbrUserAccountInfoDocuments.setDocumentType("Residence");
                                fbrUserAccountInfoDocuments.setDocumentFormat(fbrUserAccountInfoBean1.getFiletype());
                                fbrUserAccountInfoDocuments.setDocumentDescription(fbrUserAccountInfoBean1.getFilename());

                                fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);

                                if (fbrUserAccountInfoDocuments != null && fbrUserAccountInfoDocuments.getId() != null) {

                                    String fileFormat = fbrUserAccountInfoDocuments.getDocumentDescription().replace(".", "#").split("#")[1];
                                    String fileName = user.getId() + "_" + fbrUserAccountInfoDocuments.getId() + "." + fileFormat;
                                    String fileUrl= "C:/Befiler_Static_Content/FBR/Residence/" + fileName;
                                    byte[] imageByte= Base64.decodeBase64(fbrUserAccountInfoBean1.getBase64());
                                    BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                                    imageOutFile.write(imageByte);
                                    imageOutFile.close();

                                    fbrUserAccountInfoDocuments.setDocumentName("/FBR/Residence/" + fileName);

                                    fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);
                                }

                                fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                            }
                        }
                    }
                } else {
                    return new Status(0, "Please upload file");
                }*/

                /*if (fbrUserAccountInfoBean.getUtilityBillOfResidence() != null) {

                    *//*List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsListOld = fbrUserAccountInfoDocumentsServices.findAllActiveRecordsByUserAndDocumentType(user,"Bill");*//*


                    FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments = null;


                    if (fbrUserAccountInfoBean.getUtilityBillOfResidence().getId() != null) {
                        fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.findOneActiveRecordById(fbrUserAccountInfoBean.getUtilityBillOfResidence().getId());
                        if (fbrUserAccountInfoDocuments != null) {
                            fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                        }
                    }

                    if (fbrUserAccountInfoDocuments == null) {
                        if (fbrUserAccountInfoBean.getUtilityBillOfResidence().getFilename() != null && fbrUserAccountInfoBean.getUtilityBillOfResidence().getFiletype() != null && fbrUserAccountInfoBean.getUtilityBillOfResidence().getBase64() != null) {

                            fbrUserAccountInfoDocuments = new FbrUserAccountInfoDocuments();
                            fbrUserAccountInfoDocuments.setUser(user);
                            fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByActiveStatus());
                            fbrUserAccountInfoDocuments.setCurrentDate(CommonUtil.getCurrentTimestamp());

                            fbrUserAccountInfoDocuments.setDocumentType("Bill");
                            fbrUserAccountInfoDocuments.setDocumentFormat(fbrUserAccountInfoBean.getUtilityBillOfResidence().getFiletype());
                            fbrUserAccountInfoDocuments.setDocumentDescription(fbrUserAccountInfoBean.getUtilityBillOfResidence().getFilename());

                            fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);

                            if (fbrUserAccountInfoDocuments != null && fbrUserAccountInfoDocuments.getId() != null) {

                                String fileFormat = fbrUserAccountInfoDocuments.getDocumentDescription().replace(".", "#").split("#")[1];
                                String fileName = user.getId() + "_" + fbrUserAccountInfoDocuments.getId() + "." + fileFormat;
                                String fileUrl= "C:/Befiler_Static_Content/FBR/Bill/" + fileName;
                                byte[] imageByte= Base64.decodeBase64(fbrUserAccountInfoBean.getUtilityBillOfResidence().getBase64());
                            *//*boolean containVirus =  AntiVirusUtil.scanFile(imageByte,fileName);*//*


                                BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                                imageOutFile.write(imageByte);
                                imageOutFile.close();

                                fbrUserAccountInfoDocuments.setDocumentName("/FBR/Bill/" + fileName);

                                fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);
                            }

                            fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                        }
                    }
                    *//*if (fbrUserAccountInfoDocumentsListOld != null) {
                        for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsListOld) {
                            if (fbrUserAccountInfoDocuments != null) {
                                fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByDeletedStatus());
                                fbrUserAccountInfoDocuments.setCurrentDate(CommonUtil.getCurrentTimestamp());

                                fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);
                            }
                        }
                    }*//*
                } else {
                    return new Status(0, "Please upload file");
                }*/

                if (fbrUserAccountInfoBean.getCnicList() != null) {
                    for (FbrUserAccountInfoDocumentsBean fbrUserAccountInfoDocumentsBean : fbrUserAccountInfoBean.getCnicList()) {

                        FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments = null;

                        if (fbrUserAccountInfoDocumentsBean != null && fbrUserAccountInfoDocumentsBean.getId() != null) {
                            fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.findOneActiveRecordById(fbrUserAccountInfoDocumentsBean.getId());
                            if (fbrUserAccountInfoDocuments != null) {
                                fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                            }
                        }

                        if (fbrUserAccountInfoDocuments == null) {
                            if (fbrUserAccountInfoDocumentsBean.getFilename() != null && fbrUserAccountInfoDocumentsBean.getFiletype() != null && fbrUserAccountInfoDocumentsBean.getBase64() != null) {
                                fbrUserAccountInfoDocuments = new FbrUserAccountInfoDocuments();
                                fbrUserAccountInfoDocuments.setUser(user);
                                /*fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByActiveStatus());*/
                                fbrUserAccountInfoDocuments.setAppStatus(AppStatus.ACTIVE);

                                fbrUserAccountInfoDocuments.setDocumentType("Cnic");
                                fbrUserAccountInfoDocuments.setDocumentFormat(fbrUserAccountInfoDocumentsBean.getFiletype());
                                fbrUserAccountInfoDocuments.setDocumentDescription(fbrUserAccountInfoDocumentsBean.getFilename());

                                fbrUserAccountInfoDocuments = fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);

                                if (fbrUserAccountInfoDocuments != null && fbrUserAccountInfoDocuments.getId() != null) {

                                    /*String fileFormat = fbrUserAccountInfoDocuments.getDocumentDescription().replace(".", "#").split("#")[1];*/
                                    String fileFormat = FilenameUtils.getExtension(fbrUserAccountInfoDocuments.getDocumentDescription());
                                    String fileName = user.getId() + "_" + fbrUserAccountInfoDocuments.getId() + "." + fileFormat;
                                    String fileUrl= staticContentFbrCnicPath + fileName;
                                    byte[] imageByte= Base64.decodeBase64(fbrUserAccountInfoDocumentsBean.getBase64());
                                    BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                                    imageOutFile.write(imageByte);
                                    imageOutFile.close();

                                    fbrUserAccountInfoDocuments.setDocumentName("/FBR/Cnic/" + fileName);

                                    fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);
                                }
                                fbrUserAccountInfoDocumentsUpdatedList.add(fbrUserAccountInfoDocuments.getId());
                            }
                        }
                        /*if (fbrUserAccountInfoDocumentsListOld != null) {
                            for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsListOld) {
                                if (fbrUserAccountInfoDocuments != null) {
                                    fbrUserAccountInfoDocuments.setStatus(appStatusServices.findOneByDeletedStatus());
                                    fbrUserAccountInfoDocuments.setCurrentDate(CommonUtil.getCurrentTimestamp());

                                    fbrUserAccountInfoDocumentsServices.save(fbrUserAccountInfoDocuments);
                                }
                            }
                        }*/
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Please upload file"), HttpStatus.OK);
                }

                if (fbrUserAccountInfoDocumentsUpdatedList != null && fbrUserAccountInfoDocumentsUpdatedList.size() > 0) {
                    fbrUserAccountInfoDocumentsServices.deleteAllByUserNotIn(user, fbrUserAccountInfoDocumentsUpdatedList);
                }

                PaymentCart paymentCart = paymentCartServices.findOneByFbrUserAccountInfoAndUser(fbrUserAccountInfo, user);
                if (paymentCart == null) {
                    paymentCart = new PaymentCart();
                    paymentCart.setStatus(1);
                    paymentCart.setProductType(ProductType.NTN);
                    paymentCart.setUser(user);
                    paymentCart.setFbrUserAccountInfo(fbrUserAccountInfo);
                    /*paymentCart.setSettingFbrPayment(settingFbrPayment);*/
                    paymentCart.setSettingPayment(settingPayment);

                    paymentCartServices.saveOrUpdateToActiveStatus(paymentCart);
                } else if (paymentCart != null && paymentCart.getStatus() != null && paymentCart.getStatus() == 0) {
                    paymentCart.setStatus(1);
                    paymentCart.setProductType(ProductType.NTN);
                    paymentCart.setUser(user);
                    paymentCart.setFbrUserAccountInfo(fbrUserAccountInfo);
                    /*paymentCart.setSettingFbrPayment(settingFbrPayment);*/
                    paymentCart.setSettingPayment(settingPayment);

                    paymentCartServices.saveOrUpdateToActiveStatus(paymentCart);
                }


                if (fbrUserAccountInfoBean != null && fbrUserAccountInfoBean.getTaxformId() != null) {
                    Taxform taxform = taxformServices.findOne(fbrUserAccountInfoBean.getTaxformId());
                    if (taxform != null) {
                        taxform.setStatus(taxformStatusServices.findOneByPaymentStatus());
                        taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
                        taxformServices.saveOrUpdate(taxform);

                        /*SettingTaxformPayment settingTaxformPayment = settingTaxformPaymentServices.findOneByTaxformYearAndStatus(taxform.getTaxformYear(), AppStatus.ACTIVE);*/
                        SettingPayment settingPaymentForTaxform = settingPaymentServices.findPaymentByYearForForTaxform(taxform.getTaxformYear());
                        if (settingPaymentForTaxform == null) {
                            return new ResponseEntity<>(new Status(0, "Taxform payment is not defined"), HttpStatus.OK);
                        }

                        PaymentCart paymentCartForTaxform = paymentCartServices.findOneByTaxformAndUser(taxform, user);
                        if (paymentCartForTaxform == null) {
                            paymentCartForTaxform = new PaymentCart();
                            paymentCartForTaxform.setStatus(1);
                            /*paymentCartForTaxform.setProductType(1);*/
                            paymentCartForTaxform.setProductType(ProductType.TAXFORM);
                            paymentCartForTaxform.setUser(user);
                            paymentCartForTaxform.setTaxform(taxform);
                            paymentCartForTaxform.setSettingPayment(settingPaymentForTaxform);

                            paymentCartServices.saveOrUpdateToActiveStatus(paymentCartForTaxform);
                        }
                    }
                }
                return new ResponseEntity<>(new Status(1,"Successfully Saved"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0,"Incomplete Data."), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/registeredWithFbr",produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> registeredWithFbr(@RequestBody FbrUserAccountInfoBean fbrUserAccountInfoBean) throws Exception {

        try {

            /*if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrNtnNumber())) { return new ResponseEntity<>(new Status(0,"Incomplete Data :: NTN Number"),HttpStatus.OK); }*/
            /*if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrUsername())) { return new ResponseEntity<>(new Status(0,"Incomplete Data :: Registration Number"),HttpStatus.OK); }*/
            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPassword())) { return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Password"), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(fbrUserAccountInfoBean.getFbrPin())) { return new ResponseEntity<>(new Status(0,"Incomplete Data :: FBR Pin"), HttpStatus.OK); }

            User user;
            if (fbrUserAccountInfoBean.getUserId() != null) {
                /*user = usersServices.findOneByIdAndStatus(fbrUserAccountInfoBean.getUserId(), usersServices.findUserStatusById(1));*/
                user = usersServices.findOneByIdAndStatus(fbrUserAccountInfoBean.getUserId(), UserStatus.ACTIVE);
                if (user == null) {
                    return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new Status(0,"Session Expired."), HttpStatus.OK);
            }

            FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
            if (fbrUserAccountInfo == null) {
                fbrUserAccountInfo = new FbrUserAccountInfo();
            }

            /*fbrUserAccountInfo.setRegisteredWithFbr(1);*/
            fbrUserAccountInfo.setFbrUserAccountInfoStatus(FbrUserAccountInfoStatus.REGISTERED);
            /*fbrUserAccountInfo.setFbrNtnNumber(fbrUserAccountInfoBean.getFbrNtnNumber());
            fbrUserAccountInfo.setFbrUsername(fbrUserAccountInfoBean.getFbrUsername());*/
            fbrUserAccountInfo.setFbrPassword(fbrUserAccountInfoBean.getFbrPassword());
            fbrUserAccountInfo.setFbrPin(fbrUserAccountInfoBean.getFbrPin());
            fbrUserAccountInfo.setUser(user);
            fbrUserAccountInfoServices.saveOrUpdate(fbrUserAccountInfo);

            PaymentCart paymentCart = paymentCartServices.findOneByFbrUserAccountInfoAndUser(fbrUserAccountInfo, user);
            if (paymentCart != null) {
                paymentCartServices.saveOrUpdateToDeletedStatus(paymentCart);
            }
            return new ResponseEntity<>(new Status(1, "Successfully Saved"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0, "Incomplete Data"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/checkForScreen",produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<StatusBean> checkForFbrAccountInfo(@RequestBody FbrUserAccountInfoBean fbrUserAccountInfoBean, Device device) throws Exception {
        try {

            User user = usersServices.getUserFromToken();
            if (user == null) {
                return new ResponseEntity<>(new StatusBean(0,"Session Expired."), HttpStatus.OK);
            }

            FbrUserAccountInfo fbrUserAccountInfo = fbrUserAccountInfoServices.findOneByUser(user);
            if (fbrUserAccountInfo == null) {
                return new ResponseEntity<>(new StatusBean(1, "No FBR data found"), HttpStatus.OK);
            }
            if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == null) {
                return new ResponseEntity<>(new StatusBean(1, "No FBR data found"), HttpStatus.OK);
            }

            FbrUserAccountInfoBean fbrUserAccountInfoBean1 = new FbrUserAccountInfoBean();

            if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.REGISTERED) {

                fbrUserAccountInfoBean1.setId(fbrUserAccountInfo.getId());
                fbrUserAccountInfoBean1.setFbrPassword(fbrUserAccountInfo.getFbrPassword());
                fbrUserAccountInfoBean1.setFbrPin(fbrUserAccountInfo.getFbrPin());
                fbrUserAccountInfoBean1.setRegistredWithFbr(true);

                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean1);

                StatusBean statusBean = new StatusBean(3, "Already given FBR Data");
                statusBean.setResponse(fbrUserAccountInfoBeanList);

                return new ResponseEntity<>(statusBean, HttpStatus.OK);

            } else if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.NOT_REGISTERED) {
                fbrUserAccountInfoBean1.setRegistredWithFbr(false);

                List<FbrUserAccountInfoDocuments> fbrUserAccountInfoDocumentsListCnic = fbrUserAccountInfoDocumentsServices.findAllActiveRecordsByUserAndDocumentType(user,"Cnic");
                List<FbrUserAccountInfoDocumentsBean> fbrUserAccountInfoDocumentsBeanListCnic = new ArrayList<>();
                if (fbrUserAccountInfoDocumentsListCnic != null && fbrUserAccountInfoDocumentsListCnic.size() > 0) {
                    for (FbrUserAccountInfoDocuments fbrUserAccountInfoDocuments : fbrUserAccountInfoDocumentsListCnic) {
                        FbrUserAccountInfoDocumentsBean fbrUserAccountInfoDocumentsBean = new FbrUserAccountInfoDocumentsBean();

                        fbrUserAccountInfoDocumentsBean.setId(fbrUserAccountInfoDocuments.getId());
                        fbrUserAccountInfoDocumentsBean.setFilename(fbrUserAccountInfoDocuments.getDocumentDescription());

                        if (device.isTablet() || device.isMobile()) {
                            fbrUserAccountInfoDocumentsBean.setUrl(serverUrl + fbrUserAccountInfoDocuments.getDocumentName());
                        } else {
                            fbrUserAccountInfoDocumentsBean.setBase64(Base64.encodeBase64String(Base64Util.readBytesFromFile(staticContentPath + fbrUserAccountInfoDocuments.getDocumentName())));
                            if (fbrUserAccountInfoDocuments.getDocumentFormat() != null) {
                                fbrUserAccountInfoDocumentsBean.setFiletype(fbrUserAccountInfoDocuments.getDocumentFormat());
                            }
                        }
                        fbrUserAccountInfoDocumentsBeanListCnic.add(fbrUserAccountInfoDocumentsBean);
                    }
                    fbrUserAccountInfoBean1.setCnicList(fbrUserAccountInfoDocumentsBeanListCnic);
                }
                List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean1);

                StatusBean statusBean = new StatusBean(2, "Fbr data found");
                statusBean.setResponse(fbrUserAccountInfoBeanList);

                return new ResponseEntity<>(statusBean, HttpStatus.OK);
            } else {
                Payment fbrPayment = paymentServices.checkForFbrUserAccountInfoPayment(fbrUserAccountInfo);
                if (fbrPayment != null) {
                    List<Taxform> taxformList = taxformServices.findAllByUserAndFBRStatus(user);
                    if (taxformList != null && taxformList.size() > 0) {
                        for (Taxform taxform : taxformList) {
                            Payment taxformPayment = paymentServices.checkForTaxformPayment(taxform);
                            if (taxformPayment == null) {
                                SettingPayment settingPayment = settingPaymentServices.findPaymentByYearForForTaxform(taxform.getTaxformYear());
                                if (settingPayment == null) {
                                    StatusBean statusBean = new StatusBean(0, "Payment Not defined for TaxForm");
                                    return new ResponseEntity<>(statusBean, HttpStatus.OK);
                                }

                                taxform.setStatus(taxformStatusServices.findOneByPaymentStatus());
                                taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                taxformServices.saveOrUpdate(taxform);

                                PaymentCart paymentCartForTaxform = paymentCartServices.findOneByTaxformAndUser(taxform, user);
                                if (paymentCartForTaxform == null) {
                                    paymentCartForTaxform = new PaymentCart();
                                    paymentCartForTaxform.setStatus(1);
                                    paymentCartForTaxform.setProductType(ProductType.TAXFORM);
                                    paymentCartForTaxform.setUser(user);
                                    paymentCartForTaxform.setTaxform(taxform);
                                    paymentCartForTaxform.setSettingPayment(settingPayment);

                                    paymentCartServices.saveOrUpdateToActiveStatus(paymentCartForTaxform);
                                }
                            } else {
                                taxform.setStatus(taxformStatusServices.findOneByAccountantStatus());
                                taxform.setCurrentDate(CommonUtil.getCurrentTimestamp());
                                taxformServices.saveOrUpdate(taxform);
                            }
                        }
                    }
                    if (fbrUserAccountInfo.getFbrUserAccountInfoStatus() == FbrUserAccountInfoStatus.LAWYER_CLOSE) {
                        fbrUserAccountInfoBean1.setId(fbrUserAccountInfo.getId());
                        fbrUserAccountInfoBean1.setFbrNtnNumber(fbrUserAccountInfo.getFbrNtnNumber());
                        fbrUserAccountInfoBean1.setFbrUsername(fbrUserAccountInfo.getFbrUsername());
                        fbrUserAccountInfoBean1.setFbrPassword(fbrUserAccountInfo.getFbrPassword());
                        fbrUserAccountInfoBean1.setFbrPin(fbrUserAccountInfo.getFbrPin());
                        fbrUserAccountInfoBean1.setRegistredWithFbr(true);

                        List<FbrUserAccountInfoBean> fbrUserAccountInfoBeanList = new ArrayList<>();
                        fbrUserAccountInfoBeanList.add(fbrUserAccountInfoBean1);

                        StatusBean statusBean = new StatusBean(5, "NTN is registered successfully.");
                        statusBean.setResponse(fbrUserAccountInfoBeanList);

                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    } else {
                        StatusBean statusBean = new StatusBean(4, "Already paid for NTN Registration");
                        return new ResponseEntity<>(statusBean, HttpStatus.OK);
                    }
                }
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new StatusBean(0, "Incomplete Data"), HttpStatus.OK);
    }
}