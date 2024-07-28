package ru.pashkovske.buratino.tinkoff.service.model.order;

import lombok.Builder;
import lombok.Data;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.Quotation;

@Data
@Builder
public class OrderData {
    private final String id;
    private final String instrumentId;
    private Quotation price;
    private long lotsQuantity;
    private OrderDirection direction;
    private OrderType type;
}
