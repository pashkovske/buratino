package ru.pashkovske.buratino.tinkoff.service.order.strategy.command;

import lombok.Value;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

@Value
public class FollowBestSellPrice implements AssignmentCommand {
    InstrumentWrapper instrument;
    OrderDirection direction = OrderDirection.ORDER_DIRECTION_SELL;
    long lotsQuantity;
}
