package com.nabagagem.connectbe.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class MoneyAmount {
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private MoneyCurrency currency;

    public enum MoneyCurrency {
        EUR, USD, BRL
    }
}
