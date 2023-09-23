package com.nabagagem.connectbe.util;

import java.util.Optional;
import java.util.UUID;

public class UuidHelper {
    public static UUID fromString(String string) {
        try {
            return Optional.ofNullable(string)
                    .map(UUID::fromString)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
