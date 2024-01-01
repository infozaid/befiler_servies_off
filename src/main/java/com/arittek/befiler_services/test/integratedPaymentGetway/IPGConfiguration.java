package com.arittek.befiler_services.test.integratedPaymentGetway;

import org.springframework.context.annotation.Configuration;

@Configuration
public class IPGConfiguration {

    /*@Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("com.arittek.integratedPaymentGetway.wsdl");
        return marshaller;
    }

    @Bean
    public TestPaymentGetway quoteClient(Jaxb2Marshaller marshaller) {
        TestPaymentGetway client = new TestPaymentGetway();
        client.setDefaultUri("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }*/
}
