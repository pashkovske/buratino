package ru.pashkovske.buratino.tinkoff.service.instrument;

import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;

import java.util.List;

public interface InstrumentSelector<T> {
    InstrumentHolder<T> getByName(String name);
    List<InstrumentHolder<T>> findByName(String name);
    InstrumentHolder<T> getByTicker(String ticker);
}
