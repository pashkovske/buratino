package ru.pashkovske.buratino.tinkoff.service.instrument.model;

import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Quotation;

public interface InstrumentWrapper {
    InstrumentId getId();
    String getFigi();
    String getName();
    String getTicker();
    Quotation getMinPriceIncrement();
    String getCurrency();
    int getLot();
    InstrumentType getType();
}
