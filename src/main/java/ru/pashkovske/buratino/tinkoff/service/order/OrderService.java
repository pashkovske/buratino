package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Share;

public interface OrderService {
    void limitSellBestByOrderBook(InstrumentHolder instrument);
    void limitBuyBestByOrderBook(InstrumentHolder instrument);
}
