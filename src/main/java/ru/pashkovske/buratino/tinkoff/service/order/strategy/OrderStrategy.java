package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.pashkovske.buratino.tinkoff.service.model.order.OrderCommand;

public interface OrderStrategy<T> {
    void postOrder(OrderCommand<T> command);
    void putOrder(OrderCommand<T> command);
    void deleteOrder(InstrumentHolder<T> instrument);
}
