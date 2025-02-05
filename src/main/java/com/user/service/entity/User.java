package com.user.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User extends BaseVO {
    private String username;
    private String password;
    private String email;
}
