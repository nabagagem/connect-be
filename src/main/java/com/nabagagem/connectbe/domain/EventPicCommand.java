package com.nabagagem.connectbe.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record EventPicCommand(UUID id, MultipartFile file) {
}
