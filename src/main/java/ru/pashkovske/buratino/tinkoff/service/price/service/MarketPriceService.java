package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.List;

public interface MarketPriceService {
    long getSpreadBasisPoints(@NonNull InstrumentWrapper instrument);
    Quotation getBestPrice(
            @NonNull InstrumentWrapper instrument,
            List<Order> excludeOrders,
            @NonNull OrderDirection direction);
    Quotation getBestPrice(
            @NonNull InstrumentWrapper instrument,
            @NonNull OrderDirection direction);
}
