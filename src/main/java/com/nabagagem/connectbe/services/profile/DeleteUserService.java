package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.repos.ProfileRepo;
import com.nabagagem.connectbe.services.jobs.JobService;
import com.nabagagem.connectbe.services.messages.MessageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class DeleteUserService {
    private final ProfileRepo profileRepo;
    private final JobService jobService;
    private final MessageService messageService;

    public void delete(UUID id) {
        messageService.deleteForUser(id);
        jobService.deleteForUser(id);
        profileRepo.findById(id).ifPresent(profileRepo::delete);
    }
}
