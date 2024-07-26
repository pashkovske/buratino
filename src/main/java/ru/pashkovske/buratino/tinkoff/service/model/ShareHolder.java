package ru.pashkovske.buratino.tinkoff.service.model;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

@RequiredArgsConstructor
public class ShareHolder implements InstrumentHolder {
    final Share share;

    @Override
    public String getFigi() {
        return share.getFigi();
    }

    @Override
    public Quotation getMinPriceIncrement() {
        return share.getMinPriceIncrement();
    }
}
