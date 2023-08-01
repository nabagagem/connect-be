package com.nabagagem.connectbe.services.events;

import com.nabagagem.connectbe.domain.notification.EventPayload;
import com.nabagagem.connectbe.domain.notification.EventSearchParams;
import com.nabagagem.connectbe.entities.Event;
import com.nabagagem.connectbe.entities.EventMode;
import com.nabagagem.connectbe.entities.EventType;
import com.nabagagem.connectbe.entities.MoneyAmount;
import com.nabagagem.connectbe.mappers.EventMapper;
import com.nabagagem.connectbe.repos.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WithMockUser
class EventServiceTest {

    @Mock
    private EventRepository mockEventRepository;
    @Mock
    private EventMapper mockEventMapper;

    private EventService eventServiceUnderTest;

    @BeforeEach
    void setUp() {
        eventServiceUnderTest = new EventService(mockEventRepository, mockEventMapper);
    }

    @Test
    void testCreate() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");
        final Event expectedResult = Event.builder().build();

        // Configure EventMapper.toEntity(...).
        final MoneyAmount moneyAmount1 = new MoneyAmount();
        moneyAmount1.setAmount(new BigDecimal("0.00"));
        moneyAmount1.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload1 = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount1, "externalLink", "description");
        when(mockEventMapper.toEntity(eventPayload1)).thenReturn(Event.builder().build());

        when(mockEventRepository.save(Event.builder().build())).thenReturn(Event.builder().build());

        // Run the test
        final Event result = eventServiceUnderTest.create(eventPayload);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testCreate_EventRepositoryThrowsOptimisticLockingFailureException() {
        // Setup
        final MoneyAmount moneyAmount = new MoneyAmount();
        moneyAmount.setAmount(new BigDecimal("0.00"));
        moneyAmount.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount, "externalLink", "description");

        // Configure EventMapper.toEntity(...).
        final MoneyAmount moneyAmount1 = new MoneyAmount();
        moneyAmount1.setAmount(new BigDecimal("0.00"));
        moneyAmount1.setCurrency(MoneyAmount.MoneyCurrency.EUR);
        final EventPayload eventPayload1 = new EventPayload("title", LocalDate.of(2020, 1, 1), EventMode.ONLINE,
                EventType.WORKSHOP, "address", moneyAmount1, "externalLink", "description");
        when(mockEventMapper.toEntity(eventPayload1)).thenReturn(Event.builder().build());

        when(mockEventRepository.save(Event.builder().build())).thenThrow(OptimisticLockingFailureException.class);

        // Run the test
        assertThatThrownBy(() -> eventServiceUnderTest.create(eventPayload))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void testGetById() {
        // Setup
        final Optional<Event> expectedResult = Optional.of(Event.builder().build());
        when(mockEventRepository.findById(UUID.fromString("67d1edbf-6e4e-4d00-93b3-d1991aa979a9")))
                .thenReturn(Optional.of(Event.builder().build()));

        // Run the test
        final Optional<Event> result = eventServiceUnderTest.getById(
                UUID.fromString("67d1edbf-6e4e-4d00-93b3-d1991aa979a9"));

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetById_EventRepositoryReturnsAbsent() {
        // Setup
        when(mockEventRepository.findById(UUID.fromString("67d1edbf-6e4e-4d00-93b3-d1991aa979a9")))
                .thenReturn(Optional.empty());

        // Run the test
        final Optional<Event> result = eventServiceUnderTest.getById(
                UUID.fromString("67d1edbf-6e4e-4d00-93b3-d1991aa979a9"));

        // Verify the results
        assertThat(result).isEmpty();
    }

    @Test
    void testDelete() {
        // Setup
        // Run the test
        eventServiceUnderTest.delete(UUID.fromString("b89c7392-1d27-4282-a973-924384c7d7b0"));

        // Verify the results
        verify(mockEventRepository).deleteById(UUID.fromString("b89c7392-1d27-4282-a973-924384c7d7b0"));
    }

    @Test
    void testListBy() {
        // Setup
        final EventSearchParams eventSearchParams = new EventSearchParams(List.of(EventMode.ONLINE),
                List.of(EventType.WORKSHOP), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1));

        // Configure EventRepository.listBy(...).
        Event event = Event.builder()
                .eventMode(EventMode.ONLINE)
                .eventType(EventType.WORKSHOP)
                .build();
        final Page<Event> events = new PageImpl<>(List.of(event));
        when(mockEventRepository.listBy(eq(List.of(EventMode.ONLINE)), eq(List.of(EventType.WORKSHOP)),
                eq(LocalDate.of(2020, 1, 1)), eq(LocalDate.of(2020, 1, 1)), any(Pageable.class))).thenReturn(events);

        // Run the test
        final Page<Event> result = eventServiceUnderTest.listBy(eventSearchParams, PageRequest.of(0, 1));

        // Verify the results
        assertThat(result)
                .containsExactlyInAnyOrder(event);
    }

    @Test
    void testListBy_EventRepositoryReturnsNoItems() {
        // Setup
        final EventSearchParams eventSearchParams = new EventSearchParams(List.of(EventMode.ONLINE),
                List.of(EventType.WORKSHOP), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1));
        when(mockEventRepository.listBy(eq(List.of(EventMode.ONLINE)), eq(List.of(EventType.WORKSHOP)),
                eq(LocalDate.of(2020, 1, 1)), eq(LocalDate.of(2020, 1, 1)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        // Run the test
        final Page<Event> result = eventServiceUnderTest.listBy(eventSearchParams, PageRequest.of(0, 1));

        // Verify the results
        assertThat(result).isEmpty();
    }
}
