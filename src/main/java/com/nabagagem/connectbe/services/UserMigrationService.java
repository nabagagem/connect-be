package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.repos.ProfileRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class UserMigrationService {
    private final ProfileRepo profileRepo;
    private final RestTemplate cognitoRestTemplate;


    public void checkAndMigrate() {
    }
}
