package com.arittek.befiler_services.security.service;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.security.JwtUserFactory;
import com.arittek.befiler_services.services.user.UsersServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UsersServices userServices;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userServices.findOneByEmailAddress(username);
            if (user == null) {
                throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
            } else {
                return JwtUserFactory.create(user);
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        }
    }
}
