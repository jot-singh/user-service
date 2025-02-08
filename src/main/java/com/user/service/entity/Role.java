package com.user.service.entity;

import java.util.Arrays;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private String name;

    Role(String role) {
        this.name = role;
    }

    public String getName() {
        return name;
    }

    public static String[] getRoles() {
        return Arrays.stream(Role.values()).map(Role::getName).toArray(String[]::new);
    }

    public static String value(){
        return Role.value();
    }
}
