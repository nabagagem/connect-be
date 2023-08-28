package com.nabagagem.connectbe.domain.messages;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateMpAudioCommand(UUID threadId, MultipartFile file) {
}
