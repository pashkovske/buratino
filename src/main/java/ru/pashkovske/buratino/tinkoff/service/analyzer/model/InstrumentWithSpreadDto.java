package ru.pashkovske.buratino.tinkoff.service.analyzer.model;

import ru.tinkoff.piapi.contract.v1.InstrumentType;

import javax.annotation.Nonnull;

public record InstrumentWithSpreadDto(
        @Nonnull String ticker,
        @Nonnull String name,
        @Nonnull InstrumentType type,
        long basicPtsSpread
) {
}
