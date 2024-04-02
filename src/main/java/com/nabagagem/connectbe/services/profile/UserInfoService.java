package com.nabagagem.connectbe.services.profile;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.nabagagem.connectbe.repos.LegacyUserRepo;
import com.nabagagem.connectbe.repos.UserInfoRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.nabagagem.connectbe.repos.UserInfoRepo.CognitoUserInfo;

@Slf4j
@Service
@AllArgsConstructor
public class UserInfoService {
    private final UserInfoRepo userInfoRepo;
    private final LegacyUserRepo legacyUserRepo;
    private final JwtDecoder jwtDecoder;

    public UserInfo getCurrentUserInfo(String idToken) {
        CognitoUserInfo currentUserInfo = Optional.ofNullable(idToken)
                .map(jwtDecoder::decode)
                .map(this::toCognitoUser)
                .orElseGet(userInfoRepo::getCurrentUserInfo);
        log.info("Current user info: {}", currentUserInfo);
        return new UserInfo(
                currentUserInfo,
                loadLegacyUser(currentUserInfo.email())
                        .map(UUID::fromString)
                        .orElse(currentUserInfo.sub())
        );
    }

    private CognitoUserInfo toCognitoUser(Jwt jwt) {
        log.info("Decrypting email from jwt {}", jwt.getSubject());
        String email = jwt.getClaimAsString("email");
        return new CognitoUserInfo(
                UUID.fromString(jwt.getSubject()),
                email,
                email
        );
    }

    private Optional<String> loadLegacyUser(String email) {
        try {
            return legacyUserRepo.getLegacyUserId(email);
        } catch (Exception e) {
            log.warn("Failed to load keycloak legacy id for {}", email, e);
            return Optional.empty();
        }
    }

    public record UserInfo(
            @JsonUnwrapped CognitoUserInfo cognitoUserInfo,
            UUID userId
    ) {

    }

}
