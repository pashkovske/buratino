package ru.pashkovske.buratino.tinkoff.service.model;

import ru.tinkoff.piapi.contract.v1.Quotation;

public interface InstrumentHolder {
    String getFigi();
    Quotation getMinPriceIncrement();
}
