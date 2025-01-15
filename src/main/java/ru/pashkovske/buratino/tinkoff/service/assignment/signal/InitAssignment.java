package ru.pashkovske.buratino.tinkoff.service.assignment.signal;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import ru.pashkovske.buratino.tinkoff.service.assignment.strategy.Strategy;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;

@Getter
public class InitAssignment extends ApplicationEvent {
    private final Strategy strategy;
    private final InstrumentId instrumentId;

    InitAssignment(
            Object source,
            Strategy strategy,
            InstrumentId instrumentId
    ) {
        super(source);
        this.strategy = strategy;
        this.instrumentId = instrumentId;
    }
}
