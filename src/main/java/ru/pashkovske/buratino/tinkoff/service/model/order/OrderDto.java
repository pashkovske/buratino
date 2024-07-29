package ru.pashkovske.buratino.tinkoff.service.model.order;

import lombok.Builder;
import lombok.Value;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.time.Instant;

@Value
@Builder
public class OrderDto {
    String id;
    String instrumentId;
    Quotation price;
    long lotsQuantity;
    OrderDirection direction;
    OrderType type;
    Instant updateTime;
}
