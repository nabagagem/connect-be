package com.nabagagem.connectbe.domain.profile;

import org.springframework.web.multipart.MultipartFile;

public record ProfilePicCommand(java.util.UUID id, MultipartFile file) {
}
