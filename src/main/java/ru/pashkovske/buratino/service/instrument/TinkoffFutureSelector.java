package ru.pashkovske.buratino.service.instrument;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.core.InstrumentsService;

import java.util.List;

@RequiredArgsConstructor
public class TinkoffFutureSelector implements FutureSelector {
    final InstrumentsService instrumentsService;

    @Override
    public Future getByTicker(String ticker) {
        return instrumentsService.getFuturesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream()
                .filter(future -> future.getTicker().equals(ticker))
                .findFirst()
                .orElseThrow();
    }

    @Override
    public List<Future> findByName(String name) {
        return instrumentsService.getFuturesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE).stream()
                .filter(future -> future.getName().contains(name))
                .toList();
    }
}
