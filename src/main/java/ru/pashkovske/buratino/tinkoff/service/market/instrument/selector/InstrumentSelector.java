package ru.pashkovske.buratino.tinkoff.service.market.instrument.selector;

import ru.pashkovske.buratino.tinkoff.service.model.instrument.InstrumentHolder;

import java.util.List;

public interface InstrumentSelector<T> {
    InstrumentHolder<T> getByName(String name);
    List<InstrumentHolder<T>> findByName(String name);
    InstrumentHolder<T> getByTicker(String ticker);
}
