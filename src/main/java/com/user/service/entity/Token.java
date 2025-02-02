package com.user.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Token extends BaseVO {
    @OneToOne
    private User user;
    private String value;
    private Date expireAt;
    private boolean deleted;
}
