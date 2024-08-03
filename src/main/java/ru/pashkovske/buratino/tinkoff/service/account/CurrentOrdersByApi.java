package ru.pashkovske.buratino.tinkoff.service.account;

import lombok.RequiredArgsConstructor;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.core.OrdersService;

import java.util.List;

@RequiredArgsConstructor
public class CurrentOrdersByApi implements CurrentAccountOrders {
    private final OrdersService ordersService;
    private final String accountId;

    @Override
    public List<OrderState> getAllOrders() {
        return ordersService.getOrdersSync(accountId).stream()
                .toList();
    }
}
