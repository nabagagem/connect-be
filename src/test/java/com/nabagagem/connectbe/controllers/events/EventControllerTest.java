package com.nabagagem.connectbe.controllers.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabagagem.connectbe.domain.notification.EventItemPayload;
import com.nabagagem.connectbe.domain.notification.EventPayload;
import com.nabagagem.connectbe.domain.notification.EventSearchParams;
import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.mappers.EventMapper;
import com.nabagagem.connectbe.services.events.EventAuthService;
import com.nabagagem.connectbe.services.events.EventService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EventController.class)
@WithMockUser
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService mockEventService;
    @MockBean
    private EventAuthService eventAuthService;
    @MockBean
    private EventMapper mockEventMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPost() throws Exception {
        // Setup
        // Configure EventService.create(...).
        final Event event = Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build();
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");
        when(mockEventService.create(eventPayload)).thenReturn(event);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/api/v1/events")
                        .content(objectMapper.writeValueAsString(eventPayload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void testGet() throws Exception {
        // Setup
        // Configure EventService.getById(...).
        final Optional<Event> event = Optional.of(Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build());
        when(mockEventService.getById(UUID.fromString("e43195ba-6ec7-4a19-8921-a285f5080f12"))).thenReturn(event);

        // Configure EventMapper.toDto(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");
        when(mockEventMapper.toDto(Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build())).thenReturn(eventPayload);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/events/{id}", "e43195ba-6ec7-4a19-8921-a285f5080f12")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"title":"title","eventDate":"2020-01-01","eventMode":"ONLINE","eventType":"WORKSHOP",
                "address":"address","price":{"amount":0.00,"currency":"EUR"},"externalLink":"externalLink",
                "description":"description"}
                """);
    }

    @Test
    void testGet_EventServiceReturnsAbsent() throws Exception {
        // Setup
        when(mockEventService.getById(UUID.fromString("e43195ba-6ec7-4a19-8921-a285f5080f12")))
                .thenReturn(Optional.empty());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/events/{id}", "e43195ba-6ec7-4a19-8921-a285f5080f12")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/events/{id}", "2d2c21a7-1153-4db8-98cc-0cd085f972a0")
                                .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(mockEventService).delete(UUID.fromString("2d2c21a7-1153-4db8-98cc-0cd085f972a0"));
    }

    @Test
    void testList() throws Exception {
        // Setup
        // Configure EventService.listBy(...).
        final Page<Event> events = new PageImpl<>(List.of(Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build()));
        when(mockEventService.listBy(eq(new EventSearchParams(List.of(EventMode.ONLINE), List.of(EventType.WORKSHOP),
                null, null)), any(Pageable.class))).thenReturn(events);

        // Configure EventMapper.toItemDto(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventItemPayload eventItemPayload = new EventItemPayload(
                UUID.fromString("953e0262-456b-4ab4-8f65-db6791f0942a"), "title", LocalDate.of(2020, 1, 1),
                EventMode.ONLINE, EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");
        when(mockEventMapper.toItemDto(Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build())).thenReturn(eventItemPayload);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/events")
                        .queryParam("eventMode", EventMode.ONLINE.toString())
                        .queryParam("eventType", EventType.WORKSHOP.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[{"id":"953e0262-456b-4ab4-8f65-db6791f0942a","title":"title",
                "eventDate":"2020-01-01","eventMode":"ONLINE","eventType":"WORKSHOP","address":"address",
                "price":{"amount":0.00,"currency":"EUR"},"externalLink":"externalLink","description":"description"}],
                "pageable":"INSTANCE","totalPages":1,"totalElements":1,"last":true,"size":1,"number":0,
                "sort":{"empty":true,"sorted":false,"unsorted":true},"numberOfElements":1,"first":true,"empty":false}
                """);
    }

    @Test
    void testList_EventServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockEventService.listBy(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Configure EventMapper.toItemDto(...).
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventItemPayload eventItemPayload = new EventItemPayload(
                UUID.fromString("953e0262-456b-4ab4-8f65-db6791f0942a"), "title", LocalDate.of(2020, 1, 1),
                EventMode.ONLINE, EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");
        when(mockEventMapper.toItemDto(Event.builder()
                .id(UUID.fromString("145872a0-b2b0-414e-9e58-27a36825ec1a"))
                .build())).thenReturn(eventItemPayload);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/api/v1/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThatJson(response.getContentAsString()).isEqualTo("""
                {"content":[],"pageable":"INSTANCE","last":true,"totalPages":1,
                "totalElements":0,"size":0,"number":0,"sort":{"empty":true,"sorted":false,"unsorted":true},
                "first":true,"numberOfElements":0,"empty":true}
                """);
    }
}
