package ru.pashkovske.buratino.tinkoff.service.account;

import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.List;

public interface CurrentAccountOrders {
    List<OrderState> getAllOrders();
}
