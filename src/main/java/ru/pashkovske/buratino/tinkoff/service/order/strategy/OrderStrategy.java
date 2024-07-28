package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderCommand;

public interface OrderStrategy {
    void postOrder(OrderCommand command);
    void putOrder(OrderCommand command);
    void deleteOrder(InstrumentHolder instrument);
}
