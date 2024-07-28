package ru.pashkovske.buratino.tinkoff.service.model.instrument;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

@RequiredArgsConstructor
public class ShareHolder implements InstrumentHolder<Share> {
    final Share share;

    @Override
    public String getFigi() {
        return share.getFigi();
    }

    @Override
    public String getName() { return share.getName(); }

    @Override
    public String getTicker() { return share.getTicker(); }

    @Override
    public Quotation getMinPriceIncrement() {
        return share.getMinPriceIncrement();
    }
}
