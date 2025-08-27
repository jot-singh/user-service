package com.user.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    
    @Column(name = "token_value")
    private String value;
    
    private Date expireAt;
    private boolean deleted;
}
