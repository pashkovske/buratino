package ru.pashkovske.buratino.tinkoff.service.instrument.model;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

@RequiredArgsConstructor
public class ShareWrapper implements InstrumentWrapper {
    final Share share;

    @Override
    public InstrumentId getId() { return new InstrumentId(share.getUid()); }

    @Override
    public String getFigi() {
        return share.getFigi();
    }

    @Override
    public String getName() {
        return share.getName();
    }

    @Override
    public String getTicker() {
        return share.getTicker();
    }

    @Override
    public Quotation getMinPriceIncrement() {
        return share.getMinPriceIncrement();
    }

    @Override
    public String getCurrency() {
        return share.getCurrency();
    }

    @Override
    public int getLot() {
        return share.getLot();
    }

    @Override
    public InstrumentType getType() {
        return InstrumentType.INSTRUMENT_TYPE_SHARE;
    }
}
