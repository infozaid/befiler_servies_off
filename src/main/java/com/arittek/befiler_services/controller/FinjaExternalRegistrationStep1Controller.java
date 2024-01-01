package com.arittek.befiler_services.controller;

import com.arittek.befiler_services.beans.finja_external_registration.*;
import com.arittek.befiler_services.model.enums.UserStatus;
import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep1Request;
import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep1Response;
import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep2Request;
import com.arittek.befiler_services.model.finja_external_registration.FinjaExternalRegistrationStep2Response;
import com.arittek.befiler_services.repository.finja_external_registration.FinjaExternalRegistrationStep1RequestRepository;
import com.arittek.befiler_services.repository.finja_external_registration.FinjaExternalRegistrationStep1ResponseRepository;
import com.arittek.befiler_services.repository.finja_external_registration.FinjaExternalRegistrationStep2RequestRepository;
import com.arittek.befiler_services.repository.finja_external_registration.FinjaExternalRegistrationStep2ResponseRepository;
import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/finja/registration")
public class FinjaExternalRegistrationStep1Controller {

    private UsersServices usersServices;
    private FinjaExternalRegistrationStep1RequestRepository finjaExternalRegistrationStep1RequestRepository;
    private FinjaExternalRegistrationStep1ResponseRepository finjaExternalRegistrationStep1ResponseRepository;


    private FinjaExternalRegistrationStep2RequestRepository finjaExternalRegistrationStep2RequestRepository;
    private FinjaExternalRegistrationStep2ResponseRepository finjaExternalRegistrationStep2ResponseRepository;

    @Autowired
    public FinjaExternalRegistrationStep1Controller(UsersServices usersServices, FinjaExternalRegistrationStep1RequestRepository finjaExternalRegistrationStep1RequestRepository, FinjaExternalRegistrationStep1ResponseRepository finjaExternalRegistrationStep1ResponseRepository, FinjaExternalRegistrationStep2RequestRepository finjaExternalRegistrationStep2RequestRepository , FinjaExternalRegistrationStep2ResponseRepository finjaExternalRegistrationStep2ResponseRepository) {
        this.usersServices = usersServices;
        this.finjaExternalRegistrationStep1RequestRepository = finjaExternalRegistrationStep1RequestRepository;
        this.finjaExternalRegistrationStep1ResponseRepository = finjaExternalRegistrationStep1ResponseRepository;
        this.finjaExternalRegistrationStep2RequestRepository =  finjaExternalRegistrationStep2RequestRepository;
        this.finjaExternalRegistrationStep2ResponseRepository = finjaExternalRegistrationStep2ResponseRepository;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/step1", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FinjaExternalRegistrationResponseBean> finjaExternalRegistrationStep1(@RequestBody FinjaExternalRegistrationRequestBean finjaExternalRegistrationRequestBean) throws Exception {
        try {

            /*if (finjaExternalRegistrationRequestBean.getMobileNo() == null) {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Please enter mobile no."), HttpStatus.OK);
            }

            if (finjaExternalRegistrationRequestBean.getCnic() == null) {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Please enter CNIC"), HttpStatus.OK);
            }*/

            /*User user = usersServices.findOneByIdAndStatus(finjaExternalRegistrationRequestBean.getUserId(), usersServices.findUserStatusById(1));*/
            User user = usersServices.findOneByIdAndStatus(finjaExternalRegistrationRequestBean.getUserId(), UserStatus.ACTIVE);
            if (user == null) {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Session Expired"), HttpStatus.OK);
            }


            /*Finja External Registration Request for Database*/
            FinjaExternalRegistrationStep1Request finjaExternalRegistrationStep1Request = new FinjaExternalRegistrationStep1Request();

            finjaExternalRegistrationStep1Request.setAppKey("7Bf3mVupu33sDk1m");
            finjaExternalRegistrationStep1Request.setAppId("236");
            finjaExternalRegistrationStep1Request.setApplicationVersion("1");
            finjaExternalRegistrationStep1Request.setMerchantId("508251779");
            finjaExternalRegistrationStep1Request.setStep("1");

            finjaExternalRegistrationStep1Request.setMobileNo(user.getMobileNo());
            finjaExternalRegistrationStep1Request.setCnic(user.getCnicNo());

            finjaExternalRegistrationStep1RequestRepository.save(finjaExternalRegistrationStep1Request);

            String url = "http://newdemoapi.finca.web.pk/RegistrationExternal";
            FinjaExternalRegistrationStep1RequestBean finjaExternalRegistrationStep1RequestBean = new FinjaExternalRegistrationStep1RequestBean(finjaExternalRegistrationStep1Request);

            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", finjaExternalRegistrationStep1Request.getAppKey());
            headers.set("appId", finjaExternalRegistrationStep1Request.getAppId() + "");
            headers.set("Content-Type", "application/json");

            HttpEntity<FinjaExternalRegistrationStep1RequestBean> request = new HttpEntity<>(finjaExternalRegistrationStep1RequestBean, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<FinjaExternalRegistrationStep1ResponseBean> finjaExternalRegistrationStep1ResponseBeanResponseEntity = restTemplate.postForEntity(url, request, FinjaExternalRegistrationStep1ResponseBean.class);

            if (finjaExternalRegistrationStep1ResponseBeanResponseEntity != null && finjaExternalRegistrationStep1ResponseBeanResponseEntity.getBody() != null && finjaExternalRegistrationStep1ResponseBeanResponseEntity.getBody().getCode() != null) {
                FinjaExternalRegistrationStep1ResponseBean finjaExternalRegistrationStep1ResponseBean = finjaExternalRegistrationStep1ResponseBeanResponseEntity.getBody();

                FinjaExternalRegistrationStep1Response finjaExternalRegistrationStep1Response = new FinjaExternalRegistrationStep1Response(finjaExternalRegistrationStep1ResponseBean);
                finjaExternalRegistrationStep1Response.setFinjaExternalRegistrationStep1Request(finjaExternalRegistrationStep1Request);

                finjaExternalRegistrationStep1Response =  finjaExternalRegistrationStep1ResponseRepository.save(finjaExternalRegistrationStep1Response);

                /*finjaExternalRegistrationStep1Request.setFinjaExternalRegistrationStep1Response(finjaExternalRegistrationStep1Response);
                finjaExternalRegistrationStep1RequestRepository.save(finjaExternalRegistrationStep1Request);*/

                System.out.println("Step1 response msg:::::::::::::"+finjaExternalRegistrationStep1ResponseBean.getMsg());
                if (finjaExternalRegistrationStep1ResponseBean.getCode() == 200) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(2,finjaExternalRegistrationStep1Response.getFinjaExternalRegistrationStep1RequestId() ,"Successfully"), HttpStatus.OK);
                } else {
                    FinjaExternalRegistrationResponseBean finjaExternalRegistrationResponseBean = new FinjaExternalRegistrationResponseBean(1, "Error from server");
                    finjaExternalRegistrationResponseBean.setResponseCode(finjaExternalRegistrationStep1ResponseBean.getCode());
                    finjaExternalRegistrationResponseBean.setResponseMessage(finjaExternalRegistrationStep1ResponseBean.getMsg());
                    return new ResponseEntity<>(finjaExternalRegistrationResponseBean, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Server is not responding"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Incomplete data"), HttpStatus.OK);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/step2", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FinjaExternalRegistrationResponseBean> finjaExternalRegistrationStep2(@RequestBody FinjaExternalRegistrationRequestBean step2bean) {
        try {
            User user = null;
            if (step2bean != null) {
                if (step2bean.getUserId() != null) {
                    user = usersServices.findOneByIdAndStatus(step2bean.getUserId(), UserStatus.ACTIVE);
                    if (user == null) {
                        return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Session Expired"), HttpStatus.OK);
                    }
                }
//                if (step2bean.getApplicationVersion() == null) {
//                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete ApplicationVersion"), HttpStatus.OK);
//                }
                if (user.getMobileNo() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete MobileNo"), HttpStatus.OK);
                }
                if (user.getCnicNo() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete CnicNo"), HttpStatus.OK);
                }
                if (user.getEmailAddress() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete EmailAddress"), HttpStatus.OK);
                }
//                if (step2bean.getStep() == null) {
//                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete Step"), HttpStatus.OK);
//                }
//                if (step2bean.getQueueId() == null) {
//                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete QueueId"), HttpStatus.OK);
//                }
//                if (step2bean.getToken() == null) {
//                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete Token"), HttpStatus.OK);
//                }
                if (step2bean.getOtp() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete Otp"), HttpStatus.OK);
                }
                if (step2bean.getFatherName() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete FatherName"), HttpStatus.OK);
                }
                if (step2bean.getMotherName() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete MotherName"), HttpStatus.OK);
                }
                if (step2bean.getDisplayName() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete DisplayName"), HttpStatus.OK);
                }
                if (step2bean.getFirstName() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete FirstName"), HttpStatus.OK);
                }
                if (step2bean.getLastName() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete LastName"), HttpStatus.OK);
                }
                if (step2bean.getIsBirthPlace() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete IsBirthPlace"), HttpStatus.OK);
                }
                if (step2bean.getBirthPlace() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete BirthPlace"), HttpStatus.OK);
                }
                // currentPlace
                if (step2bean.getMailingAddress() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete MailingAddress"), HttpStatus.OK);
                }
                if (step2bean.getOperator() == null) {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, " Incomplete Operator"), HttpStatus.OK);
                }

                FinjaExternalRegistrationStep1Request finjaExternalRegistrationStep1Request = null;
                FinjaExternalRegistrationStep1Response finjaExternalRegistrationStep1Response = null;

                // Finja External Registration Request for Database
                FinjaExternalRegistrationStep2Request finjaExternalRegistrationStep2Request = new FinjaExternalRegistrationStep2Request();

                if(step2bean.getFinjaStep1RequestId() != null){
                     finjaExternalRegistrationStep1Request = finjaExternalRegistrationStep1RequestRepository.findById(step2bean.getFinjaStep1RequestId()).orElse(null);
                    if(finjaExternalRegistrationStep1Request!= null){
                        finjaExternalRegistrationStep2Request.setFinjaExternalRegistrationStep1Request(finjaExternalRegistrationStep1Request);
                    }
                }else{
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1,"Invalid RequestId!"), HttpStatus.OK);
                }

                if(step2bean.getFinjaStep1RequestId() != null){
                    finjaExternalRegistrationStep1Response   = finjaExternalRegistrationStep1ResponseRepository.findById(step2bean.getFinjaStep1RequestId()).orElse(null);
                    if(finjaExternalRegistrationStep1Response != null){
                        finjaExternalRegistrationStep2Request.setToken(finjaExternalRegistrationStep1Response.getToken());
                        finjaExternalRegistrationStep2Request.setQueueId(finjaExternalRegistrationStep1Response.getQueueId());
                    }
                }

                finjaExternalRegistrationStep2Request.setAppId(236);
                finjaExternalRegistrationStep2Request.setAppKey("7Bf3mVupu33sDk1m");
                finjaExternalRegistrationStep2Request.setMerchantId(508251779);
                finjaExternalRegistrationStep2Request.setStep(2);
                finjaExternalRegistrationStep2Request.setApplicationVersion(1);

                finjaExternalRegistrationStep2Request.setOperator(step2bean.getOperator());
                finjaExternalRegistrationStep2Request.setMailingAddress(step2bean.getMailingAddress());

                finjaExternalRegistrationStep2Request.setBirthPlace(step2bean.getBirthPlace()); // 0 means born in pk 1 not born in pk

                finjaExternalRegistrationStep2Request.setIsBirthPlace(step2bean.getIsBirthPlace());

                finjaExternalRegistrationStep2Request.setLastName(step2bean.getLastName());
                finjaExternalRegistrationStep2Request.setFirstName(step2bean.getFirstName());
                finjaExternalRegistrationStep2Request.setEmail(user.getEmailAddress());
                finjaExternalRegistrationStep2Request.setDisplayName(step2bean.getDisplayName());
                finjaExternalRegistrationStep2Request.setMotherName(step2bean.getMotherName());
                finjaExternalRegistrationStep2Request.setFatherName(step2bean.getFatherName());
                finjaExternalRegistrationStep2Request.setOtp(step2bean.getOtp());
                finjaExternalRegistrationStep2Request.setMobileNo(user.getMobileNo());
                finjaExternalRegistrationStep2Request.setCnic(user.getCnicNo());

                finjaExternalRegistrationStep2RequestRepository.save(finjaExternalRegistrationStep2Request);

                String url = "http://newdemoapi.finca.web.pk/RegistrationExternal";
                FinjaExternalRegistrationStep2RequestBean finjaExternalRegistrationStep2RequestBean1 = new FinjaExternalRegistrationStep2RequestBean(finjaExternalRegistrationStep2Request);

                HttpHeaders headers = new HttpHeaders();
                headers.set("appKey", finjaExternalRegistrationStep2Request.getAppKey());
                headers.set("appId", finjaExternalRegistrationStep2Request.getAppId() + "");
                headers.set("Content-Type", "application/json");


                HttpEntity<FinjaExternalRegistrationStep2RequestBean> request = new HttpEntity<>(finjaExternalRegistrationStep2RequestBean1, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<FinjaExternalRegistrationStep1ResponseBean> finjaExternalRegistrationStep2ResponseBeanResponseEntity = restTemplate.postForEntity(url, request, FinjaExternalRegistrationStep1ResponseBean.class);
                if (finjaExternalRegistrationStep2ResponseBeanResponseEntity != null && finjaExternalRegistrationStep2ResponseBeanResponseEntity.getBody() != null && finjaExternalRegistrationStep2ResponseBeanResponseEntity.getBody().getCode() != null) {
                    FinjaExternalRegistrationStep1ResponseBean finjaExternalRegistrationStep1ResponseBean = finjaExternalRegistrationStep2ResponseBeanResponseEntity.getBody();

                    FinjaExternalRegistrationStep2Response finjaExternalRegistrationStep2Response = new FinjaExternalRegistrationStep2Response(finjaExternalRegistrationStep1ResponseBean);
                    finjaExternalRegistrationStep2Response.setFinjaExternalRegistrationStep1Request(finjaExternalRegistrationStep1Request);
                    finjaExternalRegistrationStep2Response.setCurrentDate(CommonUtil.getCurrentTimestamp());
                    finjaExternalRegistrationStep2ResponseRepository.save(finjaExternalRegistrationStep2Response);

                    if (finjaExternalRegistrationStep1ResponseBean.getCode() == 200) {
                        return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(2,"Successfully"), HttpStatus.OK);
                    } else {
                        FinjaExternalRegistrationResponseBean finjaExternalRegistrationResponseBean = new FinjaExternalRegistrationResponseBean(1, "Error from server");
                        finjaExternalRegistrationResponseBean.setResponseCode(finjaExternalRegistrationStep1ResponseBean.getCode());
                        finjaExternalRegistrationResponseBean.setResponseMessage(finjaExternalRegistrationStep1ResponseBean.getMsg());
                        return new ResponseEntity<>(finjaExternalRegistrationResponseBean, HttpStatus.OK);
                    }
                } else {
                    return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Server is not responding"), HttpStatus.OK);
                }

            } else {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Incomplete all data"), HttpStatus.OK);
            }

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : ", e);
            return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Incomplete data"), HttpStatus.OK);
        }

    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/externalList", produces = "application/json", method = RequestMethod.POST)
    public ResponseEntity<FinjaExternalRegistrationResponseBean> getFinjaExternalList(@RequestBody FinjaExternalRegistrationRequestBean finjaExternalRegistrationRequestBean) throws Exception {
        try {

            if (finjaExternalRegistrationRequestBean.getListType() == null) {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Please select list type"), HttpStatus.OK);
            }

            /*User user = usersServices.findOneByIdAndStatus(finjaExternalRegistrationRequestBean.getUserId(), usersServices.findUserStatusById(1));*/
            User user = usersServices.findOneByIdAndStatus(finjaExternalRegistrationRequestBean.getUserId(), UserStatus.ACTIVE);
            if (user == null) {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(1, "Session Expired"), HttpStatus.OK);
            }

            String url = "http://newdemoapi.finca.web.pk/ExternalList";
            FinjaExternalListRequestBean finjaExternalListRequestBean = new FinjaExternalListRequestBean();

            finjaExternalListRequestBean.setApplicationVersion(1);
            finjaExternalListRequestBean.setMerchantId(508251779);
            if (finjaExternalRegistrationRequestBean.getListType() == 0) {
                finjaExternalListRequestBean.setList("city");
            } else if (finjaExternalRegistrationRequestBean.getListType() == 1) {
                finjaExternalListRequestBean.setList("country");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("appKey", "CfTua0ETxVE1414C");
            headers.set("appId", "249");
            headers.set("Content-Type", "application/json");

            HttpEntity<FinjaExternalListRequestBean> request = new HttpEntity<>(finjaExternalListRequestBean, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<FinjaExternalListResponseBean> finjaExternalListResponseBeanResponseEntity = restTemplate.postForEntity(url, request, FinjaExternalListResponseBean.class);

            if (finjaExternalListResponseBeanResponseEntity != null && finjaExternalListResponseBeanResponseEntity.getBody() != null && finjaExternalListResponseBeanResponseEntity.getBody().getCode() != null) {
                FinjaExternalListResponseBean finjaExternalListResponseBean = finjaExternalListResponseBeanResponseEntity.getBody();

                if (finjaExternalListResponseBean.getCode() == 200) {
                    List<FinjaRegionBean> finjaRegionBeanList = new ArrayList<>();
                    if (finjaExternalRegistrationRequestBean.getListType() == 0) {
                        for (City city : finjaExternalListResponseBean.getData().getCity_list()) {
                            FinjaRegionBean finjaRegionBean = new FinjaRegionBean();
                            finjaRegionBean.setId(city.getId());
                            finjaRegionBean.setName(city.getCityName());

                            finjaRegionBeanList.add(finjaRegionBean);
                        }
                    } else if (finjaExternalRegistrationRequestBean.getListType() == 1) {
                        for (Country country: finjaExternalListResponseBean.getData().getCountry_list()) {
                            FinjaRegionBean finjaRegionBean = new FinjaRegionBean();
                            finjaRegionBean.setId(country.getId());
                            finjaRegionBean.setName(country.getCountryName());

                            finjaRegionBeanList.add(finjaRegionBean);
                        }
                    }

                    FinjaExternalRegistrationResponseBean finjaExternalRegistrationResponseBean = new FinjaExternalRegistrationResponseBean(2, "Successfully");
                    finjaExternalRegistrationResponseBean.setFinjaRegionBeanList(finjaRegionBeanList);

                    return new ResponseEntity<>(finjaExternalRegistrationResponseBean, HttpStatus.OK);
                } else {
                    FinjaExternalRegistrationResponseBean finjaExternalRegistrationResponseBean = new FinjaExternalRegistrationResponseBean(1, "Error from server");
                    finjaExternalRegistrationResponseBean.setResponseCode(finjaExternalListResponseBean.getCode());
                    finjaExternalRegistrationResponseBean.setResponseMessage(finjaExternalListResponseBean.getMsg());
                    return new ResponseEntity<>(finjaExternalRegistrationResponseBean, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Server is not responding"), HttpStatus.OK);
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<>(new FinjaExternalRegistrationResponseBean(0, "Incomplete data"), HttpStatus.OK);
        }
    }


}
