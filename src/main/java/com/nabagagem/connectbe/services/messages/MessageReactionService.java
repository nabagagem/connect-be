package com.nabagagem.connectbe.services.messages;

import com.nabagagem.connectbe.domain.messages.ReactionPayload;
import com.nabagagem.connectbe.entities.Message;
import com.nabagagem.connectbe.entities.Reaction;
import com.nabagagem.connectbe.repos.ReactionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class MessageReactionService {
    private final ReactionRepository reactionRepository;

    public Reaction create(UUID messageId,
                           ReactionPayload reactionPayload) {
        return reactionRepository.save(
                Reaction.builder()
                        .reaction(reactionPayload.reaction())
                        .message(Message.builder().id(messageId).build())
                        .build());
    }

    public void delete(UUID reactionId) {
        reactionRepository.deleteById(reactionId);
    }
}
