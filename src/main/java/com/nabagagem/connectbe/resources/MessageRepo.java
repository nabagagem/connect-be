package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepo extends CrudRepository<Message, UUID> {

    List<Message> findByThreadId(UUID threadId);
}