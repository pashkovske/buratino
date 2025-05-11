package ru.pashkovske.buratino.tinkoff.service.price.service;

import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;

public interface PriceChangesBroker {
    void subscribe(InstrumentWrapper instrument);
}
