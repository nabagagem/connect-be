package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.ProfileRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class DeleteUserService {
    private final ProfileRepo profileRepo;

    public void delete(UUID id) {
        profileRepo.findById(id).ifPresent(profileRepo::delete);
    }
}
