package ru.pashkovske.buratino.service.order;

import ru.tinkoff.piapi.contract.v1.Share;

public interface OrderService {
    void sellBestByOrderBook(Share share);
    void buyBestByOrderBook(Share share);
}
