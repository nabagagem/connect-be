package com.nabagagem.connectbe.controllers.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.controllers.LoginHelper;
import com.nabagagem.connectbe.domain.messages.*;
import com.nabagagem.connectbe.entities.Thread;
import com.nabagagem.connectbe.entities.*;
import com.nabagagem.connectbe.services.mappers.MessageMapper;
import com.nabagagem.connectbe.services.messages.MessageSearchService;
import com.nabagagem.connectbe.services.messages.MessageService;
import com.nabagagem.connectbe.services.messages.ThreadAuthService;
import com.nabagagem.connectbe.services.profile.SlugService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ThreadController.class)
@WithMockUser
class ThreadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService mockMessageService;
    @MockBean
    private SlugService mockSlugService;
    @MockBean
    private ThreadAuthService mockThreadAuthService;
    @MockBean
    private MessageMapper mockMessageMapper;
    @MockBean
    private MessageSearchService mockMessageSearchService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoginHelper loginHelper;

    @Test
    void testGetThreads() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        UUID id = UUID.fromString("c10e578e-161b-492f-8992-9bb9e56d9c22");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(id);

        when(mockMessageService.getThreadsFor(id))
                .thenReturn(List.of(
                        new ProfileThreadItem() {
                            @Override
                            public UUID getId() {
                                return id;
                            }

                            @Override
                            public UUID getRecipientId() {
                                return id;
                            }

                            @Override
                            public Boolean getRecipientPublicProfile() {
                                return true;
                            }

                            @Override
                            public ThreadStatus getStatus() {
                                return ThreadStatus.OPEN;
                            }

                            @Override
                            public String getRecipientName() {
                                return "foobar";
                            }

                            @Override
                            public UUID getSenderId() {
                                return id;
                            }

                            @Override
                            public String getSenderName() {
                                return "sender";
                            }

                            @Override
                            public ZonedDateTime getLastMessageAt() {
                                return ZonedDateTime.of(2023, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault());
                            }

                            @Override
                            public String getLastMessageText() {
                                return "text";
                            }

                            @Override
                            public String getLastModifiedBy() {
                                return "sender";
                            }

                            @Override
                            public Integer getUnreadCount() {
                                return 10;
                            }

                            @Override
                            public MessageType getLastMessageType() {
                                return MessageType.TEXT;
                            }
                        }
                ));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/threads", "id")
                        .with(user("user"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                [{"id":"c10e578e-161b-492f-8992-9bb9e56d9c22","status":"OPEN","senderId":"c10e578e-161b-492f-8992-9bb9e56d9c22","lastMessageAt":"2023-02-01T00:00:00+01:00",
                "recipientPublicProfile":true,"recipientName":"foobar","senderName":"sender","lastMessageText":"text","lastModifiedBy":"sender","unreadCount":10,
                "lastMessageType":"TEXT","recipientId":"c10e578e-161b-492f-8992-9bb9e56d9c22"}]
                """);

    }

    @Test
    void testGetThreads_MessageServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SlugService.getProfileIdFrom(...).
        final UUID uuid = UUID.fromString("c10e578e-161b-492f-8992-9bb9e56d9c22");
        when(mockSlugService.getProfileIdFrom("id")).thenReturn(uuid);

        when(mockMessageService.getThreadsFor(UUID.fromString("c10e578e-161b-492f-8992-9bb9e56d9c22")))
                .thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/profile/{id}/threads", "id")
                        .with(user("username"))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    void testGetMessages() throws Exception {
        // Setup
        // Configure MessageService.getMessagesFrom(...).
        final List<Message> messages = List.of(Message.builder()
                .id(UUID.fromString("5503928c-c52d-42c8-8237-a0be8dac6f2d"))
                .thread(Thread.builder()
                        .id(UUID.fromString("534a1eb1-2a42-459c-a7e0-5412fbbfeea8"))
                        .build())
                .media(Media.builder()
                        .id(UUID.fromString("ab85e966-6b08-456f-91b8-27df42bf1cde"))
                        .build())
                .build());
        when(mockMessageService.getMessagesFrom(UUID.fromString("1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1")))
                .thenReturn(messages);

        // Configure MessageMapper.toDto(...).
        final ThreadMessage message = new ThreadMessage(UUID.fromString("5f2bea5a-2874-40cf-8d8a-bdece9394fb9"),
                UUID.fromString("0897abcc-4183-4e01-894c-3c92b2fc3e44"), "message", "sentBy",
                new URL("https://example.com/"), new MediaType("type", "subtype", StandardCharsets.UTF_8),
                "mediaOriginalName", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), false,
                Set.of(new ThreadMessageReaction(UUID.fromString("cb4bf452-e941-41e6-b729-e13980121143"), "reaction",
                        "createdBy")), MessageType.TEXT, false);
        when(mockMessageMapper.toDto(Message.builder()
                .id(UUID.fromString("5503928c-c52d-42c8-8237-a0be8dac6f2d"))
                .thread(Thread.builder()
                        .id(UUID.fromString("534a1eb1-2a42-459c-a7e0-5412fbbfeea8"))
                        .build())
                .media(Media.builder()
                        .id(UUID.fromString("ab85e966-6b08-456f-91b8-27df42bf1cde"))
                        .build())
                .build())).thenReturn(message);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/threads/{threadId}", "1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                [{"id":"5f2bea5a-2874-40cf-8d8a-bdece9394fb9","threadId":"0897abcc-4183-4e01-894c-3c92b2fc3e44","message":"message",
                "sentBy":"sentBy","fileUrl":"https://example.com/","mediaType":{"type":"type","subtype":"subtype",
                "parameters":{"charset":"UTF-8"},"qualityValue":1.0,"wildcardType":false,"wildcardSubtype":false,"subtypeSuffix":null,
                "charset":"UTF-8","concrete":true},"mediaOriginalName":"mediaOriginalName","sentAt":"2020-01-01T00:00:00Z","read":false,
                "reactions":[{"id":"cb4bf452-e941-41e6-b729-e13980121143","reaction":"reaction","createdBy":"createdBy"}],"messageType":"TEXT","textUpdated":false}]
                 """);
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1"));
    }

    @Test
    void testGetMessages_MessageServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockMessageService.getMessagesFrom(UUID.fromString("1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1")))
                .thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/threads/{threadId}", "1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("1c01af56-6cbb-42bd-86bd-e8dfdab9dfd1"));
    }

    @Test
    void testGetPage() throws Exception {
        // Setup
        // Configure MessageSearchService.getMessagesPageFrom(...).
        Message message1 = Message.builder()
                .id(UUID.fromString("5503928c-c52d-42c8-8237-a0be8dac6f2d"))
                .thread(Thread.builder()
                        .id(UUID.fromString("534a1eb1-2a42-459c-a7e0-5412fbbfeea8"))
                        .build())
                .media(Media.builder()
                        .id(UUID.fromString("ab85e966-6b08-456f-91b8-27df42bf1cde"))
                        .build())
                .build();
        final Page<Message> messages = new PageImpl<>(List.of(message1));
        when(mockMessageSearchService.getMessagesPageFrom(eq(UUID.fromString("aa967b4e-5a76-45a1-8a55-810bcbe5fd1d")),
                any(Pageable.class), eq(new MessageSearchParams("foobar", null, null, null)))).thenReturn(messages);

        // Configure MessageMapper.toDto(...).
        final ThreadMessage message = new ThreadMessage(UUID.fromString("e6ec6e51-f8f4-4064-98fa-b71aa6e6c479"),
                UUID.fromString("63292ac2-4393-4aa2-9f4e-f79c130faede"), "message", "sentBy",
                new URL("https://example.com/"), new MediaType("type", "subtype", StandardCharsets.UTF_8),
                "mediaOriginalName", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), false,
                Set.of(new ThreadMessageReaction(UUID.fromString("01c2f0c8-8789-4b48-b69e-875884bad909"), "reaction",
                        "createdBy")), MessageType.TEXT, false);
        when(mockMessageMapper.toDto(message1)).thenReturn(message);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v2/threads/{threadId}?expression=foobar", "aa967b4e-5a76-45a1-8a55-810bcbe5fd1d")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[{"id":"e6ec6e51-f8f4-4064-98fa-b71aa6e6c479","threadId":"63292ac2-4393-4aa2-9f4e-f79c130faede",
                "message":"message","sentBy":"sentBy","fileUrl":"https://example.com/","mediaType":{"type":"type","subtype":"subtype",
                "parameters":{"charset":"UTF-8"},"qualityValue":1.0,"wildcardType":false,"wildcardSubtype":false,"subtypeSuffix":null,
                "charset":"UTF-8","concrete":true},"mediaOriginalName":"mediaOriginalName","sentAt":"2020-01-01T00:00:00Z","read":false,
                "reactions":[{"id":"01c2f0c8-8789-4b48-b69e-875884bad909","reaction":"reaction","createdBy":"createdBy"}],"messageType":"TEXT","textUpdated":false}],
                "pageable":"INSTANCE","totalElements":1,"totalPages":1,"last":true,"size":1,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},
                "numberOfElements":1,"first":true,"empty":false}
                 """);
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("aa967b4e-5a76-45a1-8a55-810bcbe5fd1d"));
    }

    @Test
    void testGetPage_MessageSearchServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockMessageSearchService.getMessagesPageFrom(eq(UUID.fromString("aa967b4e-5a76-45a1-8a55-810bcbe5fd1d")),
                any(Pageable.class), eq(new MessageSearchParams(null, null, null, null))))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Configure MessageMapper.toDto(...).
        final ThreadMessage message = new ThreadMessage(UUID.fromString("e6ec6e51-f8f4-4064-98fa-b71aa6e6c479"),
                UUID.fromString("63292ac2-4393-4aa2-9f4e-f79c130faede"), "message", "sentBy",
                new URL("https://example.com/"), new MediaType("type", "subtype", StandardCharsets.UTF_8),
                "mediaOriginalName", ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 0, 0, 0), ZoneOffset.UTC), false,
                Set.of(new ThreadMessageReaction(UUID.fromString("01c2f0c8-8789-4b48-b69e-875884bad909"), "reaction",
                        "createdBy")), MessageType.TEXT, false);
        when(mockMessageMapper.toDto(Message.builder()
                .id(UUID.fromString("5503928c-c52d-42c8-8237-a0be8dac6f2d"))
                .thread(Thread.builder()
                        .id(UUID.fromString("534a1eb1-2a42-459c-a7e0-5412fbbfeea8"))
                        .build())
                .media(Media.builder()
                        .id(UUID.fromString("ab85e966-6b08-456f-91b8-27df42bf1cde"))
                        .build())
                .build())).thenReturn(message);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v2/threads/{threadId}", "aa967b4e-5a76-45a1-8a55-810bcbe5fd1d")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[],"pageable":"INSTANCE","totalElements":0,"totalPages":1,"last":true,"size":0,
                "number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},
                "numberOfElements":0,"first":true,"empty":true}
                """);
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("aa967b4e-5a76-45a1-8a55-810bcbe5fd1d"));
    }

    @Test
    void testPatch() throws Exception {
        // Setup
        // Run the test
        PatchThreadPayload patchThreadPayload = new PatchThreadPayload(ThreadStatus.OPEN);
        final MockHttpServletResponse response = mockMvc.perform(
                        patch("/api/v1/threads/{threadId}", "6c8469b3-88e4-417f-8c69-b3479731448a")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(patchThreadPayload))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockThreadAuthService).failIfUnableToUpdate(UUID.fromString("6c8469b3-88e4-417f-8c69-b3479731448a"),
                patchThreadPayload);
        verify(mockMessageService).updateThread(UUID.fromString("6c8469b3-88e4-417f-8c69-b3479731448a"),
                patchThreadPayload);
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/threads/{threadId}", "9a773975-4186-4efe-8025-65b6478825b2")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockThreadAuthService).failIfUnableToDelete(UUID.fromString("9a773975-4186-4efe-8025-65b6478825b2"));
        verify(mockMessageService).deleteThread(UUID.fromString("9a773975-4186-4efe-8025-65b6478825b2"));
    }

    @Test
    void testCreate() throws Exception {
        // Setup
        // Run the test
        TextPayload text = new TextPayload("text");
        final MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/threads/{threadId}/messages", "fd5fdd83-9b95-4c8c-b032-9c7a038fc119")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(text))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(mockThreadAuthService).failIfUnableToRead(UUID.fromString("fd5fdd83-9b95-4c8c-b032-9c7a038fc119"));
        verify(mockMessageService).create(
                new ThreadMessageCommand(UUID.fromString("fd5fdd83-9b95-4c8c-b032-9c7a038fc119"),
                        text));
    }

    @Test
    void testPost() throws Exception {
        // Setup
        // Configure MessageService.send(...).
        final Message message = Message.builder()
                .id(UUID.fromString("5503928c-c52d-42c8-8237-a0be8dac6f2d"))
                .thread(Thread.builder()
                        .id(UUID.fromString("534a1eb1-2a42-459c-a7e0-5412fbbfeea8"))
                        .build())
                .media(Media.builder()
                        .id(UUID.fromString("ab85e966-6b08-456f-91b8-27df42bf1cde"))
                        .build())
                .build();
        SendMessagePayload sendMessagePayload = new SendMessagePayload(UUID.fromString("1539dfd0-168c-4f17-ae82-85968fe1a861"),
                UUID.fromString("e26aa1cd-1fc5-43b1-a05f-eea7464427a3"), "message");
        SendMessageCommand sendMessageCommand = new SendMessageCommand(UUID.fromString("8be19a60-ed9e-41dc-adce-e2af603db0bf"),
                sendMessagePayload);
        when(mockMessageService.send(sendMessageCommand)).thenReturn(message);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/api/v1/threads")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(sendMessageCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"threadId":"534a1eb1-2a42-459c-a7e0-5412fbbfeea8","messageId":"5503928c-c52d-42c8-8237-a0be8dac6f2d"}
                """);
    }
}
