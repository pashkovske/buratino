package ru.pashkovske.buratino.service.order;

import ru.pashkovske.buratino.service.model.InstrumentHolder;
import ru.tinkoff.piapi.contract.v1.Future;
import ru.tinkoff.piapi.contract.v1.Share;

public interface OrderService {
    void sellBestByOrderBook(Share share);
    void buyBestByOrderBook(Share share);
    void sellBestByOrderBook(Future future);
    void buyBestByOrderBook(Future future);
}
