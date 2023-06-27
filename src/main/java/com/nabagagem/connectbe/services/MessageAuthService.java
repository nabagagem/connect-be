package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.MessagePatchPayload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class MessageAuthService {
    public void failIfUnableToWriteOnThread(UUID threadId) {

    }

    public void failIfUnableToDelete(UUID id) {

    }

    public void failIfUnableToPatch(UUID id, MessagePatchPayload messagePatchPayload) {

    }

    public void failIfUnableToRead(UUID id) {

    }
}
