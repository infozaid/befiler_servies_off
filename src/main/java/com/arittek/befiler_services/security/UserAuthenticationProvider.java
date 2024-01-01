package com.arittek.befiler_services.security;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.security.service.JwtUserDetailsService;
import com.arittek.befiler_services.services.user.UsersServices;
import com.arittek.befiler_services.util.CommonUtil;
import com.arittek.befiler_services.util.EncoderDecoder;
import com.arittek.befiler_services.util.Logger4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider  implements AuthenticationProvider {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersServices usersServices;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String userName = authentication.getName().trim();
            String password = EncoderDecoder.getEncryptedSHA1Password(authentication.getCredentials().toString().trim());
            Authentication auth = null;

            User user = usersServices.findOneByCnicNoAndPassword(userName, password);
            if(user == null){

                user = usersServices.findOneByEmailAddressAndPassword(userName, password);

            }

            if (user != null && user.getStatus() != null && (user.getStatus().getId() == 1 || user.getStatus().getId() == 0) ) {
                UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getEmailAddress());
                auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
                return auth;
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger4j.getLogger().error("Exception Root: " + CommonUtil.getRootCause(e).getMessage());
            Logger4j.getLogger().error("Exception : " , e);
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }


}
