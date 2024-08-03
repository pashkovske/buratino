package ru.pashkovske.buratino.tinkoff.service.instrument.selector;

import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;

import java.util.List;

public interface InstrumentSelector {
    InstrumentWrapper getByName(String name);
    List<InstrumentWrapper> findByName(String name);
    InstrumentWrapper getByTicker(String ticker);
    InstrumentWrapper getById(InstrumentId id);
}
