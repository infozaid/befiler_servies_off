package com.arittek.befiler_services.test.integratedPaymentGetway;/*
package com.arittek.integratedPaymentGetway;

import ae.comtrust.ipg.v2.MerchantAPI_PortType;
import ae.comtrust.ipg.v2.MerchantAPI_Service;
import ae.comtrust.ipg.v2.MerchantAPI_ServiceLocator;
import com.arittek.beans.Status;
import com.arittek.util.CommonUtil;
import com.arittek.util.Logger4j;
import org.datacontract.schemas._2004._07.Schemas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URL;

@RestController
public class TestIPGController {

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/testIpg" ,produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> testIPGRegisterationService(){
        try {
            */
/*System.setProperty("javax.net.ssl.trustStore",
                    "C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\cacerts");*//*

            System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
            System.setProperty("javax.net.ssl.trustStoreType", "jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
            System.setProperty("javax.net.ssl.keyStore", "C:/OpenSSL-Win64/bin/client_merchant_cert_p12.p12");
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStorePassword", "Comtrust");
            URL ur = new URL("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");
            System.out.println(ur);
            MerchantAPI_Service m = new MerchantAPI_ServiceLocator();
            System.out.println(ur.getAuthority());
            MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(ur);
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setCustomer("Demo Merchant");
            registrationRequest.setLanguage("ENG");
            registrationRequest.setVersion(BigDecimal.valueOf(2.0));
            */
/*registrationRequest.setOrderID("1234");*//*

            registrationRequest.setAmount(BigDecimal.valueOf(11500.0));
            registrationRequest.setCurrency("PKR");
            */
/*registrationRequest.setPassword("mypassword");*//*

            registrationRequest.setReturnPath("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");
            registrationRequest.setOrderName("Test Name");
            */
/*registrationRequest.setOrderInfo("Test Info");*//*

            */
/*registrationRequest.setTransactionHint("CPT:Y;VCC:Y");*//*


            RegistrationResponse rr = port.register(registrationRequest);
            System.out.println("----------Response--------");
            System.out.println(rr.getResponseCode());
            System.out.println(rr.getResponseDescription());
            System.out.println(rr.getTransactionID());
            System.out.println(rr.getUniqueID());
            System.out.println(rr.getPaymentPortal());

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<Status>(new Status(0, "Exception"), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(1,"Successfull"), HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @RequestMapping(value = "/testIpgAuthorize" ,produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> testIPGAuthorizationService(){
        try {
            */
/*System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\cacerts");*//*

            System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
            System.setProperty("javax.net.ssl.trustStoreType", "jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
            System.setProperty("javax.net.ssl.keyStore", "C:/OpenSSL-Win64/bin/client_merchant_cert_p12.p12");
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStorePassword", "Comtrust");
            URL ur = new URL("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");
            System.out.println(ur);
            MerchantAPI_Service m = new MerchantAPI_ServiceLocator();
            System.out.println(ur.getAuthority());
            MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(ur);

            AuthorizationRequest authorizationRequest = new AuthorizationRequest();
            authorizationRequest.setCustomer("Demo Merchant");
            authorizationRequest.setLanguage("ENG");
            authorizationRequest.setVersion(BigDecimal.valueOf(2.0));
            authorizationRequest.setAmount(BigDecimal.valueOf(10.00));
            authorizationRequest.setCardNumber("99900000000003");
            authorizationRequest.setCurrency("PKR");
            authorizationRequest.setExpiryMonth("12");
            authorizationRequest.setExpiryYear("16");
            authorizationRequest.setTransactionHint("CPT:Y");
            authorizationRequest.setVerifyCode("1234");
            authorizationRequest.setOrderID("NTNEncrypt ID");
            authorizationRequest.setOrderName("NTNEncrypt Name");


            AuthorizationResponse authorizationResponse = port.authorize(authorizationRequest);
            System.out.println("-------------------RESPONSE---------------");
            System.out.println(authorizationResponse.getResponseCode());
            System.out.println(authorizationResponse.getResponseDescription());
            System.out.println(authorizationResponse.getVersion());
            System.out.println(authorizationResponse.getAccount());
            System.out.println(authorizationResponse.getAmount());
            System.out.println(authorizationResponse.getApprovalCode());
            System.out.println(authorizationResponse.getBalance());
            System.out.println(authorizationResponse.getCardNumber());
            System.out.println(authorizationResponse.getCardToken());
            System.out.println(authorizationResponse.getFees());
            System.out.println(authorizationResponse.getTransactionID());

        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<Status>(new Status(0, "Exception"), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(1,"Successfull"), HttpStatus.OK);
    }

    @RequestMapping(value = "/testIpgFinalize" ,produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<?> testIPGFinalizationService(){
        try {
            */
/*System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\cacerts");*//*

            System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
            System.setProperty("javax.net.ssl.trustStoreType", "jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
            System.setProperty("javax.net.ssl.keyStore","C:/OpenSSL-Win64/bin/client_merchant_cert_p12.p12");
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.keyStorePassword","Comtrust");

            URL ur = new URL("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");
            System.out.println(ur);

            MerchantAPI_Service m=new MerchantAPI_ServiceLocator();
            System.out.println(ur.getAuthority());

            MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(ur);

            FinalizationRequest finalizationRequest= new FinalizationRequest();
            finalizationRequest.setCustomer("Demo Merchant");
            finalizationRequest.setLanguage("ENG");
            finalizationRequest.setVersion(BigDecimal.valueOf(2.0));
            finalizationRequest.setPassword("Comtrust");
            finalizationRequest.setTransactionID("738038854047");

            FinalizationResponse finalizationResponse = port.finalize(finalizationRequest);

            System.out.println("-------------------RESPONSE---------------");
            System.out.println(finalizationResponse.getResponseCode());
            System.out.println(finalizationResponse.getResponseDescription());
            System.out.println(finalizationResponse.getVersion());
            System.out.println(finalizationResponse.getAccount());
            System.out.println(finalizationResponse.getAmount());
            System.out.println(finalizationResponse.getApprovalCode());
            System.out.println(finalizationResponse.getBalance());
            System.out.println(finalizationResponse.getCardNumber());
            System.out.println(finalizationResponse.getCardToken());
            System.out.println(finalizationResponse.getFees());


        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return new ResponseEntity<Status>(new Status(0, "Exception"), HttpStatus.OK);
        }
        return new ResponseEntity<Status>(new Status(1,"Successfull"), HttpStatus.OK);
    }
}
*/
