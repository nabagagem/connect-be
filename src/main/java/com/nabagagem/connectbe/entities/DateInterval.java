package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Embeddable
public class DateInterval {
    private ZonedDateTime startAt, finishAt;
}
