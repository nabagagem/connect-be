package com.nabagagem.connectbe.services.messages;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EmojiValidatorTest {

    private EmojiValidator emojiValidatorUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        emojiValidatorUnderTest = new EmojiValidator();
    }

    @Test
    void testIsEmoji() {
        // Setup
        // Run the test
        final boolean result = emojiValidatorUnderTest.isValid(
                "message " + EmojiManager.getAll().stream().findAny().map(Emoji::getUnicode).orElse(""), null);

        // Verify the results
        assertThat(result).isFalse();
    }

    @TestFactory
    Stream<DynamicTest> testIsNotEmoji() {
        return EmojiManager.getAll()
                .stream().map(Emoji::getUnicode)
                .map(emoji -> DynamicTest.dynamicTest(emoji, () -> {
                    final boolean result = emojiValidatorUnderTest.isValid(emoji, null);
                    // Verify the results
                    assertThat(result).isTrue();
                }));
    }
}
