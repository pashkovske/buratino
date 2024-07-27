package ru.pashkovske.buratino.tinkoff.service.instrument;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.model.FutureHolder;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.core.InstrumentsService;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class FutureSelector extends AbstractInstrumentSelector<Future> {
    private final InstrumentsService instrumentsService;

    @Override
    protected Stream<Future> getInstrumentStream() {
        return instrumentsService.getFuturesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream();
    }

    @Override
    protected FutureHolder buildInstrumentHolder(Future future) {
        return new FutureHolder(future);
    }
}
