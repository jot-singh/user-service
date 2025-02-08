package com.user.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    //Write the conversion for the role
    @Column(name = "role")
    private Role role;
}
