package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.FilePurpose;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record JobFileCommand(UUID jobId, FilePurpose filePurpose, Integer position, MultipartFile file) {
}
