package com.arittek.befiler_services.config.static_content;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"dev"})
public class DevWebConfig implements WebMvcConfigurer {

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
                        "file:///C:/Befiler_Static_Content/Dev/FBR/Residence/",
                        "file:///C:/Befiler_Static_Content/Dev/FBR/Bill/",
                        "file:///C:/Befiler_Static_Content/Dev/FBR/Cnic/",
                        "file:///C:/Befiler_Static_Content/Dev/TaxForm/",
                        "file:///C:/Befiler_Static_Content/Dev/notification/",
                        "file:///C:/base64/",
                        "file:///C:/Befiler_Static_Content/Dev/em/transaction/",
                        "file:///C:/Befiler_Static_Content/Dev/IBFT/Request/");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .favorPathExtension(true);
    }
}
