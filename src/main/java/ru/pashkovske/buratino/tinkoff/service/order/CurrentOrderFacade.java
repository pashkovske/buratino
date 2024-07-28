package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.List;

public interface CurrentOrderFacade {
    List<OrderState> getOrdersByInstrument(InstrumentHolder instrument);
}
