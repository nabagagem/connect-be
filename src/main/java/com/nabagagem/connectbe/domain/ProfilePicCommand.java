package com.nabagagem.connectbe.domain;

import org.springframework.web.multipart.MultipartFile;

public record ProfilePicCommand(String id, MultipartFile file) {
}
