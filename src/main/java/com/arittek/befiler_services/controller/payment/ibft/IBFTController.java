package com.arittek.befiler_services.controller.payment.ibft;

import com.arittek.befiler_services.beans.DocumentsBean;
import com.arittek.befiler_services.beans.PaymentCartBean;
import com.arittek.befiler_services.beans.Status;
import com.arittek.befiler_services.beans.StatusBean;
import com.arittek.befiler_services.beans.payment.PaymentCustomerInfoBean;
import com.arittek.befiler_services.beans.payment.ibft.IBFTRequestBean;
import com.arittek.befiler_services.beans.payment.ibft.IBFTRequestDocumentBean;
import com.arittek.befiler_services.model.enums.*;
import com.arittek.befiler_services.model.notifications.Notification;
import com.arittek.befiler_services.model.payment.Payment;
import com.arittek.befiler_services.model.payment.PaymentCart;
import com.arittek.befiler_services.model.payment.PaymentCustomerInfo;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequest;
import com.arittek.befiler_services.model.payment.ibft.IBFTRequestDocument;
import com.arittek.befiler_services.model.payment.promo_code.PromoCode;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.SettingsServices;
import com.arittek.befiler_services.services.TaxformServices;
import com.arittek.befiler_services.services.notifications.NotificationServices;
import com.arittek.befiler_services.services.ntn.FbrUserAccountInfoServices;
import com.arittek.befiler_services.services.payment.PaymentCartServices;
import com.arittek.befiler_services.services.payment.PaymentCustomerInfoServices;
import com.arittek.befiler_services.services.payment.PaymentServices;
import com.arittek.befiler_services.services.payment.PromoCodeServices;
import com.arittek.befiler_services.services.payment.ibft.IBFTRequestDocumentServices;
import com.arittek.befiler_services.services.payment.ibft.IBFTRequestServices;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import com.arittek.befiler_services.util.email.EmailSender;
import com.arittek.befiler_services.util.email.EmailUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ibft")
public class IBFTController {

    private UsersServices usersServices;
    private TaxformServices taxformServices;
    private PaymentServices paymentServices;
    private PromoCodeServices promoCodeServices;
    private PaymentCartServices paymentCartServices;
    private IBFTRequestServices ibftRequestServices;
    private NotificationServices notificationServices;
    private FbrUserAccountInfoServices fbrUserAccountInfoServices;
    private IBFTRequestDocumentServices ibftRequestDocumentServices;
    private PaymentCustomerInfoServices paymentCustomerInfoServices;

    @Value("${static.content.path}")
    private String staticContentPath;
    @Value("${static.content.ibft.request.path}")
    private String staticContentIbftRequestPath;

    @Autowired
    public IBFTController(UsersServices usersServices, TaxformServices taxformServices, PaymentServices paymentServices, PromoCodeServices promoCodeServices, PaymentCartServices paymentCartServices, IBFTRequestServices ibftRequestServices, NotificationServices notificationServices, FbrUserAccountInfoServices fbrUserAccountInfoServices, IBFTRequestDocumentServices ibftRequestDocumentServices, PaymentCustomerInfoServices paymentCustomerInfoServices) {
        this.usersServices = usersServices;
        this.taxformServices = taxformServices;
        this.paymentServices = paymentServices;
        this.promoCodeServices = promoCodeServices;
        this.paymentCartServices = paymentCartServices;
        this.ibftRequestServices = ibftRequestServices;
        this.notificationServices = notificationServices;
        this.fbrUserAccountInfoServices = fbrUserAccountInfoServices;
        this.ibftRequestDocumentServices = ibftRequestDocumentServices;
        this.paymentCustomerInfoServices = paymentCustomerInfoServices;
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/request", produces = "application/json", method = {RequestMethod.POST})
    public ResponseEntity<Status> ibftRequest(@RequestBody PaymentCustomerInfoBean paymentCustomerInfoBean) throws Exception {
        try {
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getCustomer())) { return new ResponseEntity<>(new Status(0, "Please enter customer name."), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getMobileNo())) { return new ResponseEntity<>(new Status(0, "Please enter mobile no."), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getEmailAddress())) { return new ResponseEntity<>(new Status(0, "Please enter email address"), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getResidentialAddress())) { return new ResponseEntity<>(new Status(0, "Please enter residential address"), HttpStatus.OK); }
            if (!StringUtils.isNotEmpty(paymentCustomerInfoBean.getBillingAddress())) { return new ResponseEntity<>(new Status(0, "Please enter billing address"), HttpStatus.OK); }

            if (paymentCustomerInfoBean.getDocumentsBeanList() == null && paymentCustomerInfoBean.getDocumentsBeanList().isEmpty())
                return new ResponseEntity<>(new Status(0, "Please upload file"), HttpStatus.OK);

            User user = usersServices.getUserFromToken();
            if (user == null)
                return new ResponseEntity<>(new Status(0, "Session Expired"), HttpStatus.OK);

            IBFTRequest ibftRequest = new IBFTRequest();
            ibftRequest.setStatus(AppStatus.ACTIVE);

            ibftRequest.setCustomerName(paymentCustomerInfoBean.getCustomer());
            ibftRequest.setMobileNo(paymentCustomerInfoBean.getMobileNo());
            ibftRequest.setEmailAddress(paymentCustomerInfoBean.getEmailAddress());
            ibftRequest.setResidentialAddress(paymentCustomerInfoBean.getResidentialAddress());
            ibftRequest.setBillingAddress(paymentCustomerInfoBean.getBillingAddress());

            ibftRequest.setUser(user);

            ibftRequestServices.save(ibftRequest);

            for(IBFTRequestDocumentBean documentsBean : paymentCustomerInfoBean.getDocumentsBeanList()){
                if(documentsBean.getFilename() != null && documentsBean.getFiletype() != null && documentsBean.getBase64() != null){
                    IBFTRequestDocument ibftRequestDocument = new IBFTRequestDocument();
                    ibftRequestDocument.setIbftRequest(ibftRequest);
                    ibftRequestDocument.setStatus(AppStatus.ACTIVE);

                    ibftRequestDocument.setDocumentDescription(documentsBean.getFilename());
                    ibftRequestDocument.setDocumentFormat(documentsBean.getFiletype());
                    ibftRequestDocument.setDocumentType("IBFT Request");

                    ibftRequestDocument = ibftRequestDocumentServices.saveOrUpdate(ibftRequestDocument);

                    if(ibftRequestDocument != null && ibftRequestDocument.getId() != null){
                        String fileFormat = FilenameUtils.getExtension(documentsBean.getFilename());
                        String fileName = user.getId() + "_" + ibftRequestDocument.getId() + "." + fileFormat;
                        String fileUrl= staticContentIbftRequestPath + fileName;
                        byte[] imageByte= Base64.decodeBase64(documentsBean.getBase64());
                        BufferedOutputStream imageOutFile = new BufferedOutputStream(new FileOutputStream(fileUrl));
                        imageOutFile.write(imageByte);
                        imageOutFile.close();

                        ibftRequestDocument.setDocumentName("ibft/request/" + fileName);

                        ibftRequestDocumentServices.saveOrUpdate(ibftRequestDocument);
                    }
                }
            }
            return new ResponseEntity<>(new Status(1,"Request Generated Successfully."), HttpStatus.OK);
        } catch(Exception e){
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
        }
        return new ResponseEntity<>(new Status(0,"Incomplete Data."), HttpStatus.OK);
    }


    @SuppressWarnings("unchecked")
    @Transactional(rollbackOn = Exception.class)
    @RequestMapping(value = "/request", produces = "application/json", method = {RequestMethod.GET})
    public ResponseEntity<StatusBean> findAllIBFTRequests() {
        try {
            List<IBFTRequest> ibftRequestList = ibftRequestServices.findAllActiveRecordsOrderByIdDesc();
            List<IBFTRequestBean> ibftRequestBeanList = new ArrayList<>();
            for(IBFTRequest ibftRequest : ibftRequestList) {
                IBFTRequestBean ibftRequestBean = new IBFTRequestBean();

                ibftRequestBean.setId(ibftRequest.getId());
                ibftRequestBean.setCustomerName(ibftRequest.getCustomerName());
                ibftRequestBean.setMobileNo(ibftRequest.getMobileNo());
                ibftRequestBean.setEmailAddress(ibftRequest.getEmailAddress());
                ibftRequestBean.setResidentialAddress(ibftRequest.getResidentialAddress());
                ibftRequestBean.setBillingAddress(ibftRequest.getBillingAddress());

                ibftRequestBean.setUserId(ibftRequest.getUser().getId());
                ibftRequestBean.setUserFullName(ibftRequest.getUser().getFullName());

                List<IBFTRequestDocument> ibftRequestDocumentList = ibftRequest.getIbftRequestDocumentList();
                List<IBFTRequestDocumentBean> ibftRequestDocumentBeanList = new ArrayList<>();
                for (IBFTRequestDocument ibftRequestDocument : ibftRequestDocumentList) {
                    IBFTRequestDocumentBean ibftRequestDocumentBean = new IBFTRequestDocumentBean();

                    ibftRequestDocumentBean.setId(ibftRequestDocument.getId());
                    ibftRequestDocumentBean.setFilename(ibftRequestDocument.getDocumentDescription());
                    ibftRequestDocumentBean.setFiletype(ibftRequestDocument.getDocumentFormat());

                    File file =  new File(staticContentPath + ibftRequestDocument.getDocumentName());
                    FileInputStream fileInputStreamReader = new FileInputStream(file);
                    byte[] bytes = new byte[(int)file.length()];
                    fileInputStreamReader.read(bytes);
                    ibftRequestDocumentBean.setBase64(new String(Base64.encodeBase64(bytes), "UTF-8"));

                    ibftRequestDocumentBeanList.add(ibftRequestDocumentBean);
                }
                ibftRequestBean.setIbftRequestDocumentBeanList(ibftRequestDocumentBeanList);

                List<PaymentCartBean> paymentCartBeanList = new ArrayList<>();
                List<PaymentCart> paymentCartList = paymentCartServices.findAllActivePaymentCartsByUser(ibftRequest.getUser());
                if (paymentCartList != null && paymentCartList.size() > 0) {
                    for (PaymentCart paymentCart : paymentCartList) {
                        if (paymentCart != null && paymentCart.getProductType() != null) {
                            PaymentCartBean paymentCartBean = new PaymentCartBean();
                            if (paymentCart.getProductType() == ProductType.TAXFORM) {
                                if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                    Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                    if (payment == null) {
                                        paymentCartBean.setPaymentCartId(paymentCart.getId());
                                        paymentCartBean.setDescription(paymentCart.getTaxform().getTaxformYear().getYear() + "");
                                        paymentCartBean.setDetail(paymentCart.getTaxform().getTaxformYear().getYear() + " Tax will be filed on your behalf with Federal Board of Revenue(FBR)");
                                        paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
                                        paymentCartBean.setTaxformId(paymentCart.getTaxform().getId() + "");
                                        paymentCartBean.setRemove(true);

                                        paymentCartBeanList.add(paymentCartBean);
                                    } else {
                                        // TODO
                                    }
                                }
                            } else if (paymentCart.getProductType() == ProductType.NTN) {
                                if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                    Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                    if (payment == null) {
                                        paymentCartBean.setPaymentCartId(paymentCart.getId());
                                        paymentCartBean.setDescription("NTN");
                                        paymentCartBean.setDetail("BeFiler will register your National Tax Number(NTN) with FBR on your behalf.");
                                        paymentCartBean.setAmount(paymentCart.getSettingPayment().getAmount() + "");
                                        paymentCartBean.setFbrId(paymentCart.getFbrUserAccountInfo().getId() + "");
                                        paymentCartBean.setRemove(false);

                                        paymentCartBeanList.add(paymentCartBean);
                                    } else {
                                        //TODO
                                    }
                                }
                            }
                        }
                    }
                }
                ibftRequestBean.setPaymentCartBeanList(paymentCartBeanList);

                ibftRequestBeanList.add(ibftRequestBean);
            }
            StatusBean bean = new StatusBean(1,"Success");
            bean.setResponse(ibftRequestBeanList);
            return new ResponseEntity<>(bean, HttpStatus.OK);
        } catch(Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new StatusBean(0, "Error"), HttpStatus.OK);
        }
    }


    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/confirm", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Status> promoCode(@RequestBody PaymentCustomerInfoBean paymentCustomerInfoBean) throws Exception {
        try {
            if (paymentCustomerInfoBean.getIbftRequestId() == null)
                return new ResponseEntity<>(new Status(0, "Record not selected properly"), HttpStatus.OK);
            IBFTRequest ibftRequest = ibftRequestServices.findOneActiveRecordById(paymentCustomerInfoBean.getIbftRequestId());
            if (ibftRequest == null)
                return new ResponseEntity<>(new Status(0, "Record not selected properly"), HttpStatus.OK);

            User user = ibftRequest.getUser();
            if (user == null)
                return new ResponseEntity<>(new Status(0, "Can't find customer"), HttpStatus.OK);

            if (paymentCustomerInfoBean.getPaymentCartBeanList() == null || paymentCustomerInfoBean.getPaymentCartBeanList().size() < 0) {
                return new ResponseEntity<>(new Status(0, "Empty cart"), HttpStatus.OK);
            }

            PromoCode promoCode = promoCodeServices.findOneActiveRecordByPromoCode("IBFTJSBANK");
            if (promoCode == null) {
                return new ResponseEntity<>(new Status(0, "Promo Code expired"), HttpStatus.OK);
            }
            if (promoCode.getPromoCodeType() != PromoCodeType.CASH) {
                return new ResponseEntity<>(new Status(0, "Promo Code expired"), HttpStatus.OK);
            }

            List<PaymentCart> paymentCartList = new ArrayList<>();
            Double amount = 0.0;
            for (PaymentCartBean paymentCartBean : paymentCustomerInfoBean.getPaymentCartBeanList()) {
                if (paymentCartBean.getPaymentCartId() != null) {
                    PaymentCart paymentCart = paymentCartServices.findOneByIdAndActiveStatus(paymentCartBean.getPaymentCartId());
                    if (paymentCart != null && paymentCart.getProductType() != null) {
                        paymentCartList.add(paymentCart);
                        if (paymentCart.getProductType() == ProductType.TAXFORM) {
                            if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForTaxformPayment(paymentCart.getTaxform());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    return new ResponseEntity<>(new Status(0, "Tax Form is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        } else if (paymentCart.getProductType() == ProductType.NTN) {
                            if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                                Payment payment = paymentServices.checkForFbrUserAccountInfoPayment(paymentCart.getFbrUserAccountInfo());
                                if (payment == null) {
                                    amount += paymentCart.getSettingPayment().getAmount();
                                } else {
                                    return new ResponseEntity<>(new Status(0, "NTN Registration is already paid : " + paymentCart.getTaxform().getTaxformYear().getYear()), HttpStatus.OK);
                                }
                            }
                        }
                    } else {
                        return new ResponseEntity<>(new Status(0, "Payment cart error"), HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new Status(0, "Payment cart error"), HttpStatus.OK);
                }
            }

            /*Save Customer Info data*/
            PaymentCustomerInfo paymentCustomerInfo = paymentCustomerInfoServices.save(user, PaymentMethod.PROMO_CODE, ibftRequest, promoCode);
            if (paymentCustomerInfo == null) {
                usersServices.increasePaymentCountByOne(user, "Customer Info Error");
                return new ResponseEntity<>(new Status(0, "Customer Info Error"), HttpStatus.OK);
            }

            /*Generate Order ID*/
            SimpleDateFormat sdf = new SimpleDateFormat("yyDDD");
            String orderId = user.getId() /*+ "-"*/
                    + sdf.format(paymentCustomerInfo.getCreatedDate()) /*+ "-"*/
                    + String.format("%05d", paymentCustomerInfo.getId());
            paymentCustomerInfo.setOrderId(orderId);

            paymentCustomerInfoServices.update(paymentCustomerInfo);

            usersServices.resetPaymentCount(user, "Promo Code Success Payment ");
            paymentCartServices.saveOrUpdateToPaymentCustomerInfoRequest(paymentCartList, paymentCustomerInfo);
            List<PaymentCart> updatedPaymentCart = paymentCartServices.saveOrUpdateToPaymentStatus(paymentCartList);
            if (updatedPaymentCart != null && updatedPaymentCart.size() > 0) {
                for (PaymentCart paymentCart : paymentCartList) {
                    Payment payment = new Payment();

                    payment.setUser(user);
                    payment.setProductType(paymentCart.getProductType());
                    payment.setPaymentCustomerInfo(paymentCustomerInfo);
                    if (paymentCart.getProductType() == ProductType.TAXFORM) {
                        if (paymentCart.getTaxform() != null && paymentCart.getSettingPayment() != null) {
                            payment.setTaxform(paymentCart.getTaxform());
                            payment.setSettingPayment(paymentCart.getSettingPayment());

                            taxformServices.updateTaxformToAccountantStatus(paymentCart.getTaxform());
                        }
                    } else if (paymentCart.getProductType() == ProductType.NTN) {
                        if (paymentCart.getFbrUserAccountInfo() != null && paymentCart.getSettingPayment() != null) {
                            payment.setFbrUserAccountInfo(paymentCart.getFbrUserAccountInfo());
                            payment.setSettingPayment(paymentCart.getSettingPayment());

                            fbrUserAccountInfoServices.updateFbrUserAccountInfoToAccountantStatus(paymentCart.getFbrUserAccountInfo());
                        }
                    }
                    paymentServices.save(payment);
                }
            }

            Notification notification = new Notification();
            notification.setArchiveFlag(0);
            notification.setReadFlag(0);
            notification.setCreateDate(CommonUtil.getCurrentTimestamp());
            notification.setToUser(user.getId());
            notification.setNotificationTitle("Payment Receipt from BeFiler.com");
            notification.setNotificationDescription("We have successfully received your payment of PKR " + amount);

            notificationServices.save(notification);

            EmailSender.sendEmail(EmailUtil.paymentTemaplateForPromoCode(user.getFullName(), amount + "", paymentCustomerInfo.getOrderId(), "Payment Received Successfully"), "Payment Receipt from BeFiler.com", user.getEmailAddress());

            return new ResponseEntity<>(new Status(1, "Success"), HttpStatus.OK);
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new Status(0, "Incomplete data"), HttpStatus.OK);
        }
    }
}
