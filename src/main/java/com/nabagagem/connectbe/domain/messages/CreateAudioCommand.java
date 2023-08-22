package com.nabagagem.connectbe.domain.messages;

import java.util.UUID;

public record CreateAudioCommand(UUID threadId, AudioPayload audioPayload) {
}
