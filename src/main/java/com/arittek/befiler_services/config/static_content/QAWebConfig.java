package com.arittek.befiler_services.config.static_content;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"qa"})
public class QAWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(
                        "FBR/Residence/**",
                        "FBR/Bill/**",
                        "FBR/Cnic/**",
                        "TaxForm/**",
                        "notification/**",
                        "captcha/**",
                        "transaction/**",
                        "ibft/request/**")
                .addResourceLocations(
                        "file:///C:/Befiler_Static_Content/Qa/FBR/Residence/",
                        "file:///C:/Befiler_Static_Content/Qa/FBR/Bill/",
                        "file:///C:/Befiler_Static_Content/Qa/FBR/Cnic/",
                        "file:///C:/Befiler_Static_Content/Qa/TaxForm/",
                        "file:///C:/Befiler_Static_Content/Qa/notification/",
                        "file:///C:/base64/",
                        "file:///C:/Befiler_Static_Content/Qa/em/transaction/",
                        "file:///C:/Befiler_Static_Content/Qa/IBFT/Request/");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .favorPathExtension(true);
    }
}
