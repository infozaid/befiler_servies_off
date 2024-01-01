/*
package com.arittek.befiler_services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SSLConfig {

    @Value("${ipg.trustStore}")
    private String trustStore;
    @Value("${ipg.trustStoreType}")
    private String trustStoreType;
    @Value("${ipg.trustStorePassword}")
    private String trustStorePassword;

    @Value("${ipg.keyStore}")
    private String keyStore;
    @Value("${ipg.keyStoreType}")
    private String keyStoreType;
    @Value("${ipg.keyStorePassword}")
    private String keyStorePassword;

    @PostConstruct
    private void configureSSL() {
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
        System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }
}
*/
