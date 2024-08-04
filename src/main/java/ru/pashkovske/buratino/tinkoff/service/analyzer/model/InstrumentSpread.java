package ru.pashkovske.buratino.tinkoff.service.analyzer.model;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;

import java.util.Map;

public record InstrumentSpread(
        @NonNull InstrumentWrapper instrument,
        long spreadValue
) implements Comparable<InstrumentSpread> {
    @Override
    public int compareTo(InstrumentSpread other) {
        return (int)(this.spreadValue - other.spreadValue);
    }

    @Override
    public String toString() {
        return Map.of(
                "instrument_name", instrument.getName(),
                "instrument_ticker", instrument.getTicker(),
                "instrument_type", instrument.getType(),
                "spread", spreadValue
        ).toString();
    }
}
