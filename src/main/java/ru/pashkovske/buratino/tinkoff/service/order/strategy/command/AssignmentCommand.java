package ru.pashkovske.buratino.tinkoff.service.order.strategy.command;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

public interface AssignmentCommand {
    @NonNull
    InstrumentWrapper getInstrument();
    @NonNull
    OrderDirection getDirection();
    long getLotsQuantity();
}
