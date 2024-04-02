package com.nabagagem.connectbe.config.security;

public record TokenRequest(
        String grant_type,
        String code,
        String client_id,
        String redirect_uri
) {
}
