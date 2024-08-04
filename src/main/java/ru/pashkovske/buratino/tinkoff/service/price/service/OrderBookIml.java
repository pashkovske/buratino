package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;
import ru.tinkoff.piapi.contract.v1.Order;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBookIml implements OrderBook {
    Map<@NonNull Quotation, @NonNull Long> orders = new HashMap<>();

    @Override
    public Quotation getMaxPrice() {
        return orders.keySet().stream().max(PriceUtils.getPriceComparator()).orElse(null);
    }

    @Override
    public Quotation getMinPrice() {
        return orders.keySet().stream().min(PriceUtils.getPriceComparator()).orElse(null);
    }

    @Override
    public void put(@NonNull Order order) {
        orders.computeIfPresent(order.getPrice(), (price, lots) -> lots + order.getQuantity());
        orders.putIfAbsent(order.getPrice(), order.getQuantity());
    }

    @Override
    public void putAll(@NonNull List<Order> orders) {
        for (Order order : orders) {
            put(order);
        }
    }

    @Override
    public long getLotsQuantity(@NonNull Quotation price) {
        return orders.get(price);
    }

    @Override
    public void exclude(@NonNull Order order) {
        Quotation price = order.getPrice();
        if (orders.containsKey(price)) {
            long quantityResult = orders.get(price) - order.getQuantity();
            if (quantityResult > 0) {
                orders.replace(price, quantityResult);
            } else if (quantityResult == 0) {
                orders.remove(price);
            }
            else {
                throw new IllegalArgumentException(
                        "Попытка исключить больше лотов (" +
                                order.getQuantity() + " из " + orders.get(price) +
                                " возможных), чем есть в стакане");
            }
        }
        else {
            //throw new NullPointerException("Попытка исключиеть заказ цены которого нет в стакане: " + price);
        }
    }

    @Override
    public void excludeAll(@NonNull List<Order> orders) {
        for (Order order : orders) {
            exclude(order);
        }
    }

    @Override
    public int getSize() {
        return orders.size();
    }

    @Override
    public void clear() {
        orders.clear();
    }
}
