package com.arittek.befiler_services.model.generic;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GenericModelAudit implements Serializable {

    @CreatedBy
    private Integer createdBy;

    @CreatedDate
    private Timestamp createdDate;

    @LastModifiedBy
    private Integer lastModifiedBy;

    @LastModifiedDate
    private Timestamp lastModifiedDate;

    @Version
    private Integer version;
}
