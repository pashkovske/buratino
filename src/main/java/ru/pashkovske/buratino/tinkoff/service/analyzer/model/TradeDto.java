package ru.pashkovske.buratino.tinkoff.service.analyzer.model;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.price.model.QuotationDto;
import ru.tinkoff.piapi.contract.v1.TradeDirection;
import ru.tinkoff.piapi.contract.v1.TradeSourceType;

import java.time.Instant;

public record TradeDto(
        @NonNull Instant time,
        @NonNull TradeDirection direction,
        long lotsQuantity,
        @NonNull QuotationDto quotation,
        @NonNull TradeSourceType source
) {
}
