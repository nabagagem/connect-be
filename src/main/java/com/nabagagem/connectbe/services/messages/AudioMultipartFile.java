package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.AudioPayload;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public record AudioMultipartFile(AudioPayload audioPayload) implements MultipartFile {
    @Override
    public String getName() {
        return "audio";
    }

    @Override
    public String getOriginalFilename() {
        return "audio";
    }

    @Override
    public String getContentType() {
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return audioPayload.content().getBytes().length;
    }

    @Override
    public byte[] getBytes() {
        return audioPayload.content().getBytes();
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(audioPayload.content().getBytes());
    }

    @Override
    public void transferTo(File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("not implemented");
    }
}
