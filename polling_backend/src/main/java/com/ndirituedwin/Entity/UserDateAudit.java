package com.ndirituedwin.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ndirituedwin.Entity.DateAudit;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@JsonIgnoreProperties(value = {"createdBy","updatedBy"},allowGetters = true)
@Data
public abstract class UserDateAudit extends DateAudit {

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;
    @LastModifiedBy
    private Long updatedBy;

}
