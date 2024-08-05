package ru.pashkovske.buratino.tinkoff.service.instrument.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.tinkoff.piapi.contract.v1.*;

@ToString
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

    @Override
    public boolean getForQualInvestorFlag() {
        return future.getForQualInvestorFlag();
    }

    @Override
    public SecurityTradingStatus getTradingStatus() {
        return future.getTradingStatus();
    }

    @NonNull
    public Quotation getMinPriceIncrementAmount() {
        return future.getMinPriceIncrementAmount();
    }

    @NonNull
    public MoneyValue getMargin() {
        return future.getInitialMarginOnSell();
    }
}
