package com.arittek.befiler_services.config.audit;

import com.arittek.befiler_services.model.user.User;
import com.arittek.befiler_services.security.JwtUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.domain.AuditorAware#getCurrentAuditor()
     */
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null && !auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of(((JwtUser) auth.getPrincipal()).getId());
        } else {
            return Optional.of(0);
        }
    }

}
