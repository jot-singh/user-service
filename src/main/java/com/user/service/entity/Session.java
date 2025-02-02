package com.user.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
public class Session extends BaseVO {
    private String username;
    private String token;
    private String expiredAt;
    private String sessionId;
}
