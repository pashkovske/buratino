package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.NonNull;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.List;

public interface OrderBook {
    Quotation getMaxPrice();
    Quotation getMinPrice();
    void put(@NonNull Order order);
    void putAll(@NonNull List<Order> orders);
    long getLotsQuantity(@NonNull Quotation price);
    void exclude(@NonNull Order order);
    void excludeAll(@NonNull List<Order> orders);
    int getSize();
    void clear();
}
