package com.nabagagem.connectbe.domain.messages;

import com.nabagagem.connectbe.services.messages.EmojiValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmojiValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Emoji {
    String message() default "Invalid emoji";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
