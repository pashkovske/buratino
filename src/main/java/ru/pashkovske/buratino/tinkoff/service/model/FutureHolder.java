package ru.pashkovske.buratino.tinkoff.service.model;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Quotation;

@RequiredArgsConstructor
public class FutureHolder implements InstrumentHolder<Future> {
    final Future future;

    @Override
    public String getFigi() {
        return future.getFigi();
    }

    @Override
    public String getName() { return future.getName(); }

    @Override
    public String getTicker() { return future.getTicker(); }

    @Override
    public Quotation getMinPriceIncrement() {
        return future.getMinPriceIncrement();
    }
}
