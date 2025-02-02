package com.user.service.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class BaseVO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
