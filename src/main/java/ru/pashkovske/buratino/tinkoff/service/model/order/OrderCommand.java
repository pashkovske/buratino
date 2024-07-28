package ru.pashkovske.buratino.tinkoff.service.model.order;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

public interface OrderCommand<T> {
    InstrumentHolder<T> getInstrument();
    int getLotQuantity();
    OrderDirection getDirection();
}
