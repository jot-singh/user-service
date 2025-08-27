package com.user.service.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for username requirements
 */
@Documented
@Constraint(validatedBy = com.user.service.validation.validators.ValidUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
    
    String message() default "Username must be 3-50 characters and contain only letters, numbers, and underscores";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    int minLength() default 3;
    
    int maxLength() default 50;
    
    boolean allowReservedNames() default false;
}
