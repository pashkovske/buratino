package ru.pashkovske.buratino.tinkoff.service.order.model;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.price.model.QuotationDto;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

import javax.annotation.Nonnull;

public record OrderDto(
        @NonNull String id,
        @NonNull QuotationDto lotPrice,
        long lotsQuantity,
        @Nonnull OrderDirection direction
) {
}
