package com.user.service.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for strong password requirements
 */
@Documented
@Constraint(validatedBy = com.user.service.validation.validators.StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    
    String message() default "Password must be strong (8+ chars, uppercase, lowercase, number, special char)";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    int minLength() default 8;
    
    int maxLength() default 128;
    
    boolean requireUppercase() default true;
    
    boolean requireLowercase() default true;
    
    boolean requireDigit() default true;
    
    boolean requireSpecialChar() default true;
}
