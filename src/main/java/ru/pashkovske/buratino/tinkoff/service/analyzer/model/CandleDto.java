package ru.pashkovske.buratino.tinkoff.service.analyzer.model;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.price.model.QuotationDto;

import java.time.Instant;

public record CandleDto(
        @NonNull QuotationDto minPrice,
        @NonNull QuotationDto maxPrice,
        @NonNull Instant time,
        long quantity
) {
}
