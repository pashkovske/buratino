package ru.pashkovske.buratino.tinkoff.service.instrument;

import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;

import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractInstrumentSelector<T> implements InstrumentSelector<T> {

    protected abstract Stream<T> getInstrumentStream();
    protected abstract InstrumentHolder<T> buildInstrumentHolder(T instrument);

    @Override
    final public InstrumentHolder<T> getByName(String name) {
        return getInstrumentStream()
                .map(this::buildInstrumentHolder)
                .filter(instrument -> instrument.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @Override
    final public List<InstrumentHolder<T>> findByName(String name) {
        return getInstrumentStream()
                .map(this::buildInstrumentHolder)
                .filter(instrument -> instrument.getName().contains(name))
                .toList();
    }

    @Override
    final public InstrumentHolder<T> getByTicker(String ticker) {
        return getInstrumentStream()
                .map(this::buildInstrumentHolder)
                .filter(instrument -> instrument.getTicker().equals(ticker))
                .findFirst()
                .orElseThrow();
    }
}
