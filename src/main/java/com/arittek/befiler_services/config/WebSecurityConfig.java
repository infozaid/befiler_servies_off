package com.arittek.befiler_services.config;

import com.arittek.befiler_services.security.JwtAuthenticationEntryPoint;
import com.arittek.befiler_services.security.JwtAuthorizationTokenFilter;
import com.arittek.befiler_services.security.JwtTokenUtil;
import com.arittek.befiler_services.security.UserAuthenticationProvider;
import com.arittek.befiler_services.security.service.JwtUserDetailsService;
import com.arittek.befiler_services.services.user.UsersServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletContext;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private UsersServices usersServices;

    @Autowired
    private ServletContext servletContext;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .authenticationProvider(userAuthenticationProvider)
                .userDetailsService(this.jwtUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // allow anonymous resource requests
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/*.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.jpg",
                        "/**/*.pdf",
                        "/**/*.jpeg",
                        "/**/*.png",
                        "/**/*.svg"

                ).permitAll()
                /*.antMatchers("/public*//**", "/resources*//**","/resources/static*//**","/static*//**" ).permitAll()*/
                /*.antMatchers("/auth*").permitAll()             BEFORE
                .antMatchers("/taxform*").permitAll()
                .antMatchers("/taxform*//**").permitAll()
                .antMatchers("/users*").permitAll()
                .antMatchers("/users*//**").permitAll()
                .antMatchers("/fbr*//**").permitAll()
                .antMatchers("/fbr*").permitAll()*/
                .antMatchers("/auth*").permitAll()
//                .antMatchers("/notification/get").permitAll()
                .antMatchers("/verifyPin").permitAll()
                .antMatchers("/users/registration").permitAll()
                .antMatchers("/users/forgotPassword").permitAll()
                .antMatchers("/users/createPassword").permitAll()
                .antMatchers("/users/createPassword/*").permitAll()
                .antMatchers("/users/isEmailExist").permitAll()
                .antMatchers("/users/isCnicExist/*").permitAll()
                .antMatchers("/fbr/getFederalNTN").permitAll()
                .antMatchers("/fbr*").permitAll()
                .antMatchers("/fbr/*").permitAll()
                .antMatchers("/users/isMobileNoExist/*").permitAll()
                .antMatchers("/taxform/detailViewForFbr*").permitAll()
                .antMatchers("/testIpg*").permitAll()
                .antMatchers("/testIpg/*").permitAll()
                .antMatchers("/users/userAndFbrRegistration/").permitAll()
                .antMatchers("/users/userAndFbrRegistration/*").permitAll()
                .antMatchers("/fbr/verification*").permitAll()
                .antMatchers("/fbr/verification/*").permitAll()
                .antMatchers("/fbr/taxpayer*").permitAll()
                .antMatchers("/fbr/taxpayer/*").permitAll()

                .antMatchers("//suggestUs*").permitAll()
                .antMatchers("//suggestUs/*").permitAll()
                .antMatchers("/fbr/taxpayer/getByRegistrationNo/*").permitAll()

                .antMatchers("/api/customer/registration*").permitAll()
                .antMatchers("/api/customer/registration/*").permitAll()

                .antMatchers("/keenu/response*").permitAll()
                .antMatchers("/keenu/response/*").permitAll()

                .antMatchers("/easypaisa/confirm*").permitAll()
                .antMatchers("/easypaisa/confirm/*").permitAll()

                .antMatchers("/easypaisa/ipn*").permitAll()
                .antMatchers("/easypaisa/ipn/*").permitAll()

                .antMatchers("/swagger-resources*").permitAll()
                .antMatchers("/swagger-resources/*").permitAll()
                .antMatchers("/swagger-resources/*/").permitAll()
                .antMatchers("/swagger-resources/*/*").permitAll()
                .antMatchers("/v2/api-docs").permitAll()

                .antMatchers("/paymentNotification*").permitAll()
                .antMatchers("/paymentNotification/*").permitAll()
                .antMatchers("/paymentNotification/*/*").permitAll()

                /*.antMatchers("/dashboard*").permitAll()*/
               /* .antMatchers("/taxform/calculator*//*").permitAll()
                .antMatchers("/taxform*//*").permitAll()*/
                .anyRequest().authenticated();

        // Custom JWT based security filter
        JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), jwtTokenUtil, tokenHeader, usersServices, servletContext);
        httpSecurity
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();
    }


}
