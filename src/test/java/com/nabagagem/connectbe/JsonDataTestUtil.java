package com.nabagagem.connectbe;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonDataTestUtil {

    @SuppressWarnings("DataFlowIssue")
    public static String loadJsonFromFile(final String filePath) {
        try {
            final InputStream jsonInputStream = ClassOrderer.ClassName.class.getClassLoader().getResourceAsStream(filePath);
            return IOUtils.toString(jsonInputStream, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            Assertions.fail(String.format("could not load json from file %s", filePath));
            throw new RuntimeException(e);
        }
    }
}
