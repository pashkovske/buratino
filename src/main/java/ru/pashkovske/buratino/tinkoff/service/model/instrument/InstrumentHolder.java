package ru.pashkovske.buratino.tinkoff.service.model.instrument;

import ru.tinkoff.piapi.contract.v1.Quotation;

public interface InstrumentHolder<T> {
    String getFigi();
    String getName();
    String getTicker();
    Quotation getMinPriceIncrement();
}
