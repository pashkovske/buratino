package ru.pashkovske.buratino.tinkoff.service.model.order;

import lombok.Value;
import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

@Value
public class SimpleByeOneCommand<T> implements OrderCommand<T> {
    InstrumentHolder<T> instrument;
    int lotQuantity = 1;
    OrderDirection direction = OrderDirection.ORDER_DIRECTION_BUY;
}
