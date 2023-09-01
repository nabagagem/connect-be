package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.Emoji;
import com.vdurmont.emoji.EmojiManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmojiValidator implements ConstraintValidator<Emoji, String> {

    private boolean isEmoji(String message) {
        return EmojiManager.isEmoji(message);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isEmoji(value);
    }
}
