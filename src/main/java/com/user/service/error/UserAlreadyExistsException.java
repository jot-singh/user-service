package com.user.service.error;

public class UserAlreadyExistsException extends Throwable {
    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
