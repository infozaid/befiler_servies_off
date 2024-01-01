package com.arittek.befiler_services.config.audit;

import com.arittek.befiler_services.security.JwtUser;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) revisionEntity;
        if (auth != null && auth.getPrincipal() != null && !auth.getPrincipal().equals("anonymousUser")) {
            auditRevisionEntity.setUserId(((JwtUser) auth.getPrincipal()).getId());
        } else  {
            auditRevisionEntity.setUserId(0);
        }

    }

}
