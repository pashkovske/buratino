package ru.pashkovske.buratino.tinkoff.service.order.model;

import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.TimeInForceType;

import javax.annotation.Nonnull;


public record OrderRequest(
        @Nonnull String instrumentId,
        @Nonnull MoneyValue price,
        @Nonnull OrderDirection direction,
        long lotsQuantity,
        @Nonnull OrderType type,
        @Nonnull TimeInForceType timeInForce
) {
}
