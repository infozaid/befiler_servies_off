package com.arittek.befiler_services.security;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.model.user.role_permission.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        boolean enabled = user.getStatus().getId()==1 ? true : false;
        return new JwtUser(
                user.getId(),
                user.getEmailAddress(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getRoles()),
                enabled,
                user.getLastPasswordResetDate()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(Set<Role> userRoleTypes) {
        return userRoleTypes.stream()
                .map(userRoleType -> new SimpleGrantedAuthority(userRoleType.getName()))
                .collect(Collectors.toList());
    }
}
