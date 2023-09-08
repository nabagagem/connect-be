package com.nabagagem.connectbe.domain.job;

import com.nabagagem.connectbe.domain.FilePurpose;

import java.util.UUID;

public record DeleteJobFileCommand(UUID jobId, FilePurpose filePurpose, Integer position) {
}
