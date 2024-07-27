package ru.pashkovske.buratino.tinkoff.service.order;

import ru.pashkovske.buratino.tinkoff.service.model.InstrumentHolder;

public interface OrderService {
    void limitSellBestByOrderBook(InstrumentHolder instrument);
    void limitBuyBestByOrderBook(InstrumentHolder instrument);
}
