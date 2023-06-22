package com.nabagagem.connectbe.controllers.messages;

import com.nabagagem.connectbe.entities.Media;
import com.nabagagem.connectbe.entities.Message;
import lombok.SneakyThrows;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public interface MessageMediaUrlTrait {
    @SneakyThrows
    default URL getUrlFrom(Message message) {
        return Optional.ofNullable(message.getMedia())
                .map(Media::getId)
                .map(__ -> {
                    try {
                        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/v1/messages/{id}/file")
                                .build(message.getId()).toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(null);
    }
}
