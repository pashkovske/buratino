package ru.pashkovske.buratino.tinkoff.service.instrument.model;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Quotation;


@RequiredArgsConstructor
public class FutureWrapper implements InstrumentWrapper {
    final Future future;

    @Override
    public InstrumentId getId() { return new InstrumentId(future.getUid()); }

    @Override
    public String getFigi() {
        return future.getFigi();
    }

    @Override
    public String getName() {
        return future.getName();
    }

    @Override
    public String getTicker() {
        return future.getTicker();
    }

    @Override
    public Quotation getMinPriceIncrement() {
        return future.getMinPriceIncrement();
    }

    @Override
    public String getCurrency() {
        return future.getCurrency();
    }

    @Override
    public int getLot() {
        return future.getLot();
    }

    @Override
    public InstrumentType getType() { return InstrumentType.INSTRUMENT_TYPE_FUTURES; }

    public Quotation getMinPriceIncrementAmount() { return future.getMinPriceIncrementAmount(); }
}
