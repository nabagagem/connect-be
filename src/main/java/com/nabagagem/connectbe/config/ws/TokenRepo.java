package com.nabagagem.connectbe.config.ws;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TokenRepo extends LinkedHashMap<String, String> {
    private static final Integer MAX_ENTRIES = 100;

    public TokenRepo() {
        super(MAX_ENTRIES + 1, 1.0f, true);
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<String, String> eldest) {
        return super.size() > MAX_ENTRIES;
    }
}
