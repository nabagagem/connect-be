package com.nabagagem.connectbe.domain.profile;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateMessageFileCommand(MultipartFile file,
                                       String text,
                                       @NotNull UUID threadId) {
}
