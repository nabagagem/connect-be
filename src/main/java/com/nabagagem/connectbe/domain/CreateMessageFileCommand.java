package com.nabagagem.connectbe.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateMessageFileCommand(@NotNull MultipartFile file,
                                       @NotNull UUID threadId) {
}
